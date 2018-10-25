package com.myetherwallet.mewconnect.feature.scan.fragment

import android.os.Bundle
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import kotlinx.android.synthetic.main.fragment_transaction_confirmed.*


/**
 * Created by BArtWell on 11.07.2018.
 */

class TransactionConfirmedFragment : BaseDiFragment() {

    companion object {

        fun newInstance() = TransactionConfirmedFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transaction_confirmed_close.setOnClickListener { close() }
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_transaction_confirmed
}