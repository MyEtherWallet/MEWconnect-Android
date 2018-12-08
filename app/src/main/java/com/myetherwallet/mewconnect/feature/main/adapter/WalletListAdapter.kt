package com.myetherwallet.mewconnect.feature.main.adapter

import android.view.ViewGroup
import android.widget.Filterable
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.extenstion.isTrue
import com.myetherwallet.mewconnect.core.ui.adapter.BaseRecyclerAdapter
import com.myetherwallet.mewconnect.feature.main.adapter.filter.WalletListFilter
import com.myetherwallet.mewconnect.feature.main.adapter.holder.WalletListBaseHolder
import com.myetherwallet.mewconnect.feature.main.adapter.holder.WalletListEmptyHolder
import com.myetherwallet.mewconnect.feature.main.adapter.holder.WalletListHeaderHolder
import com.myetherwallet.mewconnect.feature.main.adapter.holder.WalletListHolder
import com.myetherwallet.mewconnect.feature.main.data.WalletListItem

private const val TYPE_ITEM = 0
private const val TYPE_HEADER = 1
private const val TYPE_FOOTER = 2
private const val TYPE_NOTHING = 3

class WalletListAdapter : BaseRecyclerAdapter<WalletListBaseHolder>(), Filterable {

    var items = mutableListOf<WalletListItem>()
        set(value) {
            field = value
            filter = WalletListFilter(this, value)
        }
    private var filter: WalletListFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_HEADER -> WalletListHeaderHolder(inflate(R.layout.list_item_header_wallet, parent))
        TYPE_FOOTER -> WalletListEmptyHolder(inflate(R.layout.list_item_footer_wallet, parent))
        TYPE_NOTHING -> WalletListEmptyHolder(inflate(R.layout.list_item_wallet, parent))
        else -> WalletListHolder(inflate(R.layout.list_item_wallet, parent))
    }

    override fun getItemViewType(position: Int) = if (position == 0) {
        TYPE_HEADER
    } else if (position == (itemCount - 1)) {
        TYPE_FOOTER
    } else if (!filter?.isEmpty().isTrue(true) && items.isEmpty()) {
        TYPE_NOTHING
    } else {
        TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return if (items.isEmpty()) {
            return if (filter?.isEmpty().isTrue(true)) {
                0
            } else {
                3
            }
        } else {
            items.size + 2
        }
    }

    override fun onBindViewHolder(holder: WalletListBaseHolder, position: Int) {
        if (getItemViewType(position) == TYPE_ITEM) {
            holder.bind(items[position - 1])
        } else if (getItemViewType(position) == TYPE_FOOTER) {
            holder.setupViewHeight(items.size)
        }
    }

    override fun getFilter() = filter
}
