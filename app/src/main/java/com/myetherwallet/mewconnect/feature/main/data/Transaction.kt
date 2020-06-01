package com.myetherwallet.mewconnect.feature.main.data

import java.math.BigInteger

/**
 * Created by BArtWell on 11.05.2020.
 */

data class Transaction(
        val from: String?,
        val nonce: BigInteger?,
        val gasPrice: BigInteger?,
        val gas: String?,
        val to: String,
        val value: BigInteger?,
        val data: String
)
