package com.myetherwallet.mewconnect.feature.scan.view

import android.graphics.Bitmap
import com.myetherwallet.mewconnect.feature.scan.view.FrameMetadata
import java.nio.ByteBuffer

/**
 * Created by BArtWell on 06.05.2020.
 */

interface VisionImageProcessor {

    fun process(data: ByteBuffer, frameMetadata: FrameMetadata)

    fun process(bitmap: Bitmap)

    fun stop()
}
