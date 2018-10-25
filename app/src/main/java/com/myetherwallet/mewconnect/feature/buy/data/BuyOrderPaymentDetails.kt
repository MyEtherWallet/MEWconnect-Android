package com.myetherwallet.mewconnect.feature.buy.data

import com.google.gson.annotations.SerializedName

data class BuyOrderPaymentDetails(
        @SerializedName("fiat_total_amount")
        val fiatTotalAmount: Amount,
        @SerializedName("requested_digital_amount")
        val requestedDigitalAmount: Amount,
        @SerializedName("destination_wallet")
        val destinationWallet: BuyOrderPaymentDetailsAddress
)