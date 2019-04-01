package com.myetherwallet.mewconnect.feature.backup.fragment

import android.os.Bundle
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import kotlinx.android.synthetic.main.fragment_wallet_backed_up.*

/**
 * Created by BArtWell on 15.08.2018.
 */

class WalletBackedUpFragment : BaseDiFragment() {

    companion object {
        fun newInstance() = WalletBackedUpFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wallet_backed_up_done.setOnClickListener { close() }
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_wallet_backed_up
}