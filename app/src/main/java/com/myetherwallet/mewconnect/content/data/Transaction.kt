package com.myetherwallet.mewconnect.content.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.web3j.crypto.RawTransaction
import java.math.BigInteger

/**
 * Created by BArtWell on 28.07.2018.
 */

@Parcelize
data class Transaction(
        val nonce: BigInteger,
        val gasPrice: BigInteger,
        val gas: BigInteger,
        val to: String,
        val value: BigInteger,
        val data: String,
        val chainId: Byte
) : BaseMessage(), Parcelable {

    fun toRawTransaction(): RawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gas, to, value, data)
}