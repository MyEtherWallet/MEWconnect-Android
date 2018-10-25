package com.myetherwallet.mewconnect.feature.backup.adapter

import android.view.ViewGroup
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.ui.adapter.BaseRecyclerAdapter
import com.myetherwallet.mewconnect.feature.backup.adapter.holder.WriteTheseWordsHolder
import kotlin.math.ceil

class WriteTheseWordsAdapter : BaseRecyclerAdapter<WriteTheseWordsHolder>() {

    private var items = mutableListOf<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WriteTheseWordsHolder {
        return WriteTheseWordsHolder(inflate(R.layout.list_item_write_these_words, parent))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: WriteTheseWordsHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setItems(data: List<String>) {
        val sorted = mutableListOf<Item>()
        val size = data.size
        val half = ceil(size.toFloat() / 2).toInt()
        for (i in 0 until half) {
            sorted.add(Item(i, data[i]))
            if (i + half < size) {
                sorted.add(Item(i + half, data[i + half]))
            }
        }
        items = sorted.toMutableList()
        notifyDataSetChanged()
    }

    data class Item(val number: Int, val word: String)
}
