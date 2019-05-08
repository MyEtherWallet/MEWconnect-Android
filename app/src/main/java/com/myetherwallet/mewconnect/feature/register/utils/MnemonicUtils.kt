package com.myetherwallet.mewconnect.feature.register.utils

import com.myetherwallet.mewconnect.content.data.Network
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.wallet.DeterministicSeed
import org.web3j.crypto.ECKeyPair
import java.util.concurrent.TimeUnit

/**
 * Created by BArtWell on 08.05.2019.
 */
object MnemonicUtils {

    private const val WALLET_PASSWORD = ""

    fun getEcKeyPair(mnemonic: String, network: Network) = getEcKeyPair(mnemonic.split(" "), network)

    fun getEcKeyPair(mnemonic: List<String>, network: Network) =
            getEcKeyPair(DeterministicSeed(mnemonic,
                    null,
                    WALLET_PASSWORD,
                    TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())), network)

    fun getEcKeyPair(deterministicSeed: DeterministicSeed, network: Network): ECKeyPair {
        //TODO: Double-check derivation path logic
        val path = network.path + "/0"
        var deterministicKey = HDKeyDerivation.createMasterPrivateKey(deterministicSeed.seedBytes)
        val pathParts = path.split("/")
        for (i in 1 until pathParts.size) {
            val childNumber: ChildNumber
            if (pathParts[i].endsWith("'")) {
                val number = Integer.parseInt(pathParts[i].substring(0, pathParts[i].length - 1))
                childNumber = ChildNumber(number, true)
            } else {
                val number = Integer.parseInt(pathParts[i])
                childNumber = ChildNumber(number, false)
            }
            deterministicKey = HDKeyDerivation.deriveChildKey(deterministicKey, childNumber)
        }
        return ECKeyPair.create(deterministicKey.privKeyBytes)
    }
}