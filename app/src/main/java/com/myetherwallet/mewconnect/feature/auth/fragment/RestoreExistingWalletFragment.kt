package com.myetherwallet.mewconnect.feature.auth.fragment

import android.os.Bundle
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import kotlinx.android.synthetic.main.fragment_restore_existing_wallet.*

/**
 * Created by BArtWell on 20.02.2019.
 */

class RestoreExistingWalletFragment : BaseDiFragment() {

    companion object {
        fun newInstance() = RestoreExistingWalletFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restore_existing_wallet_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        restore_existing_wallet_toolbar.setNavigationOnClickListener(View.OnClickListener { close() })

        restore_existing_wallet_use.setOnClickListener {
            addFragment(DoYouHavePhraseFragment.newInstance())
        }
        restore_existing_wallet_other.setOnClickListener {
            addFragment(SafetyPriorityFragment.newInstance())
        }
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_restore_existing_wallet
}