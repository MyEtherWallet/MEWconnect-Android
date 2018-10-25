package com.myetherwallet.mewconnect.feature.main.data

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class TickerUsd(
        @SerializedName("price")
        val price: BigDecimal
)