package com.myetherwallet.mewconnect.feature.main.data

/**
 * Created by BArtWell on 02.09.2018.
 */

data class WalletData(
        var isFromCache: Boolean,
        val items: List<WalletListItem>,
        val balance: WalletBalance
)