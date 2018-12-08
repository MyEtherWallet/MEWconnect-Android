package com.myetherwallet.mewconnect.feature.main.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.extenstion.formatUsd
import com.myetherwallet.mewconnect.core.utils.ApplicationUtils
import com.myetherwallet.mewconnect.feature.main.utils.WalletSizingUtils
import kotlinx.android.synthetic.main.view_wallet_header.view.*
import java.math.BigDecimal

/**
 * Created by BArtWell on 21.08.2018.
 */

private const val SCALE_MIN = 0.8f
private const val SCALE_MAX = 1f

class WalletHeader @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), WalletScrollable {

    init {
        View.inflate(context, R.layout.view_wallet_header, this)
    }

    private var yExpanded = 0
    private var yCollapsed = 0
    var onEnterSearchModeListener: (() -> Unit)? = null
    var onUpdateClickListener: (() -> Unit)? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        outlineProvider = null

        val statusBarHeight = ApplicationUtils.getStatusBarHeight(context)
        yCollapsed = statusBarHeight + resources.getDimension(R.dimen.wallet_toolbar_height).toInt()
        yExpanded = yCollapsed +
                WalletSizingUtils.calculateCardHeight(context) +
                resources.getDimension(R.dimen.dimen_16dp).toInt()

        wallet_header_update_button.setOnClickListener {
            setUpdating(true)
            onUpdateClickListener?.invoke()
        }
    }

    fun setUpdating(isUpdating: Boolean) {
        if (isUpdating) {
            wallet_header_update_button.visibility = GONE
            wallet_header_update_progress.visibility = VISIBLE
        } else {
            wallet_header_update_button.visibility = VISIBLE
            wallet_header_update_progress.visibility = GONE
        }
    }

    fun setBalance(balance: BigDecimal) {
        wallet_header_balances_value.text = balance.formatUsd()
    }

    override fun setRatio(ratio: Float) {
        val scale = SCALE_MIN + (SCALE_MAX - SCALE_MIN) * ratio

        wallet_header_update_container.scaleX = scale
        wallet_header_update_container.scaleY = scale

        wallet_header_balances_value.scaleX = scale
        wallet_header_balances_value.scaleY = scale
        wallet_header_balances_value.pivotX = wallet_header_balances_value.width.toFloat()

        y = yCollapsed + (yExpanded - yCollapsed) * ratio
    }

    fun setTextChangedListener(textWatcher: TextWatcher) {
        wallet_header_search.addTextChangedListener(textWatcher)
    }

    fun setHint(hint: String) {
        wallet_header_search.hint = hint
        wallet_header_search.onFocusChangeListener = View.OnFocusChangeListener { _: View, hasFocus: Boolean ->
            if (hasFocus) {
                onEnterSearchModeListener?.invoke()
            }
        }
    }

    fun setSearchVisible(isVisible: Boolean) {
        wallet_header_search_container.visibility = if (isVisible) VISIBLE else GONE
    }

    fun clearSearch() = wallet_header_search.setText("")
}