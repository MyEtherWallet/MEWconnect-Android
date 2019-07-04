package com.myetherwallet.mewconnect.feature.auth.utils

import android.app.KeyguardManager
import android.content.Context
import android.content.Context.KEYGUARD_SERVICE
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.text.TextUtils
import androidx.biometric.BiometricPrompt
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.fragment.app.FragmentActivity
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.persist.prefenreces.KeyStore
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.utils.MewLog
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.BiometricKeystoreHelper
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.encrypt.BaseEncryptHelper
import java.util.concurrent.Executors
import javax.crypto.Cipher


/**
 * Created by BArtWell on 15.04.2019.
 */

private const val TAG = "BiometricUtils"

object BiometricUtils {

    // TODO: migrate to biometric hardware checkout
    fun isAvailable(context: Context): Boolean {
        val fingerprintManager = FingerprintManagerCompat.from(context)
        if (fingerprintManager.isHardwareDetected) {
            val keyguardManager = context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            if (keyguardManager.isKeyguardSecure) {
                if (fingerprintManager.hasEnrolledFingerprints()) {
                    return true
                }
            }
        }
        return false
    }

    fun authenticate(activity: FragmentActivity, preferences: PreferencesManager, successCallback: (cipher: Cipher?) -> Unit) {
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                MewLog.d(TAG, "onAuthenticationSucceeded")
                successCallback(result.cryptoObject?.cipher)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                MewLog.d(TAG, "onAuthenticationFailed")
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                MewLog.d(TAG, "onAuthenticationError: errorCode=$errorCode; errString=$errString")
            }
        }

        getDecryptCipherSafely(activity, preferences)?.let { cipher ->
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle(activity.getString(R.string.fingerprint_prompt_title))
                    .setDescription(activity.getString(R.string.fingerprint_prompt_description))
                    .setNegativeButtonText(activity.getString(R.string.cancel))
                    .build()
            val cryptoObject = BiometricPrompt.CryptoObject(cipher)
            val biometricPrompt = BiometricPrompt(activity, Executors.newSingleThreadExecutor(), callback)
            biometricPrompt.authenticate(promptInfo, cryptoObject)
        }
    }

    fun isEnabled(context: Context, preferences: PreferencesManager): Boolean {
        val isEnabled = !TextUtils.isEmpty(preferences.applicationPreferences.getWalletMnemonic(KeyStore.BIOMETRIC))
        if (isEnabled) {
            return getDecryptCipherSafely(context, preferences) != null
        }
        return false
    }

    fun getDecryptCipherSafely(context: Context, preferences: PreferencesManager): Cipher? {
        val biometricKeystoreHelper = BiometricKeystoreHelper(context)
        try {
            return biometricKeystoreHelper.getDecryptCipher()
        } catch (e: KeyPermanentlyInvalidatedException) {
            MewLog.w(TAG, "Fingerprints was changed", e)
            biometricKeystoreHelper.removeKey()
            disable(preferences)
        }
        return null
    }

    fun disable(preferences: PreferencesManager) {
        preferences.applicationPreferences.removeWalletMnemonic(KeyStore.BIOMETRIC)
        for (network in Network.values()) {
            preferences.getWalletPreferences(network).removeWalletPrivateKey(KeyStore.BIOMETRIC)
        }
    }

    fun encryptData(context: Context, helper: BaseEncryptHelper, keyStore: KeyStore, preferences: PreferencesManager): Boolean {
        helper.decryptToBytes(preferences.applicationPreferences.getWalletMnemonic(keyStore))?.let { mnemonic ->
            val biometricKeystoreHelper = BiometricKeystoreHelper(context)
            preferences.applicationPreferences.setWalletMnemonic(KeyStore.BIOMETRIC, biometricKeystoreHelper.encrypt(mnemonic))
            for (network in Network.values()) {
                val walletPreferences = preferences.getWalletPreferences(network)
                helper.decryptToBytes(walletPreferences.getWalletPrivateKey(keyStore))?.let { privateKey ->
                    walletPreferences.setWalletPrivateKey(KeyStore.BIOMETRIC, biometricKeystoreHelper.encrypt(privateKey))
                }
            }
            return true
        }
        return false
    }
}