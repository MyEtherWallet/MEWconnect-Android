package com.myetherwallet.mewconnect.core.persist.prefenreces

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.google.gson.Gson
import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.DeprecatedStorageKeystoreHelper
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.StorageKeystoreHelper
import com.myetherwallet.mewconnect.feature.main.data.WalletData

/**
 * Created by BArtWell on 04.09.2018.
 */

private const val PREFIX = "wallet_"

private const val WALLET_ADDRESS = "wallet_address"
private const val WALLET_DATA_CACHE = "wallet_data_cache"

private const val DEPRECATED_WALLET_PRIVATE_KEY = "wallet_private_key"

class WalletPreferences(context: Context, network: Network) : IvPreferences {

    private val preferences = context.getSharedPreferences(PREFIX + network.name.toLowerCase(), Context.MODE_PRIVATE)
    private val keystoreHelper = StorageKeystoreHelper(context, this)

    init {
        // With biometric authentication introduction, key and mnemonic storage was refactored
        // If old key and mnemonic storage is found on device
        // we should copy data to new storage and remove old storage
        val old = getDeprecatedWalletPrivateKey(context)
        if (!TextUtils.isEmpty(old)) {
            setWalletPrivateKey(KeyStore.PASSWORD, old)
            removeDeprecatedWalletPrivateKey()
        }
    }

    fun getWalletPrivateKey(keyStore: KeyStore): String {
        return keystoreHelper.decrypt(preferences.getString(keyStore.privateKey, "")!!, keyStore.privateKey)
    }

    fun setWalletPrivateKey(keyStore: KeyStore, privateKey: String) {
        preferences.edit().putString(keyStore.privateKey, keystoreHelper.encrypt(privateKey, keyStore.privateKey)).apply()
    }

    fun removeWalletPrivateKey(keyStore: KeyStore) {
        preferences.edit().remove(keyStore.privateKey).apply()
    }

    private fun getDeprecatedWalletPrivateKey(context: Context): String {
        return DeprecatedStorageKeystoreHelper(context).decrypt(preferences.getString(DEPRECATED_WALLET_PRIVATE_KEY, "")!!)
    }

    private fun removeDeprecatedWalletPrivateKey() {
        preferences.edit().remove(DEPRECATED_WALLET_PRIVATE_KEY).apply()
    }

    fun getWalletAddress(): String = preferences.getString(WALLET_ADDRESS, "")!!

    fun setWalletAddress(address: String) {
        preferences.edit().putString(WALLET_ADDRESS, address).apply()
    }

    fun getWalletDataCache(): WalletData? {
        val string = preferences.getString(WALLET_DATA_CACHE, null)
        return Gson().fromJson(string, WalletData::class.java)
    }

    fun setWalletDataCache(walletData: WalletData) {
        val string = Gson().toJson(walletData)
        preferences.edit().putString(WALLET_DATA_CACHE, string).apply()
    }

    fun isWalletExists() = !TextUtils.isEmpty(preferences.getString(KeyStore.PASSWORD.privateKey, ""))

    fun removeAllData() {
        preferences.edit().clear().apply()
    }

    override fun getSharedPreferences(): SharedPreferences = preferences
}