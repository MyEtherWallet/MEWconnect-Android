package com.myetherwallet.mewconnect.core.utils

import com.google.android.gms.common.util.Hex
import java.math.BigInteger

/**
 * Created by BArtWell on 02.09.2018.
 */

private const val PREFIX = "0x"

object HexUtils {

    fun bytesToStringLowercase(bytes: ByteArray?) = Hex.bytesToStringUppercase(bytes).toLowerCase()

    fun toBigInteger(address: String) = BigInteger(removePrefix(address), 16)

    fun removePrefix(address: String): String {
        if (address.startsWith(PREFIX)) {
            return address.substring(2)
        }
        return address
    }

    fun withPrefix(address: String): String {
        var result = address.toLowerCase()
        if (!result.startsWith(PREFIX)) {
            result = PREFIX + result
        }
        return result
    }
}