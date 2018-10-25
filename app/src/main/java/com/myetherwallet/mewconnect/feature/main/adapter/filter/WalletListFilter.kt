package com.myetherwallet.mewconnect.feature.main.adapter.filter

import android.text.TextUtils
import android.widget.Filter
import com.myetherwallet.mewconnect.feature.main.adapter.WalletListAdapter
import com.myetherwallet.mewconnect.feature.main.data.WalletListItem

/**
 * Created by BArtWell on 31.08.2018.
 */
class WalletListFilter(private val adapter: WalletListAdapter, items: MutableList<WalletListItem>) : Filter() {

    private val originalItems = mutableListOf<WalletListItem>()

    init {
        originalItems.addAll(items)
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val filteredItems: MutableList<WalletListItem> = mutableListOf()
        val results = Filter.FilterResults()
        if (TextUtils.isEmpty(constraint)) {
            filteredItems.addAll(originalItems)
        } else {
            val pattern = constraint.toString().toLowerCase().trim()
            filteredItems.addAll(originalItems.filter { it.title.toLowerCase().contains(pattern) || it.symbol.toLowerCase().contains(pattern) })
        }
        results.values = filteredItems
        results.count = filteredItems.count()
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapter.items.clear()
        if (results.count > 0) {
            @Suppress("UNCHECKED_CAST")
            adapter.items.addAll(results.values as MutableList<WalletListItem>)
        }
        adapter.notifyDataSetChanged()
    }

    fun isEmpty() = originalItems.isEmpty()
}