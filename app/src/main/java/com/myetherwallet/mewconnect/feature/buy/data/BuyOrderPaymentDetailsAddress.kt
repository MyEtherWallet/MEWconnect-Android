package com.myetherwallet.mewconnect.feature.buy.data

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class BuyOrderPaymentDetailsAddress(
        @SerializedName("currency")
        val currency: String,
        @SerializedName("address")
        val address: String
)