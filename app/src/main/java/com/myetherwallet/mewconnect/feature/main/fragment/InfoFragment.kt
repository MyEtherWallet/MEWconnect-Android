package com.myetherwallet.mewconnect.feature.main.fragment

import android.os.Bundle
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.widget.Toolbar
import com.myetherwallet.mewconnect.BuildConfig
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.core.di.ApplicationComponent
import com.myetherwallet.mewconnect.core.persist.prefenreces.KeyStore
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.ui.fragment.BaseDiFragment
import com.myetherwallet.mewconnect.core.utils.ApplicationUtils
import com.myetherwallet.mewconnect.core.utils.LaunchUtils
import com.myetherwallet.mewconnect.core.utils.MewLog
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.BiometricKeystoreHelper
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.encrypt.BaseEncryptHelper
import com.myetherwallet.mewconnect.feature.auth.callback.AuthCallback
import com.myetherwallet.mewconnect.feature.auth.fragment.AuthFragment
import com.myetherwallet.mewconnect.feature.auth.utils.BiometricUtils
import com.myetherwallet.mewconnect.feature.backup.fragment.BackUpWalletFragment
import com.myetherwallet.mewconnect.feature.main.dialog.ResetWalletDialog
import kotlinx.android.synthetic.main.fragment_info.*
import java.io.ByteArrayOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.inject.Inject


/**
 * Created by BArtWell on 15.08.2018.
 */

private const val TAG = "InfoFragment"
private const val AUTH_REQUEST_CODE = 102

class InfoFragment : BaseDiFragment(), Toolbar.OnMenuItemClickListener, AuthCallback {

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
        if (preferences.applicationPreferences.isBackedUp()) {
            info_view_recovery_phrase.setOnClickListener { addFragment(ViewRecoveryPhraseFragment.newInstance()) }
            info_view_recovery_phrase.setText(R.string.info_view_recovery_phrase)
        } else {
            info_view_recovery_phrase.setOnClickListener { addFragment(BackUpWalletFragment.newInstance()) }
            info_view_recovery_phrase.setText(R.string.info_back_up)
        }

        info_version.text = getString(R.string.info_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

        if (BiometricUtils.isAvailable(requireContext())) {
            info_view_biometric_checkbox.isChecked = preferences.applicationPreferences.isBiometricEnabled()
            info_view_biometric_container.setOnClickListener {
                if (preferences.applicationPreferences.isBiometricEnabled()) {
                    preferences.applicationPreferences.setBiometricEnabled(false)
                    info_view_biometric_checkbox.isChecked = false
                } else {
                    val authFragment = AuthFragment.newInstance()
                    authFragment.setTargetFragment(this, AUTH_REQUEST_CODE)
                    addFragment(authFragment)
                }
            }
            info_view_biometric_container.visibility = VISIBLE
        } else {
            info_view_biometric_container.visibility = GONE
        }

        info_reset_wallet.setOnClickListener {
            val dialog = ResetWalletDialog.newInstance()
            dialog.listener = {
                ApplicationUtils.removeAllData(context, preferences)
                replaceFragment(IntroFragment.newInstance())
            }
            dialog.show(childFragmentManager)
        }
    }

    override fun onAuthResult(helper: BaseEncryptHelper, keyStore: KeyStore) {
        MewLog.d(TAG, "Auth success")
        close()
        val mnemonic = helper.decryptToBytes(preferences.applicationPreferences.getWalletMnemonic(keyStore))
        val privateKey = helper.decryptToBytes(preferences.getCurrentWalletPreferences().getWalletPrivateKey(keyStore))
        if (mnemonic != null && privateKey != null) {
            val biometricKeystoreHelper = BiometricKeystoreHelper(requireContext())
            preferences.applicationPreferences.setWalletMnemonic(KeyStore.BIOMETRIC, biometricKeystoreHelper.encrypt(mnemonic))
            preferences.getCurrentWalletPreferences().setWalletPrivateKey(KeyStore.BIOMETRIC, biometricKeystoreHelper.encrypt(privateKey))
            preferences.applicationPreferences.setBiometricEnabled(true)

//        val cryptoObject = BiometricPrompt.CryptoObject(BiometricKeystoreHelper(activity).getDecryptCipher())
//            val biometricPrompt = BiometricPrompt(requireActivity(), Executors.newSingleThreadExecutor(), object : BiometricPrompt.AuthenticationCallback() {
//                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
//                    val biometricKeystoreHelper = BiometricKeystoreHelper(requireContext())
//                    val mnemonic2 = Base64.encodeToString(result.cryptoObject?.cipher!!.doFinal(mnemonic), Base64.DEFAULT)
//                    preferences.applicationPreferences.setWalletMnemonic(KeyStore.BIOMETRIC, mnemonic2/*encrypt(mnemonic, result.cryptoObject?.cipher!!)*/)
//                    val privateKey2 = Base64.encodeToString(result.cryptoObject?.cipher!!.doFinal(privateKey), Base64.DEFAULT)
//                    preferences.getCurrentWalletPreferences().setWalletPrivateKey(KeyStore.BIOMETRIC, privateKey2/*encrypt(privateKey, result.cryptoObject?.cipher!!)*/)
//                    preferences.applicationPreferences.setBiometricEnabled(true)
//                }
//            })
//            biometricPrompt.authenticate(promptInfo, cryptoObject)


        }
    }

    override fun onAuthCancel() {
        info_view_biometric_checkbox.isChecked = false
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