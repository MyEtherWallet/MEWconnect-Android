package com.myetherwallet.mewconnect.feature.main.data

import java.math.BigDecimal

/**
 * Created by BArtWell on 02.09.2018.
 */

data class WalletBalance(
        val value: BigDecimal,
        val valueUsd: BigDecimal?,
        val stockPrice: BigDecimal?
)