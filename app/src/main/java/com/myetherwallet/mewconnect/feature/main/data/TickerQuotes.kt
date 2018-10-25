package com.myetherwallet.mewconnect.feature.main.data

import com.google.gson.annotations.SerializedName

data class TickerQuotes(
        @SerializedName("USD")
        val usd: TickerUsd
)