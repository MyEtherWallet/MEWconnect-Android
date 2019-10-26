package com.myetherwallet.mewconnect.feature.scan.fragment

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.content.data.Transaction
import com.myetherwallet.mewconnect.content.data.TransactionData
import com.myetherwallet.mewconnect.content.data.TransactionNetwork
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.extenstion.formatMoney
import com.myetherwallet.mewconnect.core.extenstion.formatUsd
import com.myetherwallet.mewconnect.core.extenstion.toEthValue
import com.myetherwallet.mewconnect.core.extenstion.viewModel
import com.myetherwallet.mewconnect.core.persist.prefenreces.KeyStore
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.ui.fragment.BaseViewModelFragment
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.encrypt.BaseEncryptHelper
import com.myetherwallet.mewconnect.feature.auth.callback.AuthCallback
import com.myetherwallet.mewconnect.feature.auth.fragment.AuthFragment
import com.myetherwallet.mewconnect.feature.register.utils.EmoticonHelper
import com.myetherwallet.mewconnect.feature.scan.viewmodel.ConfirmTransactionViewModel
import kotlinx.android.synthetic.main.fragment_confirm_transaction.*
import java.math.BigDecimal
import java.math.BigInteger
import javax.inject.Inject

/**
 * Created by BArtWell on 11.07.2018.
 */

private const val EXTRA_TRANSACTION = "transaction"
private const val EXTRA_PRICE = "price"
private const val AUTH_REQUEST_CODE = 101

class ConfirmTransactionFragment : BaseViewModelFragment(), AuthCallback {

    companion object {

        const val TAG = "ConfirmTransactionFragment"

        fun newInstance(transaction: Transaction, price: BigDecimal?): ConfirmTransactionFragment {
            val fragment = ConfirmTransactionFragment()
            val arguments = Bundle()
            arguments.putParcelable(EXTRA_TRANSACTION, transaction)
            price?.let { arguments.putSerializable(EXTRA_PRICE, price) }
            fragment.arguments = arguments
            return fragment
        }
    }

    @Inject
    lateinit var preferences: PreferencesManager
    private lateinit var viewModel: ConfirmTransactionViewModel
    private var shouldConfirmNetwork = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = viewModel()
        val transaction = arguments?.getParcelable<Transaction>(EXTRA_TRANSACTION)
        var isUnknownToken = false
        transaction?.let {
            viewModel.transaction = transaction
            val transactionData = TransactionData.fromString(transaction.data)
            isUnknownToken = transactionData?.function == TransactionData.FUNCTION_TOKEN_TRANSFER
            val currency: String
            val amount: BigDecimal
            val to: String?
            if (transaction.value == BigInteger.ZERO && transactionData?.function == TransactionData.FUNCTION_TOKEN_TRANSFER) {
                if (transaction.currency == null) {
                    amount = transactionData.amount.toBigDecimal().stripTrailingZeros()
                    currency = getString(R.string.confirm_transaction_unknown_token)
                } else {
                    amount = transactionData.amount.toEthValue(transaction.currency.decimals)
                    currency = transaction.currency.symbol
                }
                to = transactionData.address
            } else {
                amount = transaction.value.toEthValue()
                currency = preferences.applicationPreferences.getCurrentNetwork().getCurrency(requireContext())
                to = transaction.to
            }
            confirm_transaction_amount.text = amount.formatMoney(5, currency)
            confirm_transaction_wallet_address.text = to

            shouldConfirmNetwork = transaction.chainId != preferences.applicationPreferences.getCurrentNetwork().chainId.toLong()
            if (shouldConfirmNetwork) {
                confirm_transaction_network_container.visibility = VISIBLE
                confirm_transaction_network.setText(TransactionNetwork.findByChaidId(transaction.chainId)?.title
                        ?: R.string.transaction_network_unknown)
            } else {
                confirm_transaction_network_container.visibility = GONE
            }

            to?.let {
                confirm_transaction_wallet_emoticon.setImageBitmap(EmoticonHelper.draw(to, resources.getDimension(R.dimen.dimen_32dp).toInt()))
            }
            confirm_transaction_ok.setOnClickListener {
                val authFragment = AuthFragment.newInstance()
                authFragment.setTargetFragment(this, AUTH_REQUEST_CODE)
                addFragment(authFragment)
            }
        }
        confirm_transaction_cancel.setOnClickListener {
            addOrReplaceFragment(TransactionDeclinedFragment.newInstance(), TAG)
        }

        val price = arguments?.getSerializable(EXTRA_PRICE) as BigDecimal?
        if (price == null || isUnknownToken) {
            confirm_transaction_amount_price.visibility = GONE
        } else {
            confirm_transaction_amount_price.text = price.multiply(transaction?.value?.toEthValue()).formatUsd()
            confirm_transaction_amount_price.visibility = VISIBLE
        }

        updateCheckBoxState(confirm_transaction_network_container, false)
        updateCheckBoxState(confirm_transaction_wallet_container, false)
        updateCheckBoxState(confirm_transaction_amount_container, false)
        updateOkButtonState()

        confirm_transaction_network_clickable.setOnClickListener {
            confirm_transaction_network_checkbox.isChecked = !confirm_transaction_network_checkbox.isChecked
            updateCheckBoxState(confirm_transaction_network_container, confirm_transaction_network_checkbox.isChecked)
            updateOkButtonState()
        }

        confirm_transaction_wallet_clickable.setOnClickListener {
            confirm_transaction_wallet_checkbox.isChecked = !confirm_transaction_wallet_checkbox.isChecked
            updateCheckBoxState(confirm_transaction_wallet_container, confirm_transaction_wallet_checkbox.isChecked)
            updateOkButtonState()
        }

        confirm_transaction_amount_clickable.setOnClickListener {
            confirm_transaction_amount_checkbox.isChecked = !confirm_transaction_amount_checkbox.isChecked
            updateCheckBoxState(confirm_transaction_amount_container, confirm_transaction_amount_checkbox.isChecked)
            updateOkButtonState()
        }
    }

    private fun updateCheckBoxState(container: ViewGroup, isChecked: Boolean) {
        container.isEnabled = isChecked
    }

    private fun updateOkButtonState() {
        val isAllChecked = (!shouldConfirmNetwork || confirm_transaction_network_checkbox.isChecked) && confirm_transaction_wallet_checkbox.isChecked && confirm_transaction_amount_checkbox.isChecked
        confirm_transaction_ok.isEnabled = isAllChecked
    }

    override fun onAuthResult(helper: BaseEncryptHelper, keyStore: KeyStore) {
        viewModel.confirmTransaction(preferences, helper, keyStore)
        addOrReplaceFragment(TransactionConfirmedFragment.newInstance(), TAG)
    }

    override fun onAuthCancel() {
        addOrReplaceFragment(TransactionDeclinedFragment.newInstance(), TAG)
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_confirm_transaction
}