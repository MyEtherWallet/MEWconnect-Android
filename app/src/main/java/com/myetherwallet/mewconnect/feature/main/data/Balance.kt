package com.myetherwallet.mewconnect.feature.main.data

import java.math.BigDecimal
import java.math.BigInteger

data class Balance(
        val balance: BigInteger,
        val decimals: Int,
        val symbol: String,
        val address: String,
        val name: String?,
        val website: String?,
        val email: String?
) {

    fun calculateBalance(): BigDecimal {
        return BigDecimal(balance).divide(BigDecimal.TEN.pow(decimals))
    }
}
