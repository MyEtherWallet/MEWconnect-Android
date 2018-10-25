package com.myetherwallet.mewconnect.content.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by BArtWell on 30.09.2018.
 */

@Parcelize
data class MessageToSign(
        @SerializedName("hash")
        val hash: String,
        @SerializedName("text")
        val text: String
) : Parcelable