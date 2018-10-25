package com.myetherwallet.mewconnect.feature.register.view

import android.content.Context
import android.util.AttributeSet
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.LinearLayout
import android.widget.TextView
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.ui.callback.EmptyAnimationListener

/**
 * Created by BArtWell on 06.07.2018.
 */

private const val INIT_ID = -1

class AnimatedLinearLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var strings: Array<String>
    private var currentStringId: Int = INIT_ID
    var listener: (() -> Unit)? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        orientation = VERTICAL
    }

    fun addStrings(strings: Array<String>) {
        currentStringId = INIT_ID
        this.strings = strings
        processStrings()
    }

    private fun processStrings() {
        currentStringId++
        val textView = AnimatedTextView(context)
        addView(textView)
        val layoutParams = textView.layoutParams as MarginLayoutParams
        layoutParams.topMargin = resources.getDimension(R.dimen.dimen_8dp).toInt()
        textView.layoutParams = layoutParams
        if (currentStringId >= strings.size) {
            textView.startInfiniteCaretAnimation()
            listener?.invoke()
        } else {
            if (currentStringId == 0) {
                textView.startTextAnimation(strings[currentStringId], ::processStrings)
            } else {
                textView.startCaretAndTextAnimation(strings[currentStringId], ::startFadeAnimation, ::processStrings)
            }
        }
    }

    private fun startFadeAnimation(fadeDuration: Long) {
        val textView = getChildAt(childCount - 2) as TextView
        val alphaAnimation = AlphaAnimation(1f, 0.2f).apply {
            duration = fadeDuration
            fillAfter = true
            setAnimationListener(object : EmptyAnimationListener() {
                override fun onAnimationEnd(animation: Animation) {
                    textView.text = textView.text.toString() // Remove spannable formatting
                    textView.alpha = 0.2f
                    textView.clearAnimation()
                }
            })
        }
        textView.startAnimation(alphaAnimation)
    }
}