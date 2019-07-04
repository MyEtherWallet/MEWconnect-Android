package com.myetherwallet.mewconnect.feature.main.fragment

import android.os.Bundle
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import kotlinx.android.synthetic.main.fragment_fingerprint_enabled.*

/**
 * Created by BArtWell on 29.05.2019.
 */

class FingerprintEnabledFragment : BaseDiFragment() {

    companion object {
        fun newInstance() = FingerprintEnabledFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fingerprint_enabled_done.setOnClickListener { close() }
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_fingerprint_enabled
}