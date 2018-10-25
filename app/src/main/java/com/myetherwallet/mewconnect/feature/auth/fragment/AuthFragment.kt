package com.myetherwallet.mewconnect.feature.auth.fragment

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.ui.callback.EmptyTextWatcher
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import com.myetherwallet.mewconnect.core.utils.KeyboardUtils
import com.myetherwallet.mewconnect.core.utils.crypto.StorageCryptHelper
import com.myetherwallet.mewconnect.feature.auth.callback.AuthCallback
import com.myetherwallet.mewconnect.feature.main.fragment.WalletFragment
import kotlinx.android.synthetic.main.fragment_auth.*
import org.web3j.crypto.ECKeyPair
import javax.inject.Inject

/**
 * Created by BArtWell on 13.08.2018.
 */

class AuthFragment : BaseDiFragment() {

    companion object {

        fun newInstance() = AuthFragment()
    }

    @Inject
    lateinit var preferences: PreferencesManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth_forgot_password.setOnClickListener { addFragment(ForgotPasswordFragment.newInstance()) }

        auth_password_text.addTextChangedListener(object : EmptyTextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                auth_password_input_layout.isErrorEnabled = false
            }
        })

        auth_password_text.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val password = auth_password_text.text.toString()
                val privateKey = StorageCryptHelper.decrypt(preferences.getCurrentWalletPreferences().getWalletPrivateKey(), password)
                if (checkPrivateKey(privateKey)) {
                    if (targetFragment == null) {
                        replaceFragment(WalletFragment.newInstance())
                    } else {
                        val target = targetFragment
                        if (target is AuthCallback) {
                            target.onAuthResult(password)
                        }
                    }
                } else {
                    auth_password_input_layout.error = getString(R.string.auth_forgot_password_error)
                }
                true
            } else {
                false
            }
        }

        if (targetFragment != null) {
            KeyboardUtils.showKeyboard(auth_password_text)
        }
    }

    private fun checkPrivateKey(privateKey: ByteArray?): Boolean {
        privateKey?.let {
            val ecKeyPair = ECKeyPair.create(it)
            if (ecKeyPair != null && ecKeyPair.publicKey != null) {
                return true
            }
        }
        return false
    }

    override fun onBackPressed(): Boolean {
        if (targetFragment != null) {
            val target = targetFragment
            if (target is AuthCallback) {
                close()
                target.onAuthCancel()
                return true
            }
        }
        return false
    }

    override fun inject(appComponent: ApplicationComponent) {
        appComponent.inject(this)
    }

    override fun layoutId() = R.layout.fragment_auth
}