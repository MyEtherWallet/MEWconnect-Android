package com.myetherwallet.mewconnect.core.persist.prefenreces

import android.content.Context
import android.text.TextUtils
import com.google.gson.Gson
import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.utils.crypto.KeystoreHelper
import com.myetherwallet.mewconnect.feature.main.data.WalletData

/**
 * Created by BArtWell on 04.09.2018.
 */

private const val PREFIX = "wallet_"

private const val WALLET_PRIVATE_KEY = "wallet_private_key"
private const val WALLET_ADDRESS = "wallet_address"
private const val WALLET_DATA_CACHE = "wallet_data_cache"

class WalletPreferences(context: Context, network: Network) {

    private val preferences = context.getSharedPreferences(PREFIX + network.name.toLowerCase(), Context.MODE_PRIVATE)
    private val keystoreHelper: KeystoreHelper = KeystoreHelper()

    fun getWalletPrivateKey(): String {
        return keystoreHelper.decrypt(preferences.getString(WALLET_PRIVATE_KEY, "")!!)
    }

    fun setWalletPrivateKey(privateKey: String) {
        preferences.edit().putString(WALLET_PRIVATE_KEY, keystoreHelper.encrypt(privateKey)).apply()
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

    fun isWalletExists() = !TextUtils.isEmpty(preferences.getString(WALLET_PRIVATE_KEY, ""))

    fun removeAllData() {
        preferences.edit().clear().apply()
    }
}