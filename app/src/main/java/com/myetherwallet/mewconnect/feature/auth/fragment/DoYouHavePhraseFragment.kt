package com.myetherwallet.mewconnect.feature.auth.fragment

import android.os.Bundle
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import kotlinx.android.synthetic.main.fragment_do_you_have_phrase.*
import kotlinx.android.synthetic.main.fragment_restore_existing_wallet.*

/**
 * Created by BArtWell on 21.02.2019.
 */
class DoYouHavePhraseFragment : BaseDiFragment() {

    companion object {
        fun newInstance() = DoYouHavePhraseFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        do_you_have_phrase_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        do_you_have_phrase_toolbar.setNavigationOnClickListener(View.OnClickListener { close() })

        do_you_have_phrase_continue.setOnClickListener {
            addFragment(EnterRecoveryPhraseFragment.newInstance())
        }
        do_you_have_phrase_other.setOnClickListener {
            addFragment(SafetyPriorityFragment.newInstance())
        }
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_do_you_have_phrase
}