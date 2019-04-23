package com.myetherwallet.mewconnect.feature.backup.adapter.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.myetherwallet.mewconnect.feature.backup.adapter.WriteTheseWordsAdapter
import kotlinx.android.synthetic.main.list_item_write_these_words.view.*

class WriteTheseWordsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(item: WriteTheseWordsAdapter.Item) {
        itemView.write_these_word_number.text = (item.number + 1).toString()
        itemView.write_these_word_text.text = item.word
    }
}
