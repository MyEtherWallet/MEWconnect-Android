package com.myetherwallet.mewconnect.feature.register.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.ui.callback.EmptyAnimatorListener
import com.myetherwallet.mewconnect.core.utils.StringUtils


private const val SYMBOL_TYPING_DURATION = 23L
private const val CARET_ANIMATION_DURATION = 375L
private const val CARET_ANIMATION_REPEAT = 3

class AnimatedTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {

    private val caretDrawable = ContextCompat.getDrawable(context, R.drawable.generating_caret)!!

    init {
        setTextColor(ContextCompat.getColor(context, R.color.text_black))
        setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_fixed_34sp))
        typeface = Typeface.createFromAsset(context.assets, "fonts/Roboto-Bold.ttf")
        setLastSymbolColor("")
    }

    fun startTextAnimation(string: String, listener: () -> Unit) {
        val spanned = StringUtils.fromHtml(string)
        val length = spanned.length
        ValueAnimator.ofInt(0, length).apply {
            duration = SYMBOL_TYPING_DURATION * length
            interpolator = LinearInterpolator()
            addUpdateListener {
                setLastSymbolColor(spanned.substring(0, it.animatedValue as Int))
            }
            addListener(object : EmptyAnimatorListener() {
                override fun onAnimationEnd(animator: Animator) {
                    Handler().postDelayed({
                        text = spanned
                        listener.invoke()
                    }, SYMBOL_TYPING_DURATION)
                }
            })
            start()
        }
    }

    fun startInfiniteCaretAnimation() {
        ValueAnimator.ofInt(0, 255).apply {
            duration = CARET_ANIMATION_DURATION
            repeatMode = REVERSE
            repeatCount = INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener {
                setLastSymbolColor(text.toString(), it.animatedValue as Int)
            }
            start()
        }
    }

    fun startCaretAndTextAnimation(string: String, caretListener: (fadeDuration: Long) -> Unit, textListener: () -> Unit) {
        Handler().postDelayed({ caretListener.invoke(CARET_ANIMATION_DURATION) }, getCaretListenerDelay())
        playCaretAnimation({
            startTextAnimation(string, textListener)
        })
    }

    private fun getCaretListenerDelay() = CARET_ANIMATION_DURATION * CARET_ANIMATION_REPEAT * 2 - CARET_ANIMATION_DURATION

    private fun playCaretAnimation(listener: () -> Unit, i: Int = 0, isReverse: Boolean = false) {
        // Create two different Animator because reverse() seems doesn't works
        val animator = if (isReverse) {
            ValueAnimator.ofInt(255, 0)
        } else {
            ValueAnimator.ofInt(0, 255)
        }
        animator.apply {
            duration = CARET_ANIMATION_DURATION
            interpolator = LinearInterpolator()
            addUpdateListener {
                setLastSymbolColor(text.toString(), it.animatedValue as Int)
            }
            addListener(object : EmptyAnimatorListener() {
                override fun onAnimationEnd(animator: Animator) {
                    if (i < CARET_ANIMATION_REPEAT * 2 - 1) {
                        playCaretAnimation(listener, i + 1, i % 2 == 0)
                    } else {
                        listener.invoke()
                    }
                }
            })
            start()
        }
    }

    private fun setLastSymbolColor(string: String, @ColorInt alpha: Int = 255) {
        val stringWithSpace = string.trim() + "\t"
        val spannableStringBuilder = SpannableStringBuilder()
        val spannableString = SpannableString(stringWithSpace)
        val caret = caretDrawable.mutate()
        caret.alpha = alpha
        caret.setBounds(0, 0, caret.intrinsicWidth, caret.intrinsicHeight)
        val span = ImageSpan(caret, ImageSpan.ALIGN_BOTTOM)
        spannableString.setSpan(span, stringWithSpace.length - 1, stringWithSpace.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        spannableStringBuilder.append(spannableString)
        text = spannableString
    }
}
