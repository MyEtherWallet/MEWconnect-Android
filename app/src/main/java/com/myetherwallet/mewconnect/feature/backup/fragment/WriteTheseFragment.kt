package com.myetherwallet.mewconnect.feature.backup.fragment

import android.os.Bundle
import android.view.View
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.ui.fragment.BaseMnemonicListFragment
import kotlinx.android.synthetic.main.fragment_mnemonic_list.*

/**
 * Created by BArtWell on 15.08.2018.
 */

class WriteTheseFragment : BaseMnemonicListFragment() {

    companion object {
        fun newInstance() = WriteTheseFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mnemonic_list_toolbar.inflateMenu(R.menu.forward)
        mnemonic_list_toolbar.setOnMenuItemClickListener(this)
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun getTitle() = R.string.write_these_title
}