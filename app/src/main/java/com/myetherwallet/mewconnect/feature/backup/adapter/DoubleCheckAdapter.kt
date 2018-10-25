package com.myetherwallet.mewconnect.feature.backup.adapter

import android.view.ViewGroup
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.ui.adapter.BaseRecyclerAdapter
import com.myetherwallet.mewconnect.feature.backup.adapter.holder.DoubleCheckHolder

/**
 * Created by BArtWell on 24.08.2018.
 */

private const val ITEMS_COUNT = 4

class DoubleCheckAdapter(private val onSelectChange: (allCorrect: Boolean) -> Unit) : BaseRecyclerAdapter<DoubleCheckHolder>() {

    private var allItems = mutableListOf<Item>()
    private var listItems = listOf<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubleCheckHolder {
        return DoubleCheckHolder(inflate(R.layout.list_item_double_check, parent))
    }

    override fun getItemCount() = ITEMS_COUNT

    override fun onBindViewHolder(holder: DoubleCheckHolder, position: Int) {
        holder.bind(listItems, allItems) {
            listItems[position].isCorrectlySelected = it
            onSelectChange(isAllItemsCorrectlySelected())
        }
    }

    fun setItems(words: List<String>) {
        allItems = mutableListOf()
        for (i in 0 until words.size) {
            allItems.add(Item(i, words[i]))
        }

        listItems = allItems.toList()
                .shuffled()
                .subList(0, ITEMS_COUNT)
                .sortedBy { it.number }
    }

    private fun isAllItemsCorrectlySelected(): Boolean {
        for (i in 0 until ITEMS_COUNT) {
            if (!listItems[i].isCorrectlySelected) {
                return false
            }
        }
        return true
    }

    data class Item(val number: Int, val word: String, var isCorrectlySelected: Boolean = false)
}
