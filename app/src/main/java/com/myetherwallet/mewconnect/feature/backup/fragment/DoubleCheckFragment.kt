package com.myetherwallet.mewconnect.feature.backup.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.extenstion.getString
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import com.myetherwallet.mewconnect.core.utils.crypto.StorageCryptHelper
import com.myetherwallet.mewconnect.feature.backup.adapter.DoubleCheckAdapter
import kotlinx.android.synthetic.main.fragment_double_check.*
import javax.inject.Inject

/**
 * Created by BArtWell on 15.08.2018.
 */

private const val EXTRA_PASSWORD = "password"

class DoubleCheckFragment : BaseDiFragment(), View.OnClickListener, Toolbar.OnMenuItemClickListener {

    companion object {
        fun newInstance(password: String): DoubleCheckFragment {
            val fragment = DoubleCheckFragment()
            val arguments = Bundle()
            arguments.putString(EXTRA_PASSWORD, password)
            fragment.arguments = arguments
            return fragment
        }
    }

    @Inject
    lateinit var preferences: PreferencesManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        double_check_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        double_check_toolbar.setNavigationOnClickListener(this)
        double_check_toolbar.inflateMenu(R.menu.done)
        double_check_toolbar.setOnMenuItemClickListener(this)

        var mnemonicBytes: ByteArray? = null
        getString(EXTRA_PASSWORD)?.let { password ->
            mnemonicBytes = StorageCryptHelper.decrypt(preferences.applicationPreferences.getWalletMnemonic(), password)
        }

        setDoneButtonEnabled(false)

        val layoutManager = LinearLayoutManager(context)
        double_check_list.layoutManager = layoutManager
        val adapter = DoubleCheckAdapter(::setDoneButtonEnabled)
        double_check_list.adapter = adapter
        double_check_list.isNestedScrollingEnabled = false
        double_check_list.setHasFixedSize(false)

        mnemonicBytes?.let {
            val mnemonic = String(it)
            adapter.setItems(mnemonic.split(" "))
        } ?: Toast.makeText(context, R.string.words_loading_error, Toast.LENGTH_LONG).show()
    }

    private fun setDoneButtonEnabled(isEnabled: Boolean) {
        double_check_toolbar.getMenu().findItem(R.id.done).isEnabled = isEnabled
        val icon = if (isEnabled) R.drawable.ic_action_done_enabled else R.drawable.ic_action_done_disabled
        double_check_toolbar.getMenu().findItem(R.id.done).setIcon(icon)
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        preferences.applicationPreferences.setBackedUp(true)
        getString(EXTRA_PASSWORD)?.let { password ->
            replaceFragment(WalletBackedUpFragment.newInstance(password))
        }
        return true
    }

    override fun onClick(view: View) {
        close()
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_double_check
}