package com.myetherwallet.mewconnect.feature.main.adapter.holder

import android.view.View
import com.myetherwallet.mewconnect.core.extenstion.formatMoney
import com.myetherwallet.mewconnect.core.extenstion.formatUsd
import com.myetherwallet.mewconnect.feature.main.data.WalletListItem
import kotlinx.android.synthetic.main.list_item_wallet.view.*

class WalletListHolder(itemView: View) : WalletListBaseHolder(itemView) {

    override fun bind(item: WalletListItem) {
        itemView.apply {
            wallet_list_title.text = item.title
            wallet_list_value.text = item.value.formatMoney(4, item.symbol)
            wallet_list_usd_value.text = item.valueUsd.formatUsd()
            wallet_list_stock_price.text = item.stockPrice?.let { "@" + it.formatMoney(4) } ?: ""
        }
    }
}
