package com.myetherwallet.mewconnect.feature.buy.data

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class BuyQuoteDigitalMoney(
        @SerializedName("currency")
        val currency: String,
        @SerializedName("amount")
        val amount: BigDecimal
)