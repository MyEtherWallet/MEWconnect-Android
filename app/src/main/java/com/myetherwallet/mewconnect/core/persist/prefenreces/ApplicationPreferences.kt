package com.myetherwallet.mewconnect.core.persist.prefenreces

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.myetherwallet.mewconnect.BuildConfig
import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.DeprecatedStorageKeystoreHelper
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.StorageKeystoreHelper
import java.util.*

/**
 * Created by BArtWell on 10.07.2018.
 */

private const val WALLET_IS_BACKED_UP = "wallet_is_backed_up"
private const val CURRENT_NETWORK = "current_network"
private const val BACKUP_WARNING_TIME = "backup_warning_time"
private const val INSTALL_TIME = "install_time"
private const val RATE_STARTS_COUNT = "rate_starts_count"
private const val RATE_VERSION = "rate_version"
private const val RATE_VERSION_VALUE = 1
private const val AUTH_FIRST_ATTEMPT_TIME = "auth_first_attempt_time"
private const val AUTH_ATTEMPTS_COUNT = "auth_attempts_count"
private const val AUTH_TIMER_TIME = "auth_timer_time"
private const val WHATS_NEW_DIALOG_VERSION = "whats_new_dialog_version"
private const val IS_BIOMETRIC_PROMO_SHOWN = "is_biometric_promo_shown"

private const val DEPRECATED_WALLET_MNEMONIC = "wallet_mnemonic"

class ApplicationPreferences(context: Context, private val preferences: SharedPreferences) : IvPreferences {

    private val keystoreHelper = StorageKeystoreHelper(context, this)

    init {
        // With biometric authentication introduction, key and mnemonic storage was refactored
        // If old key and mnemonic storage is found on device
        // we should copy data to new storage and remove old storage
        val old = getDeprecatedWalletMnemonic(context)
        if (!TextUtils.isEmpty(old)) {
            setWalletMnemonic(KeyStore.PASSWORD, old)
            removeDeprecatedWalletMnemonic()
        }
    }

    fun getWalletMnemonic(keyStore: KeyStore): String {
        return keystoreHelper.decrypt(preferences.getString(keyStore.mnemonic, "")!!, keyStore.mnemonic)
    }

    fun setWalletMnemonic(keyStore: KeyStore, mnemonic: String) {
        preferences.edit().putString(keyStore.mnemonic, keystoreHelper.encrypt(mnemonic, keyStore.mnemonic)).apply()
    }

    fun removeWalletMnemonic(keyStore: KeyStore) {
        preferences.edit().remove(keyStore.mnemonic).apply()
    }

    private fun getDeprecatedWalletMnemonic(context: Context): String {
        return DeprecatedStorageKeystoreHelper(context).decrypt(preferences.getString(DEPRECATED_WALLET_MNEMONIC, "")!!)
    }

    private fun removeDeprecatedWalletMnemonic() {
        preferences.edit().remove(DEPRECATED_WALLET_MNEMONIC).apply()
    }

    fun isBackedUp() = preferences.getBoolean(WALLET_IS_BACKED_UP, false)

    fun setBackedUp(isBackedUp: Boolean) {
        preferences.edit().putBoolean(WALLET_IS_BACKED_UP, isBackedUp).apply()
    }

    fun getCurrentNetwork(): Network {
        return Network.valueOf(preferences.getString(CURRENT_NETWORK, Network.MAIN.name)!!)
    }

    fun setCurrentNetwork(network: Network) {
        preferences.edit().putString(CURRENT_NETWORK, network.name).apply()
    }

    fun getBackupWarningTime(): Long = preferences.getLong(BACKUP_WARNING_TIME, 0L)

    fun setBackupWarningTime() {
        preferences.edit().putLong(BACKUP_WARNING_TIME, System.currentTimeMillis()).apply()
    }

    fun getInstallTime(): Date {
        var timestamp = preferences.getLong(INSTALL_TIME, 0L)
        if (timestamp == 0L) {
            timestamp = System.currentTimeMillis()
            setInstallTime(timestamp)
        }
        return Date(timestamp)
    }

    fun setInstallTime(timestamp: Long = System.currentTimeMillis()) {
        if (!preferences.contains(INSTALL_TIME)) {
            preferences.edit().putLong(INSTALL_TIME, timestamp).apply()
        }
    }

    fun removeWalletData() {
        preferences.edit().apply {
            for (value in KeyStore.values()) {
                remove(value.mnemonic)
            }
            remove(WALLET_IS_BACKED_UP)
            remove(BACKUP_WARNING_TIME)
            remove(CURRENT_NETWORK)
            remove(AUTH_FIRST_ATTEMPT_TIME)
            remove(AUTH_ATTEMPTS_COUNT)
            remove(AUTH_TIMER_TIME)
            remove(IS_BIOMETRIC_PROMO_SHOWN)
            apply()
        }
    }

    fun getRateStartsCount() = preferences.getInt(RATE_STARTS_COUNT, 0)

    fun setRateStartsCount(count: Int) {
        preferences.edit().putInt(RATE_STARTS_COUNT, count).apply()
    }

    fun disableRateDialog() = preferences.edit().putInt(RATE_VERSION, RATE_VERSION_VALUE).apply()

    fun isRateDialogEnabled() = preferences.getInt(RATE_VERSION, 0) != RATE_VERSION_VALUE

    fun getAuthFirstAttemptTime() = preferences.getLong(AUTH_FIRST_ATTEMPT_TIME, 0L)

    fun setAuthFirstAttemptTime(uptime: Long) = preferences.edit().putLong(AUTH_FIRST_ATTEMPT_TIME, uptime).apply()

    fun getAuthAttemptsCount() = preferences.getInt(AUTH_ATTEMPTS_COUNT, 0)

    fun incrementAuthAttemptsCount() = preferences.edit().putInt(AUTH_ATTEMPTS_COUNT, getAuthAttemptsCount() + 1).apply()

    fun resetAuthAttemptsCount() = preferences.edit().remove(AUTH_ATTEMPTS_COUNT).apply()

    fun getAuthTimerTime() = preferences.getLong(AUTH_TIMER_TIME, 0)

    fun setAuthTimerTime(uptime: Long) = preferences.edit().putLong(AUTH_TIMER_TIME, uptime).apply()

    fun resetAuthTimerTime() = preferences.edit().remove(AUTH_TIMER_TIME).apply()

    fun shouldShowWhatsNewDialog(): Boolean {
        val current = preferences.getInt(WHATS_NEW_DIALOG_VERSION, 0)
        preferences.edit().putInt(WHATS_NEW_DIALOG_VERSION, BuildConfig.VERSION_CODE).apply()
        return current != BuildConfig.VERSION_CODE && current > 0
    }

    fun isBiometricPromoShown() = preferences.getBoolean(IS_BIOMETRIC_PROMO_SHOWN, false)

    fun setBiometricPromoShown(shown: Boolean) = preferences.edit().putBoolean(IS_BIOMETRIC_PROMO_SHOWN, shown).apply()

    override fun getSharedPreferences() = preferences
}