package com.myetherwallet.mewconnect.feature.buy.data

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

/**
 * Created by BArtWell on 15.09.2018.
 */

data class BuyQuoteRequest(
        @SerializedName("requested_currency")
        val requestedCurrency: String,
        @SerializedName("requested_amount")
        val requestedAmount: BigDecimal,
        @SerializedName("digital_currency")
        val digitalCurrency: String = "ETH",
        @SerializedName("fiat_currency")
        val fiatCurrency: String = "USD"
)
