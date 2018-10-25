package com.myetherwallet.mewconnect.core.ui.view

import android.content.Context
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.RadioButton
import com.myetherwallet.mewconnect.R

class ButtonGroupItem : RadioButton {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onFinishInflate() {
        super.onFinishInflate()
        setTextColor(ContextCompat.getColorStateList(context, R.color.button_group_item_text_selector))
        setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_fixed_16sp))
        letterSpacing = 0.03f
        gravity = Gravity.CENTER
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
    }
}