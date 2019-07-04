package com.myetherwallet.mewconnect.feature.main.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.myetherwallet.mewconnect.MewApplication
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.extenstion.formatMoney
import com.myetherwallet.mewconnect.core.extenstion.formatUsd
import com.myetherwallet.mewconnect.core.extenstion.getSize
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.utils.ApplicationUtils
import com.myetherwallet.mewconnect.core.utils.CardBackgroundHelper
import com.myetherwallet.mewconnect.core.utils.DisplaySizeHelper
import com.myetherwallet.mewconnect.core.utils.HexUtils
import com.myetherwallet.mewconnect.feature.main.data.WalletBalance
import com.myetherwallet.mewconnect.feature.main.utils.WalletSizingUtils
import kotlinx.android.synthetic.main.view_wallet_card.view.*
import org.web3j.crypto.Keys
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.math.roundToInt


/**
 * Created by BArtWell on 19.08.2018.
 */

private const val ADDRESS_ELLIPSIS = "\u22ef"

class WalletCardView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.cardview.widget.CardView(context, attrs, defStyleAttr), WalletScrollable {

    @Inject
    lateinit var preferences: PreferencesManager

    private val collapsedWidth = DisplaySizeHelper.width
    private val expandedWidth = WalletSizingUtils.calculateCardWidth()
    private var collapsedHeight = 0
    private val expandedHeight = WalletSizingUtils.calculateCardHeight()
    private var expandedY = 0

    private var cornerRadius = context.resources.getDimension(R.dimen.dimen_12dp).toInt()

    var onBackupClickListener: (() -> Unit)? = null
    var onShareClickListener: (() -> Unit)? = null

    init {
        View.inflate(context, R.layout.view_wallet_card, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        (context.applicationContext as MewApplication).appComponent.inject(this)

        val statusBarHeight = ApplicationUtils.getStatusBarHeight(context)
        expandedY = statusBarHeight + resources.getDimension(R.dimen.wallet_toolbar_height).toInt()
        collapsedHeight = expandedY + resources.getDimension(R.dimen.dimen_32dp).toInt()

        val layoutParams = wallet_card_content.layoutParams
        layoutParams.width = expandedWidth
        layoutParams.height = expandedHeight
        wallet_card_content.layoutParams = layoutParams

        CardBackgroundHelper.setImage(wallet_card_background, preferences.applicationPreferences.getCurrentNetwork())

        if (preferences.applicationPreferences.isBackedUp()) {
            wallet_card_backup_container.visibility = View.GONE
            wallet_card_backed_up_container.visibility = View.VISIBLE
        } else {
            wallet_card_backup_container.visibility = View.VISIBLE
            wallet_card_backed_up_container.visibility = View.GONE
        }

        wallet_card_back_up.setOnClickListener { onBackupClickListener?.invoke() }
    }

    override fun setRatio(ratio: Float) {
        y = expandedY * ratio
        val params = layoutParams
        params.width = (collapsedWidth - (collapsedWidth - expandedWidth) * ratio).toInt()
        params.height = (collapsedHeight + (expandedHeight - collapsedHeight) * ratio).toInt()
        layoutParams = params
        radius = (cornerRadius * ratio).roundToInt().toFloat() // Round to remove alpha effect when radius less but not equal 0
        wallet_card_content.alpha = ratio
    }

    fun setContentVisible(isVisible: Boolean) {
        wallet_card_content.visibility = if (isVisible) VISIBLE else GONE
    }

    fun setAddress(address: String) {
        wallet_card_address.getSize { _, fieldWidth, _ ->
            var text = HexUtils.withPrefix(Keys.toChecksumAddress(address))
            val paint = wallet_card_address.paint
            do {
                text = text.replace(ADDRESS_ELLIPSIS, "")
                val half = text.length / 2
                text = text.substring(0, half) + ADDRESS_ELLIPSIS + text.substring(half + 1)
            } while (paint.measureText(text) > fieldWidth && text.length > 5)
            wallet_card_address.text = text
            wallet_card_address_icon.setOnClickListener { onShareClickListener?.invoke() }
        }
    }

    fun isEmpty() = wallet_card_value_eth.text.isEmpty()

    fun setBalance(walletBalance: WalletBalance, network: Network) {
        with(walletBalance) {
            val currency = network.getCurrency(context)
            wallet_card_value_eth.text = value.formatMoney(5)
            wallet_card_currency_eth.text = currency
            if (network == Network.ROPSTEN) {
                wallet_card_value_usd.text = context.getString(R.string.wallet_card_test_network)
                wallet_card_stock_price.visibility = View.GONE
                wallet_card_currency_usd.visibility = View.GONE
                wallet_card_address_title.setText(R.string.wallet_card_address_title_ropsten)
            } else {
                wallet_card_value_usd.text = valueUsd.formatUsd()
                wallet_card_stock_price.text = context.getString(R.string.wallet_card_stock_price,
                        (stockPrice ?: BigDecimal.ZERO).stripTrailingZeros(),
                        currency)
                wallet_card_stock_price.visibility = View.VISIBLE
                wallet_card_currency_usd.visibility = View.VISIBLE
                wallet_card_address_title.setText(R.string.wallet_card_address_title_eth)
            }
        }
    }
}