package com.myetherwallet.mewconnect.feature.main.fragment

import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.myetherwallet.mewconnect.BuildConfig
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import com.myetherwallet.mewconnect.core.utils.ApplicationUtils
import com.myetherwallet.mewconnect.core.utils.LaunchUtils
import com.myetherwallet.mewconnect.feature.main.dialog.ResetWalletDialog
import kotlinx.android.synthetic.main.fragment_info.*
import javax.inject.Inject

/**
 * Created by BArtWell on 15.08.2018.
 */
class InfoFragment : BaseDiFragment(), Toolbar.OnMenuItemClickListener {

    companion object {
        fun newInstance() = InfoFragment()
    }

    @Inject
    lateinit var preferences: PreferencesManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        info_toolbar.inflateMenu(R.menu.close)
        info_toolbar.setOnMenuItemClickListener(this)

        info_contact.setOnClickListener { LaunchUtils.openMailApp(context, "support@myetherwallet.com") }
        info_user_guide.setOnClickListener { LaunchUtils.openWebSite(context, "https://kb.myetherwallet.com/getting-started/mew-connect-user-guide.html") }
        info_knowledge_base.setOnClickListener { LaunchUtils.openWebSite(context, "https://myetherwallet.github.io/knowledge-base/") }
        info_privacy_and_terms.setOnClickListener { LaunchUtils.openWebSite(context, "https://www.myetherwallet.com/privacy-policy.html") }
        info_site.setOnClickListener { LaunchUtils.openWebSite(context, "https://www.myetherwallet.com") }

        info_version.text = getString(R.string.info_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

        info_reset_wallet.setOnClickListener {
            val dialog = ResetWalletDialog.newInstance()
            dialog.listener = {
                ApplicationUtils.removeAllData(context, preferences)
                replaceFragment(IntroFragment.newInstance())
            }
            dialog.show(childFragmentManager)
        }
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        close()
        return true
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_info
}