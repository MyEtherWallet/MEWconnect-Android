package com.myetherwallet.mewconnect.core.ui.fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.utils.MewLog
import com.myetherwallet.mewconnect.core.utils.crypto.StorageCryptHelper
import com.myetherwallet.mewconnect.feature.auth.callback.AuthCallback
import com.myetherwallet.mewconnect.feature.auth.fragment.AuthFragment
import com.myetherwallet.mewconnect.feature.backup.adapter.WriteTheseWordsAdapter
import com.myetherwallet.mewconnect.feature.backup.fragment.DoubleCheckFragment
import com.myetherwallet.mewconnect.feature.backup.fragment.WriteTheseFragment
import kotlinx.android.synthetic.main.fragment_mnemonic_list.*
import javax.inject.Inject

/**
 * Created by BArtWell on 20.02.2019.
 */

private const val AUTH_REQUEST_CODE = 101

abstract class BaseMnemonicListFragment : BaseDiFragment(), View.OnClickListener, Toolbar.OnMenuItemClickListener, AuthCallback {

    companion object {
        const val TAG = "WriteTheseFragment"

        fun newInstance() = WriteTheseFragment()
    }

    @Inject
    lateinit var preferences: PreferencesManager
    private var password: String? = null
    private var isAuthCanceled = false
    val adapter = WriteTheseWordsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mnemonic_list_title.setText(getTitle())

        mnemonic_list_toolbar.setNavigationIcon(R.drawable.ic_action_back)
        mnemonic_list_toolbar.setNavigationOnClickListener(this)

        val layoutManager = GridLayoutManager(context, 2)
        mnemonic_list_words.layoutManager = layoutManager
        mnemonic_list_words.adapter = adapter
        mnemonic_list_words.isNestedScrollingEnabled = false
        mnemonic_list_words.setHasFixedSize(false)
    }

    override fun onResume() {
        super.onResume()
        if (isAuthCanceled) {
            return
        }
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
        MewLog.d(TAG, "Auth success")
        this.password = password
        val mnemonicBytes = StorageCryptHelper.decrypt(preferences.applicationPreferences.getWalletMnemonic(), password)
        mnemonicBytes?.let {
            val mnemonic = String(it)
            adapter.setItems(mnemonic.split(" "))
        } ?: Toast.makeText(context, R.string.words_loading_error, Toast.LENGTH_LONG).show()
        close()
    }

    override fun onAuthCancel() {
        MewLog.d(TAG, "Auth canceled")
        isAuthCanceled = true
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

    abstract fun getTitle(): Int

    override fun layoutId() = R.layout.fragment_mnemonic_list
}