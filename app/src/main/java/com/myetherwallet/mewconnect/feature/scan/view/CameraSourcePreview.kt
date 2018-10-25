package com.myetherwallet.mewconnect.feature.scan.view

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import com.google.android.gms.vision.CameraSource
import com.myetherwallet.mewconnect.core.utils.MewLog
import java.io.IOException

/**
 * Created by BArtWell on 14.07.2018.
 */

private const val TAG = "CameraSourcePreview"

class CameraSourcePreview(context: Context, attrs: AttributeSet) : ViewGroup(context, attrs) {

    private val surfaceView: SurfaceView = SurfaceView(context)
    private var isStartRequested: Boolean = false
    private var isSurfaceAvailable: Boolean = false
    private var cameraSource: CameraSource? = null

    init {
        surfaceView.holder.addCallback(SurfaceCallback())
        addView(surfaceView)
    }

    fun start(cameraSource: CameraSource?) {
        if (cameraSource == null) {
            stop()
        }

        this.cameraSource = cameraSource

        if (this.cameraSource != null) {
            isStartRequested = true
            startIfReady()
        }
    }

    fun stop() {
        cameraSource?.stop()
    }

    fun release() {
        cameraSource?.let {
            it.release()
            cameraSource = null
        }
    }

    private fun startIfReady() {
        if (isStartRequested && isSurfaceAvailable) {
            @Suppress("MissingPermission")
            cameraSource?.start(surfaceView.holder)
            isStartRequested = false
            layout()
        }
    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(surface: SurfaceHolder) {
            isSurfaceAvailable = true
            try {
                startIfReady()
            } catch (se: SecurityException) {
                MewLog.e(TAG, "Do not have permission to start the camera", se)
            } catch (e: IOException) {
                MewLog.e(TAG, "Could not start camera source.", e)
            }
        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            isSurfaceAvailable = false
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        layout()
    }

    private fun layout() {
        var previewWidth = 240
        var previewHeight = 320

        cameraSource?.let {
            val size = it.previewSize
            if (size != null) {
                previewWidth = size.height
                previewHeight = size.width
            }
        }

        val viewWidth = right - left
        val viewHeight = bottom - top

        val childWidth: Int
        val childHeight: Int
        var childXOffset = 0
        var childYOffset = 0
        val widthRatio = viewWidth.toFloat() / previewWidth.toFloat()
        val heightRatio = viewHeight.toFloat() / previewHeight.toFloat()

        if (widthRatio > heightRatio) {
            childWidth = viewWidth
            childHeight = (previewHeight.toFloat() * widthRatio).toInt()
            childYOffset = (childHeight - viewHeight) / 2
        } else {
            childWidth = (previewWidth.toFloat() * heightRatio).toInt()
            childHeight = viewHeight
            childXOffset = (childWidth - viewWidth) / 2
        }

        for (i in 0 until childCount) {
            getChildAt(i).layout(-1 * childXOffset, -1 * childYOffset, childWidth - childXOffset, childHeight - childYOffset)
        }

        try {
            startIfReady()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
