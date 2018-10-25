package com.myetherwallet.mewconnect.feature.scan.fragment

import android.os.Bundle
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import kotlinx.android.synthetic.main.fragment_message_sidned.*

/**
 * Created by BArtWell on 30.09.2018.
 */
class MessageSignedFragment : BaseDiFragment() {

    companion object {

        fun newInstance() = MessageSignedFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        message_sidned_close.setOnClickListener { close() }
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_message_sidned
}