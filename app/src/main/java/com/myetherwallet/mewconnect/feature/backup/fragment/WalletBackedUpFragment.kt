package com.myetherwallet.mewconnect.feature.backup.fragment

import android.os.Bundle
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.extenstion.getString
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import com.myetherwallet.mewconnect.feature.main.fragment.WalletFragment
import kotlinx.android.synthetic.main.fragment_wallet_backed_up.*

/**
 * Created by BArtWell on 15.08.2018.
 */

private const val EXTRA_PASSWORD = "password"

class WalletBackedUpFragment : BaseDiFragment() {

    companion object {
        fun newInstance(password: String): WalletBackedUpFragment {
            val fragment = WalletBackedUpFragment()
            val arguments = Bundle()
            arguments.putString(EXTRA_PASSWORD, password)
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getString(EXTRA_PASSWORD)?.let { password ->
            wallet_backed_up_done.setOnClickListener { replaceFragment(WalletFragment.newInstance()) }
        }
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_wallet_backed_up
}