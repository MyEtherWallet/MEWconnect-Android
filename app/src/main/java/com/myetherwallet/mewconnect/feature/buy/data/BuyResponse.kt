package com.myetherwallet.mewconnect.feature.buy.data

import com.google.gson.annotations.SerializedName

/**
 * Created by BArtWell on 15.09.2018.
 */
data class BuyResponse<T>(
        @SerializedName("error")
        val error: Boolean,
        @SerializedName("result")
        val result: T
)