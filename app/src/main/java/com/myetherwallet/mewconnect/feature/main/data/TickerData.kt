package com.myetherwallet.mewconnect.feature.main.data

import com.google.gson.annotations.SerializedName

data class TickerData(
        @SerializedName("data")
        val data : Map<String, TickerItem>
)