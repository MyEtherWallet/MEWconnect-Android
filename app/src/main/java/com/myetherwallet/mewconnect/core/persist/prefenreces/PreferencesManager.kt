package com.myetherwallet.mewconnect.core.persist.prefenreces

import android.content.Context
import android.preference.PreferenceManager
import com.myetherwallet.mewconnect.content.data.Network

/**
 * Created by BArtWell on 04.09.2018.
 */

class PreferencesManager(context: Context) {

    val applicationPreferences: ApplicationPreferences
    private val walletPreferences: MutableMap<String, WalletPreferences> = mutableMapOf()

    init {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        applicationPreferences = ApplicationPreferences(preferences)

        for (network in Network.values()) {
            walletPreferences[network.name] = WalletPreferences(context, network)
        }
    }

    fun getCurrentWalletPreferences() = getWalletPreferences(applicationPreferences.getCurrentNetwork())

    fun getWalletPreferences(network: Network) = walletPreferences[network.name]!!
}