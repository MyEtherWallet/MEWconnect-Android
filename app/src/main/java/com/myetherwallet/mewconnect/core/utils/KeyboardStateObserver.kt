package com.myetherwallet.mewconnect.core.utils

import android.view.View
import kotlin.math.abs

/**
 * Created by BArtWell on 11.07.2018.
 */
class KeyboardStateObserver(resizableView: View) {

    private var prevContainerHeight = 0
    var listener: ((isShown: Boolean) -> Unit)? = null

    init {
        resizableView.viewTreeObserver.addOnGlobalLayoutListener {
            if (prevContainerHeight != 0 && abs(prevContainerHeight - resizableView.height) > 150) {
                listener?.invoke(prevContainerHeight < resizableView.height)
            }
            prevContainerHeight = resizableView.height
        }
    }
}