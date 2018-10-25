package com.myetherwallet.mewconnect.feature.buy.data

import com.google.gson.annotations.SerializedName

class BuyOrderTransactionDetails(
        @SerializedName("payment_details")
        val paymentDetails: BuyOrderPaymentDetails
)