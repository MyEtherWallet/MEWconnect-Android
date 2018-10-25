package com.myetherwallet.mewconnect.core.extenstion

import android.text.TextUtils
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode

/**
 * Created by BArtWell on 01.09.2018.
 */

fun BigDecimal?.formatUsd() = this?.let { "$" + it.formatMoney(2) } ?: ""

fun BigDecimal?.formatMoney(scale: Int, currency: String? = null): String {
    val value = this?.setScale(scale, RoundingMode.HALF_UP)?.toStringWithoutZeroes() ?: ""
    return if (TextUtils.isEmpty(currency)) {
        value
    } else {
        "$value $currency"
    }
}

fun BigDecimal.toStringWithoutZeroes(): String = if (this.unscaledValue() == BigInteger.ZERO) {
    "0"
} else {
    this.stripTrailingZeros().toPlainString()
}