package com.myetherwallet.mewconnect.core.ui.callback

import android.animation.Animator

/**
 * Created by BArtWell on 06.07.2018.
 */
open class EmptyAnimatorListener : Animator.AnimatorListener {

    override fun onAnimationRepeat(animator: Animator) {}

    override fun onAnimationEnd(animator: Animator) {}

    override fun onAnimationCancel(animator: Animator) {}

    override fun onAnimationStart(animator: Animator) {}
}