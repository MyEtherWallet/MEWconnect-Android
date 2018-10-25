package com.myetherwallet.mewconnect.feature.register.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import com.myetherwallet.mewconnect.core.utils.AddressUtils
import pm.gnosis.blockies.Blockies
import pm.gnosis.blockies.BlockiesPainter
import pm.gnosis.model.Solidity
import java.math.BigInteger

/**
 * Created by BArtWell on 28.08.2018.
 */

object EmoticonHelper {

    fun draw(address: String, size: Int): Bitmap {
        val solidityAddress = AddressUtils.toSolidityAddress(address)
        val blockies = Blockies.fromAddress(solidityAddress)
        val painter = BlockiesPainter()
        painter.setDimensions(size.toFloat(), size.toFloat())
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        painter.draw(canvas, blockies!!)
        return bitmap
    }
}