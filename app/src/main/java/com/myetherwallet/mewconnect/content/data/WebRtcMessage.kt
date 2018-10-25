package com.myetherwallet.mewconnect.content.data

import com.google.gson.annotations.SerializedName

/**
 * Created by BArtWell on 28.07.2018.
 */
data class WebRtcMessage<T>(
        val type: Type,
        val data: T
) : BaseMessage() {

    enum class Type {
        @SerializedName("address")
        ADDRESS,
        @SerializedName("signTx")
        SIGN_TX,
        @SerializedName("signMessage")
        SIGN_MESSAGE
    }
}