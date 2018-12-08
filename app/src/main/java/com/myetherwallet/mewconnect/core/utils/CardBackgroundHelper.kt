package com.myetherwallet.mewconnect.core.utils

import android.content.Context
import android.graphics.*
import android.widget.ImageView
import com.myetherwallet.mewconnect.content.data.Network
import pm.gnosis.blockies.Blockies
import java.io.File
import java.io.FileOutputStream

/**
 * Created by BArtWell on 27.08.2018.
 */
class CardBackgroundHelper(private val context: Context) {

    companion object {

        fun isExists(context: Context, network: Network) = getFile(context, network).exists()

        fun remove(context: Context, network: Network) = getFile(context, network).delete()

        fun setImage(imageView: ImageView, network: Network) {
            imageView.setImageBitmap(BitmapFactory.decodeFile(getFile(imageView.context, network).absolutePath))
        }

        private fun getFile(context: Context, network: Network) = File(context.filesDir, network.name.toLowerCase() + "_card_background.png")
    }

    private var density = context.resources.displayMetrics.density
    private val canvasSize = (4500 * density).toInt()
    private val center = canvasSize / 2
    private val startPoint = Point(32f, 69f)
    private val curvePoints = listOf(
            Triple(Point(57.606125f, 68.999359f), Point(82.0f, 69.501244f), Point(82f, 42f)),
            Triple(Point(82.0f, 15.297916f), Point(64.606064f, 10.780972f), Point(42.0f, 3.0f)),
            Triple(Point(18.884848f, -4.275507f), Point(0.0f, 7.730424f), Point(0.0f, 29.0f)),
            Triple(Point(0.0f, 50.970837f), Point(6.005995f, 68.999359f), Point(32.0f, 69.0f))
    )

    fun draw(address: String, network: Network, scaledWidth: Int, scaledHeight: Int) {
        var sourcePath = Path()
        sourcePath.moveTo(startPoint.x, startPoint.y)
        for ((point1, point2, point3) in curvePoints) {
            sourcePath.cubicTo(
                    point1.x, point1.y,
                    point2.x, point2.y,
                    point3.x, point3.y)
        }

        val paths = mutableListOf<Path>()
        val sizeIdx = 29
        val totalPaths = 36
        val bounds = RectF()
        for (i in 0 until totalPaths) {
            sourcePath = Path(sourcePath)
            val rect = RectF()
            sourcePath.computeBounds(rect, true)
            var matrix = Matrix()
            matrix.setScale(1.101f, 1.101f, rect.centerX(), rect.centerY())
            sourcePath.transform(matrix)
            matrix = Matrix()
            matrix.setRotate(-4f, rect.centerX(), rect.centerY())
            sourcePath.transform(matrix)
            paths.add(sourcePath)
            if (i == sizeIdx) {
                bounds.set(rect)
            }
        }

        val solidityAddress = AddressUtils.toSolidityAddress(address)
        val blockies = Blockies.fromAddress(solidityAddress)!!
        val fillColors = arrayOf(blockies.primaryColor, blockies.backgroundColor, blockies.spotColor, blockies.backgroundColor)

        val bitmap = Bitmap.createBitmap(bounds.width().toInt(), bounds.height().toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.save()
        canvas.translate(-(canvasSize - bounds.width()) / 2, canvas.height.toFloat() + (canvasSize - bounds.height()) / 2)
        canvas.scale(1f, -1f)

        for (i in paths.size - 1 downTo 0) {
            val color = fillColors[i % fillColors.size]
            val paint = Paint()
            paint.isAntiAlias = true
            paint.color = color
            canvas.drawPath(paths[i], paint)
        }

        canvas.restore()

        val randSeed = seedFromAddress(HexUtils.withPrefix(address))

        val x = (((nextFromSeed(randSeed) + nextFromSeed(randSeed)) * 40) + 10) / 100.0
        val y = (((nextFromSeed(randSeed) + nextFromSeed(randSeed)) * 45) + 5) / 100.0

        val lastBitmap = Bitmap.createBitmap(bitmap,
                (x * (bounds.width() - scaledWidth)).toInt() / 2,
                (y * (bounds.height() - scaledHeight)).toInt() / 2,
                scaledWidth,
                scaledHeight)


        var stream: FileOutputStream? = null
        try {
            stream = FileOutputStream(getFile(context, network))
            lastBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                stream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun seedFromAddress(address: String): IntArray {
        val seed = IntArray(2)
        var i = 0
        (0 until address.length).forEach { _ ->
            seed[i % 2] = ((seed[i % 2] * 8) - seed[i % 2]) + Character.codePointAt(address, i)
            i++
        }
        return seed
    }

    private fun nextFromSeed(seed: IntArray): Double {
        val t = (seed[0] xor (seed[0] shl 11))
        seed[0] = seed[1]
        seed[1] = seed[1] xor (seed[1] shr 19) xor t xor (t shr 8)
        val t1 = Math.abs(seed[1]).toDouble()
        return t1 / Integer.MAX_VALUE
    }

    private inner class Point(xDpi: Float, yDpi: Float) {
        val x = center + xDpi * density
        val y = center - yDpi * density
    }
}