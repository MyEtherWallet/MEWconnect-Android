package com.myetherwallet.mewconnect.feature.main.fragment

import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.ui.fragment.BaseMnemonicListFragment

/**
 * Created by BArtWell on 20.02.2019.
 */
class ViewRecoveryPhraseFragment : BaseMnemonicListFragment() {

    companion object {
        fun newInstance() = ViewRecoveryPhraseFragment()
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun getTitle() = R.string.view_recovery_phrase_title
}