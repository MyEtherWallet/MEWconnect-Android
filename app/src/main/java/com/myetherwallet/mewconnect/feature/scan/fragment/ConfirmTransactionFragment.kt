package com.myetherwallet.mewconnect.feature.scan.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.content.data.Transaction
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.extenstion.formatMoney
import com.myetherwallet.mewconnect.core.extenstion.formatUsd
import com.myetherwallet.mewconnect.core.extenstion.toEthValue
import com.myetherwallet.mewconnect.core.extenstion.viewModel
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.ui.fragment.BaseViewModelFragment
import com.myetherwallet.mewconnect.feature.auth.callback.AuthCallback
import com.myetherwallet.mewconnect.feature.auth.fragment.AuthFragment
import com.myetherwallet.mewconnect.feature.register.utils.EmoticonHelper
import com.myetherwallet.mewconnect.feature.scan.viewmodel.ConfirmTransactionViewModel
import kotlinx.android.synthetic.main.fragment_confirm_transaction.*
import java.math.BigDecimal
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = viewModel()
        val transaction = arguments?.getParcelable<Transaction>(EXTRA_TRANSACTION)
        transaction?.let {
            viewModel.transaction = transaction
            val currency = preferences.applicationPreferences.getCurrentNetwork().getCurrency(requireContext())
            confirm_transaction_amount.text = transaction.value.toEthValue().formatMoney(5, currency)
            confirm_transaction_wallet_address.text = transaction.to
            confirm_transaction_wallet_emoticon.setImageBitmap(EmoticonHelper.draw(transaction.to, resources.getDimension(R.dimen.dimen_32dp).toInt()))
            confirm_transaction_ok.setOnClickListener { _ ->
                val authFragment = AuthFragment.newInstance()
                authFragment.setTargetFragment(this, AUTH_REQUEST_CODE)
                addFragment(authFragment)
            }
        }
        confirm_transaction_cancel.setOnClickListener {
            addOrReplaceFragment(TransactionDeclinedFragment.newInstance(), TAG)
        }

        arguments?.getSerializable(EXTRA_PRICE)?.let {
            confirm_transaction_amount_price.text = (it as BigDecimal).multiply(transaction?.value?.toEthValue()).formatUsd()
        }

        updateCheckBoxState(confirm_transaction_wallet_container, false)
        updateCheckBoxState(confirm_transaction_amount_container, false)
        updateOkButtonState()

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
        val isAllChecked = confirm_transaction_wallet_checkbox.isChecked && confirm_transaction_amount_checkbox.isChecked
        confirm_transaction_ok.isEnabled = isAllChecked
    }

    override fun onAuthResult(password: String) {
        viewModel.confirmTransaction(preferences, password)
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