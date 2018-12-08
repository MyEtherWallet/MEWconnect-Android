package com.myetherwallet.mewconnect.feature.main.adapter.holder

import android.view.View
import com.myetherwallet.mewconnect.feature.main.utils.WalletSizingUtils
import kotlinx.android.synthetic.main.list_item_header_wallet.view.*

/**
 * Created by BArtWell on 02.09.2018.
 */

class WalletListHeaderHolder(itemView: View) : WalletListBaseHolder(itemView) {

    init {
        val layoutParams = itemView.wallet_list_header.layoutParams
        layoutParams.height = WalletSizingUtils.calculateListMargin(itemView)
        itemView.wallet_list_header.layoutParams = layoutParams
    }
}