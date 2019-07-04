package com.myetherwallet.mewconnect.core.utils

import android.content.Context
import android.content.res.Resources
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager

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

    fun pxToDp(px: Float): Float {
        val metrics = Resources.getSystem().displayMetrics
        val dp = px / (metrics.densityDpi / 160f)
        return Math.round(dp).toFloat()
    }

    fun dpToPx(dp: Float): Float {
        val metrics = Resources.getSystem().displayMetrics
        val px = dp * (metrics.densityDpi / 160f)
        return Math.round(px).toFloat()
    }

    fun getStatusBarHeight(context: Context?): Int {
        context?.let {
            val resources = context.resources
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                return resources.getDimensionPixelSize(resourceId)
            }
        }
        return ApplicationUtils.dpToPx(if (VERSION.SDK_INT >= VERSION_CODES.M) 24f else 25f).toInt()
    }

    fun getToolbarMargin(view: View?) =
            if (DisplaySizeHelper.cutOut > 0)
                DisplaySizeHelper.cutOut
            else
                getStatusBarHeight(view?.context)

    fun removeAllData(context: Context?, preferences: PreferencesManager) {
        context?.let { CardBackgroundHelper.remove(it, preferences.applicationPreferences.getCurrentNetwork()) }
        preferences.applicationPreferences.removeWalletData()
        for (network in Network.values()) {
            preferences.getWalletPreferences(network).removeAllData()
        }
    }
}