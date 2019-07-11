package com.myetherwallet.mewconnect.feature.backup.fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.extenstion.getString
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import com.myetherwallet.mewconnect.feature.backup.adapter.DoubleCheckAdapter
import kotlinx.android.synthetic.main.fragment_double_check.*
import javax.inject.Inject

/**
 * Created by BArtWell on 15.08.2018.
 */

private const val EXTRA_MNEMONIC = "mnemonic"

class DoubleCheckFragment : BaseDiFragment(), View.OnClickListener, Toolbar.OnMenuItemClickListener {

    companion object {
        fun newInstance(mnemonic: String): DoubleCheckFragment {
            val fragment = DoubleCheckFragment()
            val arguments = Bundle()
            arguments.putString(EXTRA_MNEMONIC, mnemonic)
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

        setDoneButtonEnabled(false)

        val layoutManager = LinearLayoutManager(context)
        double_check_list.layoutManager = layoutManager
        val adapter = DoubleCheckAdapter(::setDoneButtonEnabled)
        double_check_list.adapter = adapter
        double_check_list.isNestedScrollingEnabled = false
        double_check_list.setHasFixedSize(false)

        getString(EXTRA_MNEMONIC)?.let { mnemonic ->
            adapter.setItems(mnemonic.split(" "))
        } ?: Toast.makeText(context, R.string.words_loading_error, Toast.LENGTH_LONG).show()
    }

    private fun setDoneButtonEnabled(isEnabled: Boolean) {
        addOnResumeListener {
            double_check_toolbar.getMenu().findItem(R.id.done).isEnabled = isEnabled
            val icon = if (isEnabled) R.drawable.ic_action_done_enabled else R.drawable.ic_action_done_disabled
            double_check_toolbar.getMenu().findItem(R.id.done).setIcon(icon)
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        preferences.applicationPreferences.setBackedUp(true)
        closeToFirst()
        addFragment(WalletBackedUpFragment.newInstance())
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