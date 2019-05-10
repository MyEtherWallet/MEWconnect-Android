package com.myetherwallet.mewconnect.core.utils

import com.google.android.gms.common.util.Hex
import java.math.BigInteger

/**
 * Created by BArtWell on 02.09.2018.
 */

private const val PREFIX = "0x"

object HexUtils {

    fun bytesToStringLowercase(bytes: ByteArray?) = Hex.bytesToStringUppercase(bytes).toLowerCase()

    fun toBigInteger(data: String) = BigInteger(removePrefix(normalizeBigInteger(data)), 16)

    private fun normalizeBigInteger(data: String) =
            if (data == PREFIX) {
                "0x0"
            } else {
                data
            }

    fun removePrefix(address: String): String {
        if (address.startsWith(PREFIX)) {
            return address.substring(2)
        }
        return address
    }

    fun withPrefixLowerCase(address: String) = withPrefix(address.toLowerCase())

    fun withPrefix(address: String): String {
        var result = address
        if (!result.startsWith(PREFIX)) {
            result = PREFIX + result
        }
        return result
    }

    fun isStringHexWithPrefix(text: String?) = (text?.startsWith(PREFIX) == true && isStringHex(removePrefix(text)))

    fun isStringHex(text: String?): Boolean {
        text?.let {
            for (char in text.toCharArray()) {
                if (char != '0' &&
                        char != '1' &&
                        char != '2' &&
                        char != '3' &&
                        char != '4' &&
                        char != '5' &&
                        char != '6' &&
                        char != '7' &&
                        char != '8' &&
                        char != '9' &&
                        char != 'a' &&
                        char != 'b' &&
                        char != 'c' &&
                        char != 'd' &&
                        char != 'e' &&
                        char != 'f' &&
                        char != 'A' &&
                        char != 'B' &&
                        char != 'C' &&
                        char != 'D' &&
                        char != 'E' &&
                        char != 'F') {
                    return false
                }
            }
        } ?: return false
        return true
    }
}