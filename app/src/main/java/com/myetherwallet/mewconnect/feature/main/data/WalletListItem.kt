package com.myetherwallet.mewconnect.feature.main.data

import java.math.BigDecimal

/**
 * Created by BArtWell on 01.09.2018.
 */

data class WalletListItem(
        val title: String,
        val symbol: String,
        val value: BigDecimal,
        val valueUsd: BigDecimal?,
        val stockPrice: BigDecimal?
)