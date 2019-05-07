package com.myetherwallet.mewconnect.feature.register.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.feature.main.utils.WalletSizingUtils
import kotlin.math.abs

/**
 * Created by BArtWell on 06.09.2018.
 */

// Behaviour scroll works incorrectly when scrolling with smoothScrollBy()
// RecyclerView.OnScrollListener doesn't catch scroll events when items removed
// So we will compute scroll position

class ScrollWatcher {

    private lateinit var recyclerView: RecyclerView
    var scrollPositionListener: ((position: Int) -> Unit)? = null
    var scrollStateListener: ((recyclerView: RecyclerView, state: Int, position: Int) -> Unit)? = null

    var scrollPosition = 0
    var scrollState = RecyclerView.SCROLL_STATE_IDLE

    private val headerHeight by lazy { WalletSizingUtils.calculateListMargin(recyclerView) }
    private val itemHeight by lazy { recyclerView.context.resources.getDimension(R.dimen.wallet_list_item_height) }

    fun setup(view: RecyclerView) {
        recyclerView = view
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                scrollPosition += dy
                scrollPositionListener?.invoke(scrollPosition)
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                scrollState = newState
                scrollStateListener?.invoke(recyclerView, newState, scrollPosition)
            }
        })
    }

    fun request(): Int {
        scrollPosition = calculateCurrentScrollPosition()
        scrollPositionListener?.invoke(scrollPosition)
        scrollStateListener?.invoke(recyclerView, RecyclerView.SCROLL_STATE_IDLE, scrollPosition)
        return scrollPosition
    }

    private fun calculateCurrentScrollPosition(): Int {
        val layoutManager = recyclerView.layoutManager as
                LinearLayoutManager
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        val firstVisibleView = layoutManager.findViewByPosition(firstVisibleItemPosition)
        return if (firstVisibleItemPosition > 0) {
            abs(headerHeight + itemHeight * (firstVisibleItemPosition - 1) - firstVisibleView!!.top).toInt()
        } else {
            abs(firstVisibleView!!.top)
        }
    }
}