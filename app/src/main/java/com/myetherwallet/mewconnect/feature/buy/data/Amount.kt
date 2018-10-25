package com.myetherwallet.mewconnect.feature.buy.data

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Amount(
        @SerializedName("currency")
        val currency: String,
        @SerializedName("amount")
        val amount: BigDecimal
)