package com.myetherwallet.mewconnect.core.persist.prefenreces

import android.content.SharedPreferences
import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.utils.crypto.KeystoreHelper
import java.util.*

/**
 * Created by BArtWell on 10.07.2018.
 */

private const val WALLET_MNEMONIC = "wallet_mnemonic"
private const val WALLET_IS_BACKED_UP = "wallet_is_backed_up"
private const val CURRENT_NETWORK = "current_network"
private const val BACKUP_WARNING_TIME = "backup_warning_time"
private const val INSTALL_TIME = "install_time"

class ApplicationPreferences(private val preferences: SharedPreferences) {

    private val keystoreHelper: KeystoreHelper = KeystoreHelper()

    fun getWalletMnemonic(): String {
        return keystoreHelper.decrypt(preferences.getString(WALLET_MNEMONIC, "")!!)
    }

    fun setWalletMnemonic(mnemonic: String) {
        preferences.edit().putString(WALLET_MNEMONIC, keystoreHelper.encrypt(mnemonic)).apply()
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
        preferences.edit()
                .remove(WALLET_MNEMONIC)
                .remove(WALLET_IS_BACKED_UP)
                .remove(BACKUP_WARNING_TIME)
                .remove(CURRENT_NETWORK)
                .apply()
    }
}