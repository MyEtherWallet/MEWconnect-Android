package com.myetherwallet.mewconnect.feature.backup.fragment

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import kotlinx.android.synthetic.main.fragment_back_up_wallet.*

/**
 * Created by BArtWell on 15.08.2018.
 */

class BackUpWalletFragment : BaseDiFragment(), Toolbar.OnMenuItemClickListener {

    companion object {
        fun newInstance() = BackUpWalletFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_up_wallet_toolbar.inflateMenu(R.menu.close)
        back_up_wallet_toolbar.setOnMenuItemClickListener(this)

        back_up_wallet_confirm.setOnClickListener {
            addFragment(PrepareWriteFragment.newInstance())
        }
    }

    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        close()
        return true
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_back_up_wallet
}