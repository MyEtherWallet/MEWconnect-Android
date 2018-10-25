package com.myetherwallet.mewconnect.feature.main.adapter.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.myetherwallet.mewconnect.feature.main.data.WalletListItem

/**
 * Created by BArtWell on 02.09.2018.
 */
abstract class WalletListBaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    open fun bind(item: WalletListItem) {}

    open fun setupViewHeight(itemsCount: Int) {}
}