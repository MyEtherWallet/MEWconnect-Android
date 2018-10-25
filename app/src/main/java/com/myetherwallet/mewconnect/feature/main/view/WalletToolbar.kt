package com.myetherwallet.mewconnect.feature.main.view

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Color
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.extenstion.formatMoney
import com.myetherwallet.mewconnect.core.extenstion.overrideColor
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
        val blueWhite = ArgbEvaluator().evaluate(ratio, Color.WHITE, blueColor) as Int

        wallet_toolbar_title.setTextColor(blackWhite)

        if (ratio > 0.5f) {
            wallet_toolbar_beta.setImageResource(R.drawable.beta_blue)
            wallet_toolbar_beta.alpha = 1f - (1f - ratio) * 2
        } else {
            wallet_toolbar_beta.setImageResource(R.drawable.beta_white)
            wallet_toolbar_beta.alpha = 1f - ratio * 2
        }

        val sumRatio = 1f / 0.15f * (0.15f - min(0.15f, ratio))
        val sumTextSize = subtitleTextSize * sumRatio
        wallet_toolbar_balance.setTextSize(TypedValue.COMPLEX_UNIT_PX, sumTextSize)
        wallet_toolbar_balance.visibility = if (sumTextSize.roundToInt() == 0) View.GONE else View.VISIBLE

        val networkRatio = 1f / 0.15f * (min(0.3f, max(0.15f, ratio)) - 0.15f)
        val networkTextSize = subtitleTextSize * networkRatio
        wallet_toolbar_network.setTextSize(TypedValue.COMPLEX_UNIT_PX, networkTextSize)
        wallet_toolbar_network.visibility = if (networkTextSize.roundToInt() == 0) View.GONE else View.VISIBLE

        val buttonBackground = ArgbEvaluator().evaluate(ratio, Color.WHITE, ContextCompat.getColor(context, R.color.wallet_toolbar_buttons_background)) as Int
        wallet_toolbar_buy.background = ContextCompat.getDrawable(context, R.drawable.wallet_toolbar_buy_background)!!.overrideColor(buttonBackground)
        wallet_toolbar_buy.setOnClickListener { onBuyClickListener?.invoke() }
        wallet_toolbar_info.background = ContextCompat.getDrawable(context, R.drawable.wallet_toolbar_info_background)!!.overrideColor(buttonBackground)
        wallet_toolbar_info.setOnClickListener { onInfoClickListener?.invoke() }

        wallet_toolbar_network.setOnClickListener { onNetworkClickListener?.invoke() }
    }
}