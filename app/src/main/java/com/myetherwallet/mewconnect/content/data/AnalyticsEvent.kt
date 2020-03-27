package com.myetherwallet.mewconnect.content.data

import java.util.*

/**
 * Created by BArtWell on 26.03.2020.
 */

data class AnalyticsEvent(
        val id: String,
        val timestamp: Date = Date()
) {

    companion object {
        val BANNER_SHOWN = "Android-MEWconnectApp-Banner-shown"
        val BANNER_FREE_UPGRADE_CLICKED = "Android-MEWconnectApp-Banner-FreeUpgrade-Clicked"
    }
}
