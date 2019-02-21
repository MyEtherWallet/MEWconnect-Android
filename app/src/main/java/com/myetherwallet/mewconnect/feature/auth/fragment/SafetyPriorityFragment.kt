package com.myetherwallet.mewconnect.feature.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import kotlinx.android.synthetic.main.fragment_do_you_have_phrase.*
import kotlinx.android.synthetic.main.fragment_safety_priority.*

/**
 * Created by BArtWell on 21.02.2019.
 */
class SafetyPriorityFragment : BaseDiFragment() {

    companion object {
        fun newInstance() = SafetyPriorityFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        safety_priority_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        safety_priority_toolbar.setNavigationOnClickListener(View.OnClickListener { close() })
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_safety_priority
}