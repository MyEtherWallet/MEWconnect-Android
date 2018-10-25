package com.myetherwallet.mewconnect.content.data

import com.google.gson.annotations.SerializedName

/**
 * Created by BArtWell on 04.10.2018.
 */

data class MessageSignData(
        @SerializedName("address")
        private val address: String,
        @SerializedName("sig")
        private val signature: String
)