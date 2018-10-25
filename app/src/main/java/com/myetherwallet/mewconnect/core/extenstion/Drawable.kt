package com.myetherwallet.mewconnect.core.extenstion

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.support.annotation.ColorInt

/**
 * Created by BArtWell on 22.08.2018.
 */

fun Drawable.overrideColor(@ColorInt colorInt: Int) : Drawable {
    when (this) {
        is GradientDrawable -> setColor(colorInt)
        is ShapeDrawable -> paint.color = colorInt
        is ColorDrawable -> color = colorInt
    }
    return this
}