package com.myetherwallet.mewconnect.core.ui.callback

import android.view.animation.Animation

/**
 * Created by BArtWell on 06.07.2018.
 */
open class EmptyAnimationListener : Animation.AnimationListener {

    override fun onAnimationRepeat(animation: Animation) {}

    override fun onAnimationEnd(animation: Animation) {}

    override fun onAnimationStart(animation: Animation) {}
}