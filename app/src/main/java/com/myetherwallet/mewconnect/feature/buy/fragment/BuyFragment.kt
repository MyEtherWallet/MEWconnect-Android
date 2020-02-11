package com.myetherwallet.mewconnect.feature.buy.fragment

import android.graphics.Rect
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.extenstion.*
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.ui.fragment.BaseViewModelFragment
import com.myetherwallet.mewconnect.core.utils.StringUtils
import com.myetherwallet.mewconnect.feature.buy.activity.BuyWebViewActivity
import com.myetherwallet.mewconnect.feature.buy.data.BuyQuoteResult
import com.myetherwallet.mewconnect.feature.buy.data.BuyResponse
import com.myetherwallet.mewconnect.feature.buy.viewmodel.BuyViewModel
import kotlinx.android.synthetic.main.fragment_buy.*
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import javax.inject.Inject

/**
 * Created by BArtWell on 12.09.2018.
 */

private const val CURRENCY_USD = "USD"
private const val CURRENCY_ETH = "ETH"
private const val ETH_DECIMALS = 8
private val LIMIT_MIN = BigDecimal(50)
private val LIMIT_MAX = BigDecimal(20000)

class BuyFragment : BaseViewModelFragment() {

    companion object {
        fun newInstance() = BuyFragment()
    }

    @Inject
    lateinit var preferences: PreferencesManager
    private lateinit var viewModel: BuyViewModel

    private var textSizeMin = 0f
    private var textSizeMax = 0f
    private var isInUsd = true
    private var price = BigDecimal.ZERO
    private var isShortSimplexDescription = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = viewModel(viewModelFactory) {
            observe(data, ::onQuoteLoaded)
        }
        viewModel.loadQuote()

        textSizeMin = resources.getDimension(R.dimen.text_size_fixed_20sp)
        textSizeMax = resources.getDimension(R.dimen.text_size_fixed_48sp)

