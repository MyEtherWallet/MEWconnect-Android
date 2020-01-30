package com.myetherwallet.mewconnect.feature.main.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.utils.ApplicationUtils
import com.myetherwallet.mewconnect.feature.main.utils.WalletSizingUtils
import kotlin.math.max

/**
 * Created by BArtWell on 29.01.2020.
 */

class MewWalletView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.cardview.widget.CardView(context, attrs, defStyleAttr), WalletScrollable {

    init {
        View.inflate(context, R.layout.view_mewwallet, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val statusBarHeight = ApplicationUtils.getStatusBarHeight(context)
        val toolbarHeight = statusBarHeight + resources.getDimension(R.dimen.wallet_toolbar_height).toInt()
        y = toolbarHeight + WalletSizingUtils.calculateCardHeight() + resources.getDimension(R.dimen.dimen_16dp)
    }

    override fun setRatio(ratio: Float) {
        val newRatio = max(ratio - 0.8f, 0f) / 0.2f
        alpha =newRatio
    }
}
