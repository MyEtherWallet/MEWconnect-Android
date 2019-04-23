package com.myetherwallet.mewconnect.feature.main.view

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.util.StateSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.extenstion.formatMoney
import kotlinx.android.synthetic.main.view_wallet_toolbar.view.*
import java.math.BigDecimal
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Created by BArtWell on 21.08.2018.
 */

class WalletToolbar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), WalletScrollable {

    private val blueColor = ContextCompat.getColor(context, R.color.colorAccent)
    private val subtitleTextSize = resources.getDimension(R.dimen.text_size_fixed_14sp)
    private val cornerRadius = resources.getDimension(R.dimen.dimen_12dp)
    var onInfoClickListener: (() -> Unit)? = null
    var onBuyClickListener: (() -> Unit)? = null
    var onNetworkClickListener: (() -> Unit)? = null

    init {
        View.inflate(context, R.layout.view_wallet_toolbar, this)
    }

    fun setBalance(balance: BigDecimal) {
        wallet_toolbar_balance.text = balance.formatMoney(5, "ETH")
    }

    fun setNetwork(network: Network) {
        wallet_toolbar_network.setText(network.shortName)
    }

    override fun setRatio(ratio: Float) {
        val blackWhite = ArgbEvaluator().evaluate(ratio, Color.WHITE, Color.BLACK) as Int

        wallet_toolbar_title.setTextColor(blackWhite)

        val sumRatio = 1f / 0.15f * (0.15f - min(0.15f, ratio))
        val sumTextSize = subtitleTextSize * sumRatio
        wallet_toolbar_balance.setTextSize(TypedValue.COMPLEX_UNIT_PX, sumTextSize)
        wallet_toolbar_balance.visibility = if (sumTextSize.roundToInt() == 0) View.GONE else View.VISIBLE

        val networkRatio = 1f / 0.15f * (min(0.3f, max(0.15f, ratio)) - 0.15f)
        val networkTextSize = subtitleTextSize * networkRatio
        wallet_toolbar_network.setTextSize(TypedValue.COMPLEX_UNIT_PX, networkTextSize)
        wallet_toolbar_network.visibility = if (networkTextSize.roundToInt() == 0) View.GONE else View.VISIBLE

        val buttonBackground = ArgbEvaluator().evaluate(ratio, Color.WHITE, ContextCompat.getColor(context, R.color.wallet_toolbar_buttons_background)) as Int
        val pressedBackground = ArgbEvaluator().evaluate(ratio + if (ratio > 0.5) -0.15f else 0.15f, Color.WHITE, ContextCompat.getColor(context, R.color.wallet_toolbar_buttons_background)) as Int
        wallet_toolbar_buy.background = createSelector(buttonBackground, pressedBackground)
        wallet_toolbar_buy_container.setOnClickListener { onBuyClickListener?.invoke() }
        wallet_toolbar_info.background = createSelector(buttonBackground, pressedBackground)
        wallet_toolbar_info_container.setOnClickListener { onInfoClickListener?.invoke() }

        wallet_toolbar_network.setOnClickListener { onNetworkClickListener?.invoke() }
    }

    private fun createSelector(@ColorInt default: Int, @ColorInt pressed: Int): StateListDrawable {
        val list = StateListDrawable()

        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadius = cornerRadius
        shape.setStroke(0, Color.TRANSPARENT)
        shape.setColor(pressed)
        list.addState(intArrayOf(android.R.attr.state_pressed), shape)

        val shape2 = GradientDrawable()
        shape2.shape = GradientDrawable.RECTANGLE
        shape2.cornerRadius = cornerRadius
        shape2.setStroke(0, Color.TRANSPARENT)
        shape2.setColor(default)
        list.addState(StateSet.WILD_CARD, shape2)
        return list
    }
}