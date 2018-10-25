package com.myetherwallet.mewconnect.core.extenstion

/**
 * Created by BArtWell on 05.09.2018.
 */

fun Boolean?.isTrue(defValue: Boolean = false) = this?.let { it } ?: defValue