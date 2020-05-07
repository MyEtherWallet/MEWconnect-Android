package com.myetherwallet.mewconnect.feature.scan.view

import android.app.Activity
import android.content.Context
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.PreviewCallback
import android.util.DisplayMetrics
import android.view.Surface
import android.view.SurfaceHolder
import android.view.WindowManager
import com.google.android.gms.common.images.Size
import com.myetherwallet.mewconnect.core.utils.MewLog
import java.io.IOException
import java.nio.ByteBuffer
import java.util.*

/**
 * Created by BArtWell on 06.05.2020.
 */

private const val REQUESTED_FPS = 30.0f
private const val DUMMY_TEXTURE_NAME = 100
private const val ASPECT_RATIO_TOLERANCE = 0.01f
private const val IMAGE_FORMAT = ImageFormat.NV21
private const val DEFAULT_REQUESTED_CAMERA_PREVIEW_WIDTH = 480
private const val DEFAULT_REQUESTED_CAMERA_PREVIEW_HEIGHT = 360
private const val TAG = "CameraSource"

class CameraSource(private var activity: Activity) {

    private var camera: Camera? = null
    private var rotation = 0
    var previewSize: Size? = null
        private set
    private val requestedAutoFocus = true

    // These instances need to be held onto to avoid GC of their underlying resources.  Even though
    // these aren't used outside of the method that creates them, they still must have hard
    // references maintained to them.
    private var dummySurfaceTexture: SurfaceTexture? = null

    // True if a SurfaceTexture is being used for the preview, false if a SurfaceHolder is being
    // used for the preview.  We want to be compatible back to Gingerbread, but SurfaceTexture
    // wasn't introduced until Honeycomb.  Since the interface cannot use a SurfaceTexture, if the
    // developer wants to display a preview we must use a SurfaceHolder.  If the developer doesn't
    // want to display a preview we use a SurfaceTexture if we are running at least Honeycomb.
    private var usingSurfaceTexture = false

    private var processingThread: Thread? = null
    private val processingRunnable = FrameProcessingRunnable()
    private val processorLock = Any()

    private var frameProcessor: VisionImageProcessor? = null
    private val bytesToByteBuffer: MutableMap<ByteArray, ByteBuffer> = IdentityHashMap()

    fun release() {
        synchronized(processorLock) {
            stop()
            processingRunnable.release()
            if (frameProcessor != null) {
                frameProcessor?.stop()
            }
        }
    }

    @Synchronized
    fun start(): CameraSource {
        if (camera != null) {
            return this
        }
        camera = createCamera()
        dummySurfaceTexture = SurfaceTexture(DUMMY_TEXTURE_NAME)
        camera!!.setPreviewTexture(dummySurfaceTexture)
        usingSurfaceTexture = true
        camera!!.startPreview()
        processingThread = Thread(processingRunnable)
        processingRunnable.setActive(true)
        processingThread!!.start()
        return this
    }

    @Synchronized
    fun start(surfaceHolder: SurfaceHolder?): CameraSource {
        camera?.let {
            return this
        }
        camera = createCamera()
        camera!!.setPreviewDisplay(surfaceHolder)
        camera!!.startPreview()
        processingThread = Thread(processingRunnable)
        processingRunnable.setActive(true)
        processingThread!!.start()
        usingSurfaceTexture = false
        return this
    }

    @Synchronized
    fun stop() {
        processingRunnable.setActive(false)
        if (processingThread != null) {
            try {
                processingThread!!.join()
            } catch (e: InterruptedException) {
                MewLog.d(TAG, "Frame processing thread interrupted on release.")
            }
            processingThread = null
        }
        if (camera != null) {
            camera!!.stopPreview()
            camera!!.setPreviewCallbackWithBuffer(null)
            try {
                if (usingSurfaceTexture) {
                    camera!!.setPreviewTexture(null)
                } else {
                    camera!!.setPreviewDisplay(null)
                }
            } catch (e: Exception) {
                MewLog.e(TAG, "Failed to clear camera preview: $e")
            }
            camera!!.release()
            camera = null
        }
        bytesToByteBuffer.clear()
    }

