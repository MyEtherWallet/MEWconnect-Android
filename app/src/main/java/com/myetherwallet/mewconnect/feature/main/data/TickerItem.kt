package com.myetherwallet.mewconnect.feature.main.data

import com.google.gson.annotations.SerializedName

data class TickerItem(
        @SerializedName("symbol")
        val symbol: String,
        @SerializedName("quotes")
        val quotes: TickerQuotes
)