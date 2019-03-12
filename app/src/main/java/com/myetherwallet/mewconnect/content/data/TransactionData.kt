package com.myetherwallet.mewconnect.content.data

import com.myetherwallet.mewconnect.core.utils.HexUtils
import java.math.BigInteger

/**
 * Created by BArtWell on 03.01.2019.
 */
data class TransactionData(
        val function: String,
        val address: String,
        val amount: BigInteger
) {

    companion object {
        const val FUNCTION_TOKEN_TRANSFER = "0xa9059cbb"

        fun fromString(data: String): TransactionData? {
            if (data.length == 138) {
                return TransactionData(
                        data.substring(0, 10),
                        HexUtils.withPrefixLowerCase(data.substring(34, 74)),
                        HexUtils.toBigInteger(data.substring(74)))
            } else {
                return null
            }
        }
    }
}