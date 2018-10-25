package com.myetherwallet.mewconnect.core.extenstion

import com.google.android.gms.common.util.Hex
import org.spongycastle.util.BigIntegers
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Created by BArtWell on 05.09.2018.
 */

private const val ETH_DECIMALS = 18

fun BigInteger.toHex(): String = Hex.bytesToStringUppercase(this.toBytes()).toLowerCase()

fun BigInteger.toBytes(): ByteArray = BigIntegers.asUnsignedByteArray(this)

fun BigInteger.toEthValue(): BigDecimal = BigDecimal(this).divide(BigDecimal.TEN.pow(ETH_DECIMALS))
