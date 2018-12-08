package com.myetherwallet.mewconnect.feature.main.view.behavior

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.myetherwallet.mewconnect.feature.main.utils.WalletSizingUtils
import com.myetherwallet.mewconnect.feature.main.view.WalletScrollable
import com.myetherwallet.mewconnect.feature.register.utils.ScrollWatcher


/**
 * Created by BArtWell on 17.08.2018.
 */

private const val EXTRA_SCROLL = "scroll"

class WalletScrollBehavior(context: Context, attrs: AttributeSet?, private val scrollWatcher: ScrollWatcher) : CoordinatorLayout.Behavior<FrameLayout>(context, attrs) {

    private var scroll = 0
    private var scrollThreshold = 0
    private val views = mutableListOf<WalletScrollable>()

    override fun layoutDependsOn(parent: CoordinatorLayout, child: FrameLayout, dependency: View) = dependency is RecyclerView

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: FrameLayout, dependency: View): Boolean {
        if (dependency is RecyclerView) {
            scrollWatcher.scrollPositionListener = { notifyViews() }

            for (i in 0 until child.childCount) {
                val view = child.getChildAt(i)
                if (view is WalletScrollable) {
                    views.add(view)
                }
            }

            notifyViews()
        }
        return super.onDependentViewChanged(parent, child, dependency)
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: FrameLayout, layoutDirection: Int): Boolean {
        if (scrollThreshold == 0) {
            scrollThreshold = WalletSizingUtils.calculateScrollThreshold(parent)
        }
        val result = super.onLayoutChild(parent, child, layoutDirection)
        notifyViews()
        return result
    }

    override fun onSaveInstanceState(parent: CoordinatorLayout, child: FrameLayout): Parcelable? {
        val bundle = Bundle()
        bundle.putInt(EXTRA_SCROLL, scroll)
        return bundle
    }

    override fun onRestoreInstanceState(parent: CoordinatorLayout, child: FrameLayout, state: Parcelable) {
        super.onRestoreInstanceState(parent, child, state)
        scroll = (state as Bundle).getInt(EXTRA_SCROLL)
        notifyViews()
    }

    private fun notifyViews() {
        if (!views.isEmpty()) {
            var ratio = 1 - scrollWatcher.scrollPosition / scrollThreshold.toFloat()
            if (ratio < 0) {
                ratio = 0f
            }
            views[0].setRatio(ratio)
            views[1].setRatio(ratio)
            views[2].setRatio(ratio)
        }
    }
}