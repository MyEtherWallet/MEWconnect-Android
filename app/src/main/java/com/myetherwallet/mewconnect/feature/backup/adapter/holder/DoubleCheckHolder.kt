package com.myetherwallet.mewconnect.feature.backup.adapter.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.ui.view.ButtonGroupItem
import com.myetherwallet.mewconnect.feature.backup.adapter.DoubleCheckAdapter
import kotlinx.android.synthetic.main.list_item_double_check.view.*

/**
 * Created by BArtWell on 24.08.2018.
 */

class DoubleCheckHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val views = listOf<ButtonGroupItem>(itemView.double_check_left, itemView.double_check_center, itemView.double_check_right).shuffled()

    fun bind(items: List<DoubleCheckAdapter.Item>, allItems: List<DoubleCheckAdapter.Item>, onSelectListener: (isCorrectlySelected: Boolean) -> Unit) {
        val context = itemView.context

        val correct = items[adapterPosition]
        val wrong = allItems
                .filter { it != correct }
                .distinctBy { it.word }
                .shuffled()

        itemView.double_check_title.text = context.getString(R.string.lets_double_check_item_title, correct.number + 1)

        views[0].text = correct.word
        views[1].text = wrong[0].word
        views[2].text = wrong[1].word

        itemView.double_check_group.setOnCheckedChangeListener { radioGroup, checkedId ->
            onSelectListener.invoke(views[0].id == checkedId)
        }
    }
}
