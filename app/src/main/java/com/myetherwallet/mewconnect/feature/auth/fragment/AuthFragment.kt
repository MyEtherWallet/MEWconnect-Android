package com.myetherwallet.mewconnect.feature.auth.fragment

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.persist.prefenreces.KeyStore
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.ui.callback.EmptyTextWatcher
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import com.myetherwallet.mewconnect.core.utils.KeyboardUtils
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.BiometricKeystoreHelper
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.encrypt.BaseEncryptHelper
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.encrypt.PasswordKeystoreHelper
import com.myetherwallet.mewconnect.feature.auth.callback.AuthCallback
import com.myetherwallet.mewconnect.feature.auth.utils.AuthAttemptsHelper
import com.myetherwallet.mewconnect.feature.auth.utils.BiometricUtils
import com.myetherwallet.mewconnect.feature.main.fragment.WalletFragment
import kotlinx.android.synthetic.main.fragment_auth.*
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import javax.inject.Inject

/**
 * Created by BArtWell on 13.08.2018.
 */

private const val EXTRA_ALLOW_BIOMETRIC = "allow_biometric"

class AuthFragment : BaseDiFragment() {

    companion object {
        fun newInstance(allowBiometric: Boolean = true): AuthFragment {
            val fragment = AuthFragment()
            val arguments = Bundle()
            arguments.putBoolean(EXTRA_ALLOW_BIOMETRIC, allowBiometric)
            fragment.arguments = arguments
            return fragment
        }
    }

    @Inject
    lateinit var preferences: PreferencesManager
    private lateinit var attemptsHelper: AuthAttemptsHelper
    private val handler = Handler()
    private var isBiometricAllowed = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isBiometricAllowed = arguments?.getBoolean(EXTRA_ALLOW_BIOMETRIC) ?: true

        setEnterButtonEnabled(false)

        attemptsHelper = AuthAttemptsHelper(handler, preferences.applicationPreferences) { minute: Int, second: Int ->
            if (minute == 0 && second == 0) {
                auth_password_input_layout.isErrorEnabled = false
                auth_password_input_layout.isEnabled = true
                setEnterButtonEnabled(auth_password_text.length() > 0)
            } else {
                auth_password_input_layout.isErrorEnabled = false
                auth_password_input_layout.isEnabled = false
                setEnterButtonEnabled(false)
                auth_password_input_layout.error = getString(R.string.auth_incorrect_attempts, minute, second)
            }
        }

        auth_password_text.addTextChangedListener(object : EmptyTextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                auth_password_input_layout.isErrorEnabled = false
                setEnterButtonEnabled(auth_password_text.length() > 0)
            }
        })

        auth_forgot_password.setOnClickListener { addFragment(ForgotPasswordFragment.newInstance()) }
        auth_enter_container.setOnClickListener { doAuth() }

        auth_password_text.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                doAuth()
                return@setOnEditorActionListener true
            }
            false
        }

        auth_fingerprint.setOnClickListener {
            showBiometricPrompt()
        }

        if (isBiometricAllowed && BiometricUtils.isAvailable(requireContext()) && BiometricUtils.isEnabled(requireContext(), preferences)) {
            auth_fingerprint.visibility = VISIBLE
            handler.postDelayed({
                try {
                    showBiometricPrompt()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 300L)
        } else {
            auth_fingerprint.visibility = GONE
        }
        KeyboardUtils.showKeyboard(auth_password_text)
    }

    private fun showBiometricPrompt() {
        BiometricUtils.authenticate(requireActivity(), preferences) { cipher ->
            cipher?.let {
                requireActivity().runOnUiThread {
                    handleResult(BiometricKeystoreHelper(requireContext(), cipher), KeyStore.BIOMETRIC)
                }
            }
        }
    }

    private fun setEnterButtonEnabled(enabled: Boolean) {
        auth_enter_container.isEnabled = enabled
        val color = if (enabled) R.color.blue else R.color.auth_enter_disabled
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.auth_enter)
        val drawableWrap = DrawableCompat.wrap(drawable!!).mutate()
        DrawableCompat.setTint(drawableWrap, ContextCompat.getColor(requireContext(), color))
        auth_enter.setImageDrawable(drawable)
    }

    private fun doAuth() {
        if (auth_password_input_layout.isEnabled) {
            val password = auth_password_text.text.toString()
            handleResult(PasswordKeystoreHelper(password), KeyStore.PASSWORD)
        }
    }

    private fun handleResult(keystoreHelper: BaseEncryptHelper, keyStore: KeyStore) {
        if (keyStore == KeyStore.BIOMETRIC || checkPrivateKey(keystoreHelper, keyStore)) {
            attemptsHelper.reset()
            if (targetFragment == null) {
                replaceFragment(WalletFragment.newInstance())
            } else {
                val target = targetFragment
                if (target is AuthCallback) {
                    target.onAuthResult(keystoreHelper, keyStore)
                }
            }
        } else {
            if (!attemptsHelper.check()) {
                auth_password_input_layout.error = getString(R.string.auth_wrong_password_error)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        attemptsHelper.resume()
    }

    override fun onStop() {
        attemptsHelper.pause()
        super.onStop()
    }

    private fun checkPrivateKey(keystoreHelper: BaseEncryptHelper, keyStore: KeyStore): Boolean {
        keystoreHelper.decryptToBytes(preferences.getWalletPreferences(Network.MAIN).getWalletPrivateKey(keyStore))?.let {
            val ecKeyPair = ECKeyPair.create(it)
            if (ecKeyPair != null) {
                val address = Keys.getAddress(ecKeyPair)
                if (address == preferences.getWalletPreferences(Network.MAIN).getWalletAddress()) {
                    return true
                }
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