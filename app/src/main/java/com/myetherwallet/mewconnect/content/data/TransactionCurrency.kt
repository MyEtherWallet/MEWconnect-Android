package com.myetherwallet.mewconnect.content.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by BArtWell on 03.01.2019.
 */

@Parcelize
data class TransactionCurrency(
        val symbol: String,
        val decimals: Int,
        val address: String?
) : Parcelable