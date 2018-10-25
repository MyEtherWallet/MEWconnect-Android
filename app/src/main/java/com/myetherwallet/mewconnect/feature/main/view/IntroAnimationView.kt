package com.myetherwallet.mewconnect.feature.main.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import com.github.alexjlockwood.kyrie.*
import com.myetherwallet.mewconnect.R
import kotlinx.android.synthetic.main.view_start_animation.view.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Created by BArtWell on 22.09.2018.
 */

private const val BORDER_SIZE = 5f

class IntroAnimationView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var iconsViews: Array<ImageView?>

    private var maxProgress = 0
    private var pagesCount = 0
    private var drawable: KyrieDrawable? = null

    init {
        View.inflate(context, R.layout.view_start_animation, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val icons = arrayOf(R.drawable.intro1, R.drawable.intro2, R.drawable.intro3, null, R.drawable.intro5, R.drawable.intro6)
        iconsViews = arrayOfNulls(icons.size)
        for (i in 0 until icons.size) {
            icons[i]?.let {
                val view = ImageView(context)
                view.setImageResource(it)
                view.alpha = 0f
                intro_animation_container.addView(view)
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.width = WRAP_CONTENT
                params.height = WRAP_CONTENT
                params.gravity = Gravity.CENTER
                view.layoutParams = params
                iconsViews[i] = view
            }
        }
        iconsViews[0]?.alpha = 1f
    }

    fun setMaxProgress(max: Int) {
        maxProgress = max
    }

    fun setProgress(progress: Int, page: Int) {
        drawable?.currentPlayTime = progress.toLong()
        val onePageWidth = (maxProgress / pagesCount).toFloat()
        val percent = (progress % onePageWidth) / onePageWidth
        intro_animation_container.rotation = percent * 360

        iconsViews[page]?.alpha = 1 - percent
        if (page < pagesCount - 1) {
            iconsViews[page + 1]?.alpha = percent
        }
        if (percent == 0f) {
            for (i in 0 until iconsViews.size) {
                if (i != page) {
                    iconsViews[i]?.alpha = 0f
                }
            }
        }
    }

    fun setPagesCount(count: Int) {
        pagesCount = count
    }

    fun init() {
        val keyFrames = arrayOfNulls<Keyframe<PathData>>(pagesCount)
        val backgrounds = arrayOfNulls<Animation<Int, Int>>(pagesCount)
        val maxCorners = 4 + pagesCount * 2
        for (i in 0 until pagesCount) {
            keyFrames[i] = Keyframe.of(min(1f / pagesCount * i, 1f), PathData.parse(buildPath(100, 100, 4 + i * 2, maxCorners)))
            if (i == 0) {
                backgrounds[i] = Animation.ofArgb(Color.WHITE, Color.WHITE)
            } else {
                val color = if (i == 2) Color.TRANSPARENT else Color.WHITE
                backgrounds[i] = Animation.ofArgb(color)
            }
            backgrounds[i]?.duration((maxProgress / pagesCount).toLong())?.startDelay((maxProgress / pagesCount * i).toLong())
        }

        val imageSize = (context.resources.getDimension(R.dimen.dimen_204dp) + BORDER_SIZE * 4).toInt()
        drawable = KyrieDrawable.builder()
                .width(imageSize)
                .height(imageSize)
                .viewport(100f + BORDER_SIZE * 2, 100f + BORDER_SIZE * 2)
                .child(PathNode.builder()
                        .translateX(BORDER_SIZE)
                        .translateY(BORDER_SIZE)
                        .strokeColor(Color.WHITE)
                        .strokeWidth(BORDER_SIZE)
                        .fillColor(*backgrounds)
                        .pathData(Animation
                                .ofPathMorph(*keyFrames)
                                .duration(maxProgress.toLong())))
                .build()

        intro_animation_background.setImageDrawable(drawable)
    }

    private fun buildPath(width: Int, height: Int, numberOfCorners: Int, numberOfMorphCorners: Int): String {
        if (numberOfCorners < 2) throw IllegalStateException("Number of corners should be greater than 2")
        if (numberOfMorphCorners < numberOfCorners) throw IllegalStateException("Number of morph corners should be greater than number of corners")

        val bezierPath = mutableListOf<PathPoint>()

        val angleStep = (PI * 2.0f) / numberOfCorners
        val xRadius = width / 2.0
        val midX = xRadius
        val midY = height / 2.0
        val yRadius = -1.0 * midY

        val animationCorners = (numberOfMorphCorners - numberOfCorners) / 2

        val startAngle = PI / 2
        bezierPath.add(PathPoint("M", midX, 0.0))

        for (i in 0 until animationCorners) {
            bezierPath.add(PathPoint("L", midX, 0.0))
        }

        for (i in 1 until numberOfCorners) {
            val angle = startAngle - angleStep * i
            val x = midX + xRadius * cos(angle)
            val y = midY + yRadius * sin(angle)
            bezierPath.add(PathPoint("L", x, y))
            if (i == numberOfCorners / 2) {
                for (j in 0 until animationCorners) {
                    bezierPath.add(PathPoint("L", x, y))
                }
            }
        }

        return bezierPath.joinToString(" ") + " Z"
    }

    private class PathPoint(val command: String, val x: Double, val y: Double) {
        override fun toString(): String {
            return "$command $x,$y"
        }
    }
}