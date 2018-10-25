package com.myetherwallet.mewconnect.core.extenstion

import android.view.View
import android.view.ViewTreeObserver

/**
 * Created by BArtWell on 21.07.2018.
 */

inline fun View.getSize(crossinline callback: (view: View, width: Int, height: Int) -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            callback(this@getSize, width, height)
            viewTreeObserver.removeOnGlobalLayoutListener(this)
        }
    })
}