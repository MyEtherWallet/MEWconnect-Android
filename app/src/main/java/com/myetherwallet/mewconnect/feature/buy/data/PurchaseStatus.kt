package com.myetherwallet.mewconnect.feature.buy.data

import com.google.gson.annotations.SerializedName

data class PurchaseStatus(
        @SerializedName("user_id")
        val userId: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("fiat_total_amount")
        val fiatTotalAmount: Amount,
        @SerializedName("requested_digital_amount")
        val requestedDigitalAmount: Amount
) {
    companion object {
        const val STATUS_IN_PROGRESS = "in progress"
        const val STATUS_APPROVED = "approved"
        const val STATUS_DECLINED = "declined"
    }
}