    private fun createCamera(): Camera {
        val requestedCameraId = getCameraId()
        if (requestedCameraId == -1) {
            throw IOException("Could not find requested camera.")
        }
        val camera = Camera.open(requestedCameraId)
        val metrics = DisplayMetrics()
        activity.windowManager?.defaultDisplay?.getMetrics(metrics)
        var sizePair = selectSizePair(camera, metrics.heightPixels, metrics.widthPixels)
        if (sizePair == null) {
            sizePair = selectSizePair(camera, DEFAULT_REQUESTED_CAMERA_PREVIEW_WIDTH, DEFAULT_REQUESTED_CAMERA_PREVIEW_HEIGHT)
        }
        if (sizePair == null) {
            throw IOException("Could not find suitable preview size.")
        }
        previewSize = sizePair.preview
        MewLog.v(TAG, "Camera preview size: $previewSize")
        val previewFpsRange = selectPreviewFpsRange(camera) ?: throw IOException("Could not find suitable preview frames per second range.")
        val parameters = camera.parameters
        val pictureSize = sizePair.picture
        if (pictureSize != null) {
            MewLog.v(TAG, "Camera picture size: $pictureSize")
            parameters.setPictureSize(pictureSize.width, pictureSize.height)
        }
        parameters.setPreviewSize(previewSize!!.width, previewSize!!.height)
        parameters.setPreviewFpsRange(previewFpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX], previewFpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX])
        // Use YV12 so that we can exercise YV12->NV21 auto-conversion logic for OCR detection
        parameters.previewFormat = IMAGE_FORMAT
        setRotation(camera, parameters, requestedCameraId)
        if (requestedAutoFocus) {
            if (parameters
                            .supportedFocusModes
                            .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)
            ) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
            } else {
                MewLog.i(TAG, "Camera auto focus is not supported on this device.")
            }
        }
        camera.parameters = parameters

        // Four frame buffers are needed for working with the camera:
        //
        //   one for the frame that is currently being executed upon in doing detection
        //   one for the next pending frame to process immediately upon completing detection
        //   two for the frames that the camera uses to populate future preview images
        //
        // Through trial and error it appears that two free buffers, in addition to the two buffers
        // used in this code, are needed for the camera to work properly.  Perhaps the camera has
        // one thread for acquiring images, and another thread for calling into user code.  If only
        // three buffers are used, then the camera will spew thousands of warning messages when
        // detection takes a non-trivial amount of time.
        camera.setPreviewCallbackWithBuffer(CameraPreviewCallback())
        camera.addCallbackBuffer(createPreviewBuffer(previewSize))
        camera.addCallbackBuffer(createPreviewBuffer(previewSize))
        camera.addCallbackBuffer(createPreviewBuffer(previewSize))
        camera.addCallbackBuffer(createPreviewBuffer(previewSize))
        return camera
    }

    class SizePair {
        val preview: Size
        val picture: Size?

        internal constructor(previewSize: Camera.Size, pictureSize: Camera.Size?) {
            preview = Size(previewSize.width, previewSize.height)
            picture = if (pictureSize != null) Size(pictureSize.width, pictureSize.height) else null
        }
    }

    private fun setRotation(camera: Camera, parameters: Camera.Parameters, cameraId: Int) {
        val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var degrees = 0
        val rotation = windowManager.defaultDisplay.rotation
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
            else -> MewLog.e(TAG, "Bad rotation value: $rotation")
        }
        val cameraInfo = CameraInfo()
        Camera.getCameraInfo(cameraId, cameraInfo)
        val angle = (cameraInfo.orientation - degrees + 360) % 360

        // This corresponds to the rotation constants.
        this.rotation = angle / 90
        MewLog.d(TAG, "Display rotation is: $rotation")
        MewLog.d(TAG, "Camera face is: " + cameraInfo.facing)
        MewLog.d(TAG, "Camera rotation is: " + cameraInfo.orientation)
        MewLog.d(TAG, "Rotation is: " + this.rotation)
        camera.setDisplayOrientation(angle)
        parameters.setRotation(angle)
    }

    private fun createPreviewBuffer(previewSize: Size?): ByteArray {
        val bitsPerPixel = ImageFormat.getBitsPerPixel(IMAGE_FORMAT)
        val sizeInBits = previewSize!!.height.toLong() * previewSize.width * bitsPerPixel
        val bufferSize = Math.ceil(sizeInBits / 8.0).toInt() + 1

        val byteArray = ByteArray(bufferSize)
        val buffer = ByteBuffer.wrap(byteArray)
        check(!(!buffer.hasArray() || !buffer.array().contentEquals(byteArray))) {
            // I don't think that this will ever happen.  But if it does, then we wouldn't be
            // passing the preview content to the underlying detector later.
            "Failed to create valid buffer for camera source."
        }
        bytesToByteBuffer[byteArray] = buffer
        return byteArray
    }

    private inner class CameraPreviewCallback : PreviewCallback {
        override fun onPreviewFrame(data: ByteArray, camera: Camera) {
            processingRunnable.setNextFrame(data, camera)
        }
    }

    fun setMachineLearningFrameProcessor(processor: VisionImageProcessor?) {
        synchronized(processorLock) {
            if (frameProcessor != null) {
                frameProcessor?.stop()
            }
            frameProcessor = processor
        }
    }

    private inner class FrameProcessingRunnable internal constructor() : Runnable {
        private val lock = Object()
        private var active = true
        private var pendingFrameData: ByteBuffer? = null

        fun release() {
            assert(processingThread?.state == Thread.State.TERMINATED)
        }

        fun setActive(active: Boolean) {
            synchronized(lock) {
                this.active = active
                lock.notifyAll()
            }
        }

        fun setNextFrame(data: ByteArray, camera: Camera) {
            synchronized(lock) {
                if (pendingFrameData != null) {
                    camera.addCallbackBuffer(pendingFrameData!!.array())
                    pendingFrameData = null
                }
                if (!bytesToByteBuffer.containsKey(data)) {
                    MewLog.d(TAG, "Skipping frame. Could not find ByteBuffer associated with the image data from the camera.")
                    return
                }
                pendingFrameData = bytesToByteBuffer[data]

                // Notify the processor thread if it is waiting on the next frame (see below).
                lock.notifyAll()
            }
        }

        override fun run() {
            var data: ByteBuffer?
            while (true) {
                synchronized(lock) {
                    while (active && (pendingFrameData == null)) {
                        try {
                            // Wait for the next frame to be received from the camera, since we
                            // don't have it yet.
                            lock.wait()
                        } catch (e: InterruptedException) {
                            MewLog.d(TAG, "Frame processing loop terminated.", e)
                            return
                        }
                    }
                    if (!active) {
                        return
                    }

                    data = pendingFrameData
                    pendingFrameData = null
                }

                try {
                    synchronized(processorLock) {
                        MewLog.d(TAG, "Process an image")
                        frameProcessor?.process(
                                data!!,
                                FrameMetadata.Builder()
                                        .setWidth(previewSize!!.width)
                                        .setHeight(previewSize!!.height)
                                        .setRotation(rotation)
                                        .setCameraFacing(CameraInfo.CAMERA_FACING_BACK)
                                        .build()
                        )
                    }
                } catch (t: Exception) {
                    MewLog.e(TAG, "Exception thrown from receiver.", t)
                } finally {
                    camera!!.addCallbackBuffer(data!!.array())
                }
            }
        }
    }

    companion object {
        private fun getCameraId(): Int {
            val cameraInfo = CameraInfo()
            for (i in 0 until Camera.getNumberOfCameras()) {
                Camera.getCameraInfo(i, cameraInfo)
                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                    return i
                }
            }
            return -1
        }

        fun selectSizePair(camera: Camera, desiredWidth: Int, desiredHeight: Int): SizePair? {
            val validPreviewSizes = generateValidPreviewSizeList(camera)
            var selectedPair: SizePair? = null
            var minDiff = Int.MAX_VALUE
            for (sizePair in validPreviewSizes) {
                val size = sizePair.preview
                val diff = Math.abs(size.width - desiredWidth) + Math.abs(size.height - desiredHeight)
                if (diff < minDiff) {
                    selectedPair = sizePair
                    minDiff = diff
                }
            }
            return selectedPair
        }

        private fun generateValidPreviewSizeList(camera: Camera): List<SizePair> {
            val parameters = camera.parameters
            val supportedPreviewSizes = parameters.supportedPreviewSizes
            val supportedPictureSizes = parameters.supportedPictureSizes
            val validPreviewSizes: MutableList<SizePair> = ArrayList()
            for (previewSize in supportedPreviewSizes) {
                val previewAspectRatio = previewSize.width.toFloat() / previewSize.height.toFloat()

                // By looping through the picture sizes in order, we favor the higher resolutions.
                // We choose the highest resolution in order to support taking the full resolution
                // picture later.
                for (pictureSize in supportedPictureSizes) {
                    val pictureAspectRatio = pictureSize.width.toFloat() / pictureSize.height.toFloat()
                    if (Math.abs(previewAspectRatio - pictureAspectRatio) < ASPECT_RATIO_TOLERANCE) {
                        validPreviewSizes.add(SizePair(previewSize, pictureSize))
                        break
                    }
                }
            }

            // If there are no picture sizes with the same aspect ratio as any preview sizes, allow all
            // of the preview sizes and hope that the camera can handle it.  Probably unlikely, but we
            // still account for it.
            if (validPreviewSizes.size == 0) {
                MewLog.w(TAG, "No preview sizes have a corresponding same-aspect-ratio picture size")
                for (previewSize in supportedPreviewSizes) {
                    // The null picture size will let us know that we shouldn't set a picture size.
                    validPreviewSizes.add(SizePair(previewSize, null))
                }
            }
            return validPreviewSizes
        }

        private fun selectPreviewFpsRange(camera: Camera): IntArray? {
            val desiredPreviewFpsScaled = (REQUESTED_FPS * 1000.0f).toInt()
            var selectedFpsRange: IntArray? = null
            var minDiff = Int.MAX_VALUE
            val previewFpsRangeList = camera.parameters.supportedPreviewFpsRange
            for (range in previewFpsRangeList) {
                val deltaMin = desiredPreviewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]
                val deltaMax = desiredPreviewFpsScaled - range[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
                val diff = Math.abs(deltaMin) + Math.abs(deltaMax)
                if (diff < minDiff) {
                    selectedFpsRange = range
                    minDiff = diff
                }
            }
            return selectedFpsRange
        }
    }
}
