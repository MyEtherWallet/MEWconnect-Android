package com.myetherwallet.mewconnect.feature.buy.data

import com.google.gson.annotations.SerializedName
import java.util.*

data class BuyOrderAccountDetails(
        @SerializedName("app_end_user_id")
        val appEndUserId: String,
        @SerializedName("app_install_date")
        val app_install_date: Date
)