package com.myetherwallet.mewconnect.feature.main.utils

import com.google.android.gms.common.util.Hex
import com.myetherwallet.mewconnect.core.extenstion.toEthValue
import com.myetherwallet.mewconnect.core.utils.HexUtils
import com.myetherwallet.mewconnect.feature.main.data.Balance
import com.myetherwallet.mewconnect.feature.main.data.JsonRpcResponse
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created by BArtWell on 31.08.2018.
 */

class JsonRpcResponseConverter(private val jsonRpcResponse: JsonRpcResponse) {

    private var offset = 0

    fun toWalletBalance(): BigDecimal =  HexUtils.toBigInteger(jsonRpcResponse.result).toEthValue()

    fun toBalancesList(): List<Balance> {
        val balances = mutableListOf<Balance>()
        val tokensCount = getInt(getNextBytes(32))
        val hasName = getBoolean(getNextBytes(1))
        val hasWebsite = getBoolean(getNextBytes(1))
        val hasEmail = getBoolean(getNextBytes(1))
        for (i in 0 until tokensCount) {
            // symbol
            val symbol = getString(getNextBytes(16))

            //addr
            val addrStr = getString(getNextBytes(20))
            val address = "0x$addrStr"

            //decimal
            val decimals = getInt(getNextBytes(1))

            //balance
            val balance = getBigInteger(getNextBytes(32))

            var name: String? = null
            if (hasName) {
                name = getString(getNextBytes(16))
            }

            var website: String? = null
            if (hasWebsite) {
                website = getString(getNextBytes(32))
            }

            var email: String? = null
            if (hasEmail) {
                email = getString(getNextBytes(32))
            }

            balances.add(Balance(balance, decimals, symbol, address, name, website, email))
        }
        return balances
    }

    private fun getBigInteger(string: String) = BigInteger(string, 16)

    private fun sizeHex(size: Int) = size * 2

    private fun getNextBytes(bytesCount: Int): String {
        val start = offset
        offset += sizeHex(bytesCount)
        val end = offset
        return HexUtils.removePrefix(jsonRpcResponse.result).substring(start, end)
    }

    private fun getBoolean(string: String) = trimInt(string) == "1"

    private fun getInt(string: String): Int {
        val trimmed = trimInt(string)
        if (trimmed.isEmpty()) {
            return 0
        }
        return trimmed.toInt(16)
    }

    private fun getString(string: String) = String(Hex.stringToBytes(string)).trimEnd(0.toChar())

    private fun trimInt(string: String) = string.trimStart('0')
}
