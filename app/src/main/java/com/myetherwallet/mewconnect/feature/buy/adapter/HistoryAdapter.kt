package com.myetherwallet.mewconnect.feature.buy.adapter

import android.view.ViewGroup
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.ui.adapter.BaseRecyclerAdapter
import com.myetherwallet.mewconnect.feature.buy.adapter.holder.HistoryHolder
import com.myetherwallet.mewconnect.feature.buy.data.PurchaseStatus

/**
 * Created by BArtWell on 18.09.2018.
 */

class HistoryAdapter : BaseRecyclerAdapter<HistoryHolder>() {

    var items = listOf<PurchaseStatus>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        return HistoryHolder(inflate(R.layout.list_item_history, parent))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        holder.bind(items[position])
    }
}
