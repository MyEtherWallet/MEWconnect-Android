package com.myetherwallet.mewconnect.feature.buy.data

import com.google.gson.annotations.SerializedName

data class BuyQuoteResult(
        @SerializedName("user_id")
        val userId: String,
        @SerializedName("quote_id")
        val quoteId: String,
        @SerializedName("digital_money")
        val digitalMoney: BuyQuoteDigitalMoney,
        @SerializedName("fiat_money")
        val fiatMoney: BuyQuoteFiatMoney
)