package com.myetherwallet.mewconnect.core.ui.view

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet

/**
 * Created by BArtWell on 17.10.2018.
 */
open class FixedFontTextView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        typeface = Typeface.createFromAsset(context.assets, "fonts/Roboto-Bold.ttf")
    }
}