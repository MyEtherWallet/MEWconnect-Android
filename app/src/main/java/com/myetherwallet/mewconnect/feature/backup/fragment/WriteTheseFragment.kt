package com.myetherwallet.mewconnect.feature.backup.fragment

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import com.myetherwallet.mewconnect.core.utils.crypto.StorageCryptHelper
import com.myetherwallet.mewconnect.feature.auth.callback.AuthCallback
import com.myetherwallet.mewconnect.feature.auth.fragment.AuthFragment
import com.myetherwallet.mewconnect.feature.backup.adapter.WriteTheseWordsAdapter
import kotlinx.android.synthetic.main.fragment_write_these.*
import javax.inject.Inject

/**
 * Created by BArtWell on 15.08.2018.
 */

private const val AUTH_REQUEST_CODE = 101

class WriteTheseFragment : BaseDiFragment(), View.OnClickListener, Toolbar.OnMenuItemClickListener, AuthCallback {

    companion object {
        const val TAG = "WriteTheseFragment"

        fun newInstance() = WriteTheseFragment()
    }

    @Inject
    lateinit var preferences: PreferencesManager
    private var password: String? = null
    val adapter = WriteTheseWordsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        write_these_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        write_these_toolbar.setNavigationOnClickListener(this)
        write_these_toolbar.inflateMenu(R.menu.forward)
        write_these_toolbar.setOnMenuItemClickListener(this)

        val layoutManager = GridLayoutManager(context, 2)
        write_these_words.layoutManager = layoutManager
        write_these_words.adapter = adapter
        write_these_words.isNestedScrollingEnabled = false
        write_these_words.setHasFixedSize(false)
    }

    override fun onResume() {
        super.onResume()
        setWindowSecure(true)
        if (password == null) {
            val authFragment = AuthFragment.newInstance()
            authFragment.setTargetFragment(this, AUTH_REQUEST_CODE)
            addFragment(authFragment)
        }
    }

    override fun onPause() {
        password = null
        setWindowSecure(false)
        super.onPause()
    }

    override fun onAuthResult(password: String) {
        this.password = password
        val mnemonicBytes = StorageCryptHelper.decrypt(preferences.applicationPreferences.getWalletMnemonic(), password)
        mnemonicBytes?.let {
            val mnemonic = String(it)
            adapter.setItems(mnemonic.split(" "))
        } ?: Toast.makeText(context, R.string.words_loading_error, Toast.LENGTH_LONG).show()
        close()
    }

    override fun onAuthCancel() {
        close()
    }

    override fun onMenuItemClick(menuItem: MenuItem): Boolean {
        password?.let {
            addFragment(DoubleCheckFragment.newInstance(it))
        }
        return true
    }

    override fun onClick(view: View) {
        close()
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_write_these
}