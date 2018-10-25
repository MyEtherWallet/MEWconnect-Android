package com.myetherwallet.mewconnect.feature.buy.data

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class BuyQuoteFiatMoney(
        @SerializedName("currency")
        val currency: String,
        @SerializedName("base_amount")
        val baseAmount: BigDecimal
)