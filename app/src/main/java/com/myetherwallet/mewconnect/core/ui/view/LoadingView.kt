package com.myetherwallet.mewconnect.core.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.myetherwallet.mewconnect.R

/**
 * Created by BArtWell on 19.09.2018.
 */
class LoadingView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_loading, this)
    }
}