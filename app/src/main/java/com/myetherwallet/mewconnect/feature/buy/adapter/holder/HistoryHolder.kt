package com.myetherwallet.mewconnect.feature.buy.adapter.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.extenstion.formatUsd
import com.myetherwallet.mewconnect.feature.buy.data.PurchaseStatus
import kotlinx.android.synthetic.main.list_item_history.view.*
import java.util.*

class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(status: PurchaseStatus) {
        val context = itemView.context
        val date = Date()
        itemView.history_date.text = String.format(Locale.US, context.getString(R.string.history_date), date, date)
        itemView.history_total_amount.text = status.fiatTotalAmount.amount.formatUsd()
        itemView.history_status.text = status.status.toLowerCase().capitalize()
        val statusLowerCase = status.status.toLowerCase()
        if (statusLowerCase == PurchaseStatus.STATUS_IN_PROGRESS) {
            itemView.history_status.setTextColor(context.getColor(R.color.blue))
        } else if (statusLowerCase == PurchaseStatus.STATUS_DECLINED) {
            itemView.history_status.setTextColor(context.getColor(R.color.red))
        } else {
            itemView.history_status.setTextColor(context.getColor(R.color.text_black))
        }
    }
}
