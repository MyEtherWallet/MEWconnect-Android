package com.myetherwallet.mewconnect.content.data

import android.content.Context
import com.myetherwallet.mewconnect.R

/**
 * Created by BArtWell on 04.09.2018.
 */
enum class Network(val fullName: Int, val shortName: Int, val currency: Int, val apiMethod: String, val path: String, val chainId: Byte) {

    MAIN(R.string.wallet_network_main_full, R.string.wallet_network_main_short, R.string.wallet_network_main_currency, "eth", "m/44'/60'/0'/0", 1),
    ROPSTEN(R.string.wallet_network_ropsten_full, R.string.wallet_network_ropsten_short, R.string.wallet_network_ropsten_currency, "rop", "m/44'/1'/0'/0", 3),

    XSM(R.string.wallet_network_smilo_full, R.string.wallet_network_smilo_short, R.string.wallet_network_smilo_currency, "xsm", "m/44'/20080914'/0'/0", 20080914.toByte()),
    TESTNET(R.string.wallet_network_smilotestnet_full, R.string.wallet_network_smilotestnet_short, R.string.wallet_network_smilotestnet_currency, "rop", "m/44'/10'/0'/0", 10);

    companion object {

        fun getTitles(context: Context): Array<String> {
            val titles = mutableListOf<String>()
            for (value in values()) {
                titles.add(context.getString(value.fullName))
            }
            return titles.toTypedArray()
        }
    }

    fun getCurrency(context: Context): String = context.getString(currency)
}