        buy_toolbar.setNavigationIcon(R.drawable.ic_action_close)
        buy_toolbar.setNavigationOnClickListener(View.OnClickListener { close() })
        buy_toolbar.setTitle(R.string.buy_title)
        buy_toolbar.inflateMenu(R.menu.buy)
        buy_toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener {
            if (it.itemId == R.id.buy_history) {
                addFragment(HistoryFragment.newInstance())
                true
            } else {
                false
            }
        })

        buy_simplex_description.text = StringUtils.fromHtml(requireContext(), R.string.buy_simplex_description_full)
        buy_keyboard_container.getSize { _, _, _ ->
            val rect1 = Rect()
            buy_amount_container.getGlobalVisibleRect(rect1)
            val rect2 = Rect()
            buy_keyboard_container.getGlobalVisibleRect(rect2)
            if (rect1.bottom >= rect2.top) {
                isShortSimplexDescription = true
                buy_keyboard_margin.visibility = GONE
            }
            populateMainValue(BigDecimal(100))
        }

        buy_button_1.setOnClickListener { addDigit(1) }
        buy_button_2.setOnClickListener { addDigit(2) }
        buy_button_3.setOnClickListener { addDigit(3) }
        buy_button_4.setOnClickListener { addDigit(4) }
        buy_button_5.setOnClickListener { addDigit(5) }
        buy_button_6.setOnClickListener { addDigit(6) }
        buy_button_7.setOnClickListener { addDigit(7) }
        buy_button_8.setOnClickListener { addDigit(8) }
        buy_button_9.setOnClickListener { addDigit(9) }
        buy_button_point.setOnClickListener { addPoint() }
        buy_button_0.setOnClickListener { addDigit(0) }
        buy_button_delete.setOnClickListener { delete() }

        buy_button_delete.setOnLongClickListener {
            populateMainValue(BigDecimal.ZERO)
            true
        }

        buy_toggle_currency.setOnClickListener {
            isInUsd = !isInUsd
            val tmp = buy_sum_1.text
            setRawText(buy_sum_2.text.toString())
            buy_sum_2.text = tmp
            populateSecondValue()
        }

        buy_button.setOnClickListener {
            buy_loading.visibility = VISIBLE
            viewModel.preparePostRequest(BigDecimal(getCurrentValue()),
                    if (isInUsd) CURRENCY_USD else CURRENCY_ETH,
                    preferences.getCurrentWalletPreferences().getWalletAddress(),
                    preferences.applicationPreferences.getInstallTime(),
                    {
                        addOnResumeListener {
                            startActivity(BuyWebViewActivity.createIntent(requireContext(), it.url, it.getEncodedPostData()))
                            buy_loading.visibility = GONE
                        }
                    },
                    {
                        addOnResumeListener {
                            Toast.makeText(context, R.string.buy_loading_error, Toast.LENGTH_LONG).show()
                            buy_loading.visibility = GONE
                        }
                    })
        }
    }

    private fun onQuoteLoaded(data: BuyResponse<BuyQuoteResult>?) {
        data?.result?.fiatMoney?.baseAmount?.let {
            price = it
            populateSecondValue()
        }
    }

    private fun populateMainValue(value: BigDecimal) {
        if (isInUsd) {
            setRawText(value.formatMoney(2))
        } else {
            setRawText(value.toStringWithoutZeroes())
        }
        populateSecondValue()
    }

    private fun populateSecondValue() {
        val text = getCurrentValue()
        var amountInUsd = BigDecimal.ZERO
        if (price > BigDecimal.ZERO) {
            val amount = BigDecimal(text)
            var second: BigDecimal
            val decimals: Int
            if (isInUsd) {
                amountInUsd = amount
                second = amount
                        .minus(calculateFee(amount))
                        .divide(price, ETH_DECIMALS, RoundingMode.HALF_UP)
                decimals = ETH_DECIMALS
            } else {
                second = amount
                        .multiply(price)
                        .plus(calculateFee(amount * price))
                decimals = 2
                amountInUsd = second
            }
            if (second < BigDecimal.ZERO) {
                second = BigDecimal.ZERO
            }
            buy_sum_2.text = second.formatMoney(decimals)
        } else {
            buy_sum_2.text = "0"
        }
        val feeText = if (amountInUsd.multiply(BigDecimal(0.05)) < BigDecimal.TEN) "$10" else "5%"
        val feeTextRes = if (isShortSimplexDescription) R.string.buy_simplex_description_short else R.string.buy_simplex_description_full
        buy_simplex_description.text = StringUtils.fromHtml(getString(feeTextRes, feeText))
        populateCurrency()
    }

    private fun getCurrentValue(): String {
        var text = buy_sum_1.text.toString()
        return if (text.isEmpty()) "0" else text
    }

    private fun calculateFee(amount: BigDecimal) =
            if (amount.compareTo(BigDecimal.ZERO) == 0) {
                BigDecimal.ZERO
            } else {
                if (amount < BigDecimal(210)) {
                    (BigDecimal.TEN
                            .divide(amount, 10, RoundingMode.HALF_UP)
                            .minus(BigDecimal(0.08).divide(amount, 10, RoundingMode.HALF_UP))
                            .plus(BigDecimal(0.01)))
                            .multiply(amount)
                            .minus(BigDecimal(0.03))
                } else {
                    BigDecimal(0.0566) * amount
                }
            }

    private fun populateCurrency() {
        if (isInUsd) {
            buy_currency_1.text = CURRENCY_USD
            buy_symbol_1.text = "$"
            buy_currency_2.text = CURRENCY_ETH
            buy_symbol_2.text = ""
        } else {
            buy_currency_1.text = CURRENCY_ETH
            buy_symbol_1.text = ""
            buy_currency_2.text = ""
            buy_symbol_2.text = "$"
        }
        setupBuyButton()
    }

    private fun setupBuyButton() {
        if (isInUsd || price > BigDecimal.ZERO) {
            var currentValue = BigDecimal(getCurrentValue())
            if (!isInUsd) {
                currentValue = currentValue.multiply(price)
            }
            if (currentValue < LIMIT_MIN) {
                buy_button.setText(R.string.buy_minimum_warning)
                buy_button.isEnabled = false
            } else {
                buy_button.setText(R.string.buy_button)
                buy_button.isEnabled = true
            }
        } else {
            buy_button.setText(R.string.buy_button)
            buy_button.isEnabled = true
        }
    }

    private fun delete() {
        var text = getCurrentValue()
        val length = text.length
        text = text.substring(0, length - 1)
        if (text.isEmpty()) {
            populateMainValue(BigDecimal.ZERO)
        } else {
            setRawText(text)
            populateSecondValue()
        }
    }

    private fun addPoint() {
        val text = getCurrentValue()
        val value = BigDecimal(text)
        if (value < LIMIT_MAX) {
            if (value.unscaledValue() == BigInteger.ZERO) {
                setRawText("0.")
                populateSecondValue()
            } else if (!text.contains(".")) {
                setRawText("$text.")
            }
        }
    }

    private fun setRawText(value: String) {
        buy_sum_1.text = value
        val delta = (textSizeMax - textSizeMin) * (value.length / 18f)
        val textSize = textSizeMax - delta
        buy_symbol_1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        buy_sum_1.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
    }

    private fun addDigit(digit: Int) {
        var text = getCurrentValue()
        if (BigDecimal(text).unscaledValue() == BigInteger.ZERO && !text.contains(".")) {
            text = ""
        }
        val length = text.length
        val newValue = text + digit
        if (isInUsd) {
            if (length >= 3 && text.substring(length - 3, length - 2) == ".") { // If already has 2 decimals
                return
            }
            if (BigDecimal(newValue) > LIMIT_MAX) {
                populateMainValue(LIMIT_MAX)
                return
            }
        } else {
            if (length >= ETH_DECIMALS + 1 && text.substring(length - ETH_DECIMALS - 1, length - ETH_DECIMALS) == ".") { // If already has 18 decimals
                return
            }
            val limit = if (price > BigDecimal.ZERO) {
                LIMIT_MAX.divide(price, ETH_DECIMALS, RoundingMode.HALF_UP)
            } else {
                BigDecimal(20000)
            }
            if (BigDecimal(newValue) > limit) {
                populateMainValue(limit)
                return
            }
        }
        setRawText(newValue)
        populateSecondValue()
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_buy
}
