package com.myetherwallet.mewconnect.core.utils

import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager

/**
 * Created by BArtWell on 28.05.2019.
 */

object DisplaySizeHelper {

    var width = 0
        private set
    var height = 0
        private set
    var cutOut = 0
        private set
    private val handler = Handler()

    fun setup(view: View) {
        val displayMetrics = DisplayMetrics()
        (view.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay
                .getMetrics(displayMetrics)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            handler.postDelayed({
                cutOut = getCutOut(view)
                width = displayMetrics.widthPixels
                height = displayMetrics.heightPixels + cutOut
            }, 500)
        } else {
            width = displayMetrics.widthPixels
            height = displayMetrics.heightPixels
        }
    }

    private fun getCutOut(view: View): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            view.rootWindowInsets?.displayCutout?.let {
                if (it.boundingRects.isNotEmpty()) {
                    return it.boundingRects[0].height()
                }
            }
        }
        return 0
    }

    fun init() {}
}