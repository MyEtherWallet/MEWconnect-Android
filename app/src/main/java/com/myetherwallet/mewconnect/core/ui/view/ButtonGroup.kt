package com.myetherwallet.mewconnect.core.ui.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.RadioGroup
import com.myetherwallet.mewconnect.R

class ButtonGroup @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RadioGroup(context, attrs) {

    private var previousCheckedPosition = -1

    init {
        orientation = RadioGroup.HORIZONTAL
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (childCount >= 2) {
            setChildDrawable(0, R.drawable.button_group_item_left_selector)
            for (i in 1..childCount - 2) {
                setChildDrawable(i, R.drawable.button_group_item_center_selector)
            }
            setChildDrawable(childCount - 1, R.drawable.button_group_item_right_selector)

            // Uncheck on second click
            for (i in 0 until childCount) {
                getChildAt(i).setOnClickListener {
                    if (previousCheckedPosition == i) {
                        clearCheck()
                        previousCheckedPosition = -1
                    } else {
                        previousCheckedPosition = i
                    }
                }
            }
        }
    }

    private fun setChildDrawable(position: Int, @DrawableRes background: Int) {
        val child = getChildAt(position) as ButtonGroupItem
        child.buttonDrawable = ColorDrawable(Color.TRANSPARENT)
        child.background = ContextCompat.getDrawable(context, background)
    }
}