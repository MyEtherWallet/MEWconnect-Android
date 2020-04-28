package com.myetherwallet.mewconnect.feature.main.fragment

import android.os.Bundle
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.content.data.AnalyticsEvent
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.extenstion.viewModel
import com.myetherwallet.mewconnect.core.ui.fragment.BaseViewModelFragment
import com.myetherwallet.mewconnect.feature.auth.fragment.RestoreExistingWalletFragment
import com.myetherwallet.mewconnect.feature.main.utils.MewWalletUtils
import com.myetherwallet.mewconnect.feature.main.viewmodel.IntroViewModel
import com.myetherwallet.mewconnect.feature.register.fragment.password.PickPasswordFragment
import kotlinx.android.synthetic.main.fragment_intro.*

class IntroFragment : BaseViewModelFragment() {

    private lateinit var viewModel: IntroViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        intro_setup.setOnClickListener { addFragment(PickPasswordFragment.newInstance()) }
        intro_restore_wallet.setOnClickListener { addFragment(RestoreExistingWalletFragment.newInstance()) }
        intro_mewwallet_button.setOnClickListener {
            MewWalletUtils.launchAppOrMarket(requireContext())
            viewModel.submitEvents(context, AnalyticsEvent(AnalyticsEvent.INTRO_BANNER_FREE_UPGRADE_CLICKED))
        }

        viewModel = viewModel(viewModelFactory)
        viewModel.submitEvents(context, AnalyticsEvent(AnalyticsEvent.INTRO_BANNER_SHOWN))
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_intro

    companion object {
        fun newInstance() = IntroFragment()
    }
}
