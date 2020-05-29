package com.myetherwallet.mewconnect.feature.scan.view

import android.graphics.Bitmap
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import java.nio.ByteBuffer

/**
 * Created by BArtWell on 06.05.2020.
 */

abstract class VisionProcessorBase<T> : VisionImageProcessor {

    private var latestImage: ByteBuffer? = null
    private var latestImageMetaData: FrameMetadata? = null
    private var processingImage: ByteBuffer? = null
    private var processingMetaData: FrameMetadata? = null

    @Synchronized
    override fun process(data: ByteBuffer, frameMetadata: FrameMetadata) {
        latestImage = data
        latestImageMetaData = frameMetadata
        if (processingImage == null && processingMetaData == null) {
            processLatestImage()
        }
    }

    override fun process(bitmap: Bitmap) {
        detectInVisionImage(FirebaseVisionImage.fromBitmap(bitmap))
    }

    @Synchronized
    private fun processLatestImage() {
        processingImage = latestImage
        processingMetaData = latestImageMetaData
        latestImage = null
        latestImageMetaData = null
        if (processingImage != null && processingMetaData != null) {
            processImage(processingImage!!, processingMetaData!!)
        }
    }

    private fun processImage(data: ByteBuffer, frameMetadata: FrameMetadata) {
        val metadata = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setWidth(frameMetadata.width)
            .setHeight(frameMetadata.height)
            .setRotation(frameMetadata.rotation)
            .build()
        detectInVisionImage(FirebaseVisionImage.fromByteBuffer(data, metadata))
    }

    private fun detectInVisionImage(image: FirebaseVisionImage) {
        detectInImage(image)
            .addOnSuccessListener { results ->
                onSuccess(results)
                processLatestImage()
            }
            .addOnFailureListener { e -> onFailure(e) }
    }

    override fun stop() {}

    protected abstract fun detectInImage(image: FirebaseVisionImage): Task<T>

    protected abstract fun onSuccess(results: T)

    protected abstract fun onFailure(e: Exception)
}
