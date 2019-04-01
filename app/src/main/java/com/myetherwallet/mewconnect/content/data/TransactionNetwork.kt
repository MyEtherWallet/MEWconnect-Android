package com.myetherwallet.mewconnect.content.data

import com.myetherwallet.mewconnect.R

/**
 * Created by BArtWell on 22.03.2019.
 */

enum class TransactionNetwork(val title: Int, val chainId: Long) {

    ETH(R.string.transaction_network_eth, 1),
    EXP(R.string.transaction_network_exp, 2),
    ROPSTEN_ETH(R.string.transaction_network_ropsten_eth, 3),
    RIN(R.string.transaction_network_rin, 4),
    UBQ(R.string.transaction_network_ubq, 8),
    EOSC(R.string.transaction_network_eosc, 20),
    ETSC(R.string.transaction_network_etsc, 28),
    KOV(R.string.transaction_network_kov, 42),
    GO(R.string.transaction_network_go, 60),
    ETC(R.string.transaction_network_etc, 61),
    ELLA(R.string.transaction_network_ella, 64),
    POA(R.string.transaction_network_poa, 99),
    CLO(R.string.transaction_network_clo, 820),
    EGEM(R.string.transaction_network_egem, 1987),
    ESN(R.string.transaction_network_esn, 31102),
    TOMO(R.string.transaction_network_tomo, 40686),
    AKA(R.string.transaction_network_aka, 200625),
    ETHO(R.string.transaction_network_etho, 1313114),
    MUSIC(R.string.transaction_network_music, 7762959),
    PIRL(R.string.transaction_network_pirl, 3125659152);

    companion object {

        fun findByChaidId(chainId: Long): TransactionNetwork? {
            for (value in values()) {
                if (value.chainId == chainId) {
                    return value
                }
            }
            return null
        }
    }

    val symbol: String
        get() = name.replace('_', ' ')
}