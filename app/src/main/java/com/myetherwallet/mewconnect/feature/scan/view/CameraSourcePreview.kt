package com.myetherwallet.mewconnect.feature.scan.view

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.myetherwallet.mewconnect.core.utils.MewLog
import java.io.IOException

/**
 * Created by BArtWell on 06.05.2020.
 */

private const val TAG = "CameraSourcePreview"

class CameraSourcePreview(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private val surfaceView: SurfaceView
    private var startRequested: Boolean
    private var surfaceAvailable: Boolean
    private var cameraSource: CameraSource? = null

    init {
        startRequested = false
        surfaceAvailable = false
        surfaceView = SurfaceView(context)
        surfaceView.holder.addCallback(SurfaceCallback())
        addView(surfaceView)
        val layoutParams = surfaceView.layoutParams
        layoutParams.width = MATCH_PARENT
        layoutParams.height = MATCH_PARENT
        surfaceView.layoutParams = layoutParams
    }

    fun start(cameraSource: CameraSource?) {
        this.cameraSource = cameraSource
        if (this.cameraSource != null) {
            startRequested = true
            startIfReady()
        }
    }

    fun stop() {
        cameraSource?.stop()
    }

    fun release() {
        cameraSource?.release()
        cameraSource = null
        surfaceView.holder.surface.release()
    }

    private fun startIfReady() {
        if (startRequested && surfaceAvailable) {
            cameraSource!!.start(surfaceView.holder)
            requestLayout()
            startRequested = false
        }
    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(surface: SurfaceHolder) {
            surfaceAvailable = true
            try {
                startIfReady()
            } catch (e: IOException) {
                MewLog.e(TAG, "Could not start camera source", e)
            }
        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            surfaceAvailable = false
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
