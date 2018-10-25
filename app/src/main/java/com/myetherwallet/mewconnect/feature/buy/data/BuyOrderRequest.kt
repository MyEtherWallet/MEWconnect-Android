package com.myetherwallet.mewconnect.feature.buy.data

import com.google.gson.annotations.SerializedName

/**
 * Created by BArtWell on 15.09.2018.
 */

data class BuyOrderRequest(
        @SerializedName("account_details")
        val accountDetails: BuyOrderAccountDetails,
        @SerializedName("transaction_details")
        val transactionDetails: BuyOrderTransactionDetails
)
