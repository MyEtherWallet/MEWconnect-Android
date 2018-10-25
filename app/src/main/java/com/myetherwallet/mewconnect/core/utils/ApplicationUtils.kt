package com.myetherwallet.mewconnect.core.utils

import android.app.Activity
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.support.annotation.AttrRes
import android.util.DisplayMetrics
import android.util.Size
import android.util.TypedValue
import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import kotlin.math.ceil


/**
 * Created by BArtWell on 21.08.2018.
 */
object ApplicationUtils {

    fun getAttrDimension(context: Context, @AttrRes resId: Int): Int {
        val typedValue = TypedValue()
        if (context.theme.resolveAttribute(resId, typedValue, true)) {
            return TypedValue.complexToDimensionPixelSize(typedValue.data, context.resources.displayMetrics)
        }
        return 0
    }

    fun getDisplaySize(context: Context): Size {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager
                .defaultDisplay
                .getMetrics(displayMetrics)
        return Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    fun getStatusBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            val heightInDp = if (VERSION.SDK_INT >= VERSION_CODES.M) 24 else 25
            ceil(heightInDp * resources.displayMetrics.density).toInt()
        }
    }

    fun removeAllData(context: Context?, preferences: PreferencesManager) {
        context?.let { CardBackgroundHelper.remove(it, preferences.applicationPreferences.getCurrentNetwork()) }
        preferences.applicationPreferences.removeWalletData()
        for (network in Network.values()) {
            preferences.getWalletPreferences(network).removeAllData()
        }
    }
}