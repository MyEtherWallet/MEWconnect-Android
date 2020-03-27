package com.myetherwallet.mewconnect.content.data

import com.google.gson.annotations.SerializedName

/**
 * Created by BArtWell on 26.03.2020.
 */

data class AnalyticsEventsRequest(
        @SerializedName("events")
        val events: List<AnalyticsEvent>
)
