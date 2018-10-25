package com.myetherwallet.mewconnect.feature.main.adapter.holder

import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.utils.ApplicationUtils
import kotlinx.android.synthetic.main.list_item_footer_wallet.view.*
import kotlin.math.max

/**
 * Created by BArtWell on 02.09.2018.
 */
class WalletListEmptyHolder(itemView: View) : WalletListBaseHolder(itemView) {

    private val context = itemView.context
    private val itemSize = context.resources.getDimension(R.dimen.wallet_list_item_height)
    private val displayHeight = ApplicationUtils.getDisplaySize(context).height
    private val listFooterHeight = context.resources.getDimension(R.dimen.wallet_list_footer_height)
    private val toolbarHeight = context.resources.getDimension(R.dimen.wallet_toolbar_height)
    private val headerHeight = context.resources.getDimension(R.dimen.wallet_header_height)
    private val statusBarHeight = ApplicationUtils.getStatusBarHeight(context)

    override fun setupViewHeight(itemsCount: Int) {
        val itemsSize = itemsCount * itemSize
        val autoHeight = displayHeight - itemsSize - toolbarHeight - headerHeight - statusBarHeight
        val height = max(autoHeight, listFooterHeight)
        val layoutParams = itemView.wallet_list_footer.layoutParams
        layoutParams.height = height.toInt()
        itemView.wallet_list_footer.layoutParams = layoutParams
    }
}