package com.myetherwallet.mewconnect.feature.scan.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

/**
 * Created by BArtWell on 14.08.2018.
 */
object VibrateUtils {

    fun vibrate(context: Context?) {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(500)
        }
    }
}