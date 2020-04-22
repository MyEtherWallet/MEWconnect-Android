package com.myetherwallet.mewconnect.feature.main.fragment

import android.os.Bundle
import android.os.Handler
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.ui.fragment.BaseFragment
import com.myetherwallet.mewconnect.feature.auth.fragment.RestoreExistingWalletFragment
import com.myetherwallet.mewconnect.feature.register.fragment.password.PickPasswordFragment
import kotlinx.android.synthetic.main.fragment_intro.*

class IntroFragment : BaseFragment() {

    companion object {

        fun newInstance() = IntroFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        intro_setup.setOnClickListener { addFragment(PickPasswordFragment.newInstance()) }
        intro_restore_wallet.setOnClickListener { addFragment(RestoreExistingWalletFragment.newInstance()) }
    }

    override fun layoutId() = R.layout.fragment_intro
}