package com.myetherwallet.mewconnect.feature.backup.fragment

import android.os.Bundle
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import kotlinx.android.synthetic.main.fragment_prepare_write.*

/**
 * Created by BArtWell on 15.08.2018.
 */

class PrepareWriteFragment : BaseDiFragment(), View.OnClickListener {

    companion object {
        fun newInstance() = PrepareWriteFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepare_write_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        prepare_write_toolbar.setNavigationOnClickListener(this)

        prepare_write_confirm.setOnClickListener {
            addFragment(WriteTheseFragment.newInstance())
        }
    }

    override fun onClick(view: View) {
        close()
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_prepare_write
}