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

    private val endOfDataIndex = jsonRpcResponse.result.lastIndexOf("01")
    private var offset = endOfDataIndex

    fun toWalletBalance(): BigDecimal =  HexUtils.toBigInteger(jsonRpcResponse.result).toEthValue()

    fun toBalancesList(): List<Balance> {
        val balances = mutableListOf<Balance>()
        val tokensCount = getInt(getNextBytes(32))
        val hasName = getBoolean(getNextBytes(1))
        val hasWebsite = getBoolean(getNextBytes(1))
        val hasEmail = getBoolean(getNextBytes(1))
        for (i in 0 until tokensCount) {
            // balance
            val balanceOffset = offset - sizeHex(16 + 20 + 1 + 32)
            val balanceStr = jsonRpcResponse.result.substring(balanceOffset, balanceOffset + sizeHex(32))
            val balance = getBigInteger(balanceStr)
            if (balance.compareTo(BigInteger.ZERO) <= 0) {
                offset -= sizeHex(16 + 20 + 1 + 32)
                if (hasName) {
                    offset -= sizeHex(16)
                }
                if (hasWebsite) {
                    offset -= sizeHex(32)
                }
                if (hasEmail) {
                    offset -= sizeHex(32)
                }
                continue
            }

            // symbol
            offset -= sizeHex(16)
            val symbolStr = jsonRpcResponse.result.substring(offset, offset + sizeHex(16))
            val symbol = getString(symbolStr)

            //addr
            offset -= sizeHex(20)
            val addrStr = jsonRpcResponse.result.substring(offset, offset + sizeHex(20))
            val address = "0x$addrStr"

            //decimal
            offset -= sizeHex(1)
            val decimalStr = jsonRpcResponse.result.substring(offset, offset + sizeHex(1))
            val decimals = getInt(decimalStr)

            //balance
            offset -= sizeHex(32)

            var name: String? = null
            if (hasName) {
                offset -= sizeHex(16)
                val nameStr = jsonRpcResponse.result.substring(offset, offset + sizeHex(16))
                name = getString(nameStr)
            }

            var website: String? = null
            if (hasWebsite) {
                offset -= sizeHex(32)
                val webSiteStr = jsonRpcResponse.result.substring(offset, offset + sizeHex(32))
                website = getString(webSiteStr)
            }

            var email: String? = null
            if (hasEmail) {
                offset -= sizeHex(32)
                val emailStr = jsonRpcResponse.result.substring(offset, offset + sizeHex(32))
                email = getString(emailStr)
            }

            balances.add(Balance(balance, decimals, symbol, address, name, website, email))
        }
        return balances
    }

    private fun getBigInteger(string: String) = BigInteger(string, 16)

    private fun sizeHex(size: Int) = size * 2

    private fun getNextBytes(bytesCount: Int): String {
        val end = offset
        offset -= sizeHex(bytesCount)
        val start = offset
        return jsonRpcResponse.result.substring(start, end)
    }

    private fun getBoolean(string: String) = (trimInt(string) == "1")

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