package com.myetherwallet.mewconnect.feature.auth.utils

import android.content.Context
import androidx.biometric.BiometricPrompt
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.fragment.app.FragmentActivity
import com.myetherwallet.mewconnect.core.utils.MewLog
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.BiometricKeystoreHelper
import java.util.concurrent.Executors
import javax.crypto.Cipher

/**
 * Created by BArtWell on 15.04.2019.
 */

private const val TAG = "BiometricUtils"

object BiometricUtils {

    // TODO: migrate to biometric hardware checkout
    fun isAvailable(context: Context) = FingerprintManagerCompat.from(context).isHardwareDetected

    fun authenticate(activity: FragmentActivity, successCallback: (cipher: Cipher?) -> Unit) {
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

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Set the title to display.")
                .setSubtitle("Set the subtitle to display.")
                .setDescription("Set the description to display")
                .setNegativeButtonText("Negative Button")
                .build()
        val cryptoObject = BiometricPrompt.CryptoObject(BiometricKeystoreHelper(activity).getDecryptCipher())
        val biometricPrompt = BiometricPrompt(activity, Executors.newSingleThreadExecutor(), callback)
        biometricPrompt.authenticate(promptInfo, cryptoObject)
    }
}