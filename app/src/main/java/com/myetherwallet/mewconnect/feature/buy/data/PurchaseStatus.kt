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
        val STATUS_IN_PROGRESS = arrayOf("payment_request_submitted", "pending_simplexcc_approval")
        val STATUS_APPROVED = arrayOf("payment_simplexcc_approved", "pending_simplexcc_payment_to_partner")
        val STATUS_DECLINED = arrayOf("payment_simplexcc_declined", "simplexcc_declined")
    }
}