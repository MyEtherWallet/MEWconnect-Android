package com.myetherwallet.mewconnect.feature.main.adapter.holder

import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.utils.DisplaySizeHelper
import com.myetherwallet.mewconnect.feature.main.utils.WalletSizingUtils
import kotlinx.android.synthetic.main.list_item_footer_wallet.view.*
import kotlin.math.max

/**
 * Created by BArtWell on 02.09.2018.
 */

class WalletListEmptyHolder(itemView: View) : WalletListBaseHolder(itemView) {

    private val context = itemView.context
    private val itemSize = context.resources.getDimension(R.dimen.wallet_list_item_height)
    private val listFooterHeight = context.resources.getDimension(R.dimen.wallet_list_footer_height)

    override fun setupViewHeight(itemsCount: Int) {
        val itemsSize = itemsCount * itemSize
        val autoHeight = DisplaySizeHelper.height - (WalletSizingUtils.calculateListMargin(itemView) + itemsSize) + WalletSizingUtils.calculateScrollThreshold(itemView)
        val height = max(autoHeight, listFooterHeight)
        val layoutParams = itemView.wallet_list_footer.layoutParams
        layoutParams.height = height.toInt()
        itemView.wallet_list_footer.layoutParams = layoutParams
    }
}