package com.myetherwallet.mewconnect.feature.register.fragment

import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.extenstion.getString
import com.myetherwallet.mewconnect.core.extenstion.viewModel
import com.myetherwallet.mewconnect.core.ui.fragment.BaseViewModelFragment
import com.myetherwallet.mewconnect.core.utils.ApplicationUtils
import com.myetherwallet.mewconnect.feature.main.fragment.WalletFragment
import com.myetherwallet.mewconnect.feature.register.viewmodel.GeneratingViewModel
import kotlinx.android.synthetic.main.fragment_generating.*

private const val EXTRA_PASSWORD = "password"
private const val EXTRA_MNEMONIC = "mnemonic"

class GeneratingFragment : BaseViewModelFragment() {

    companion object {
        fun newInstance(password: String, mnemonic: String?): GeneratingFragment {
            val fragment = GeneratingFragment()
            val arguments = Bundle()
            arguments.putString(EXTRA_PASSWORD, password)
            mnemonic?.let {
                arguments.putString(EXTRA_MNEMONIC, mnemonic)
            }
            fragment.arguments = arguments
            return fragment
        }
    }

    private lateinit var viewModel: GeneratingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val strings = resources.getStringArray(R.array.generating_animation_strings)
        generating_content.listener = { if (generating_start_using != null) generating_start_using.visibility = VISIBLE }
        generating_content.addStrings(strings)
        generating_start_using.setOnClickListener { replaceFragment(WalletFragment.newInstance()) }

        viewModel = viewModel(viewModelFactory)
        val displaySize = ApplicationUtils.getDisplaySize(requireContext())
        viewModel.createWallets(getString(EXTRA_PASSWORD)!!, getString(EXTRA_MNEMONIC), displaySize.width)
    }

    override fun layoutId() = R.layout.fragment_generating

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }
}