package com.myetherwallet.mewconnect.content.data

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by BArtWell on 24.07.2018.
 */
data class EncryptedMessage(
        @SerializedName("ciphertext")
        val ciphertext: ByteArray,
        @SerializedName("ephemPublicKey")
        val ephemPublicKey: ByteArray,
        @SerializedName("iv")
        val iv: ByteArray,
        @SerializedName("mac")
        val mac: ByteArray
) : BaseMessage() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedMessage

        if (!Arrays.equals(ciphertext, other.ciphertext)) return false
        if (!Arrays.equals(ephemPublicKey, other.ephemPublicKey)) return false
        if (!Arrays.equals(iv, other.iv)) return false
        if (!Arrays.equals(mac, other.mac)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(ciphertext)
        result = 31 * result + Arrays.hashCode(ephemPublicKey)
        result = 31 * result + Arrays.hashCode(iv)
        result = 31 * result + Arrays.hashCode(mac)
        return result
    }
}