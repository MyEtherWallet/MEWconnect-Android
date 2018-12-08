package com.myetherwallet.mewconnect.feature.register.interactor

import android.content.Context
import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.extenstion.toBytes
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.platform.BaseInteractor
import com.myetherwallet.mewconnect.core.platform.Either
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.core.utils.CardBackgroundHelper
import com.myetherwallet.mewconnect.core.utils.crypto.StorageCryptHelper
import com.myetherwallet.mewconnect.feature.main.utils.WalletSizingUtils
import com.myetherwallet.mewconnect.feature.main.view.WalletCardView
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.crypto.HDKeyDerivation
import org.bitcoinj.wallet.DeterministicSeed
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import org.web3j.crypto.MnemonicUtils
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by BArtWell on 12.10.2018.
 */

private const val WALLET_PASSWORD = ""

class CreateWallets
@Inject constructor(private val context: Context, private val preferences: PreferencesManager) : BaseInteractor<Any, CreateWallets.Params>() {

    private val secureRandom = SecureRandom()

    override suspend fun run(params: Params): Either<Failure, Any> {

        val mnemonic: String
        val creationTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        val deterministicSeed: DeterministicSeed
        if (params.mnemonic == null) {
            val entropy = generateNewEntropy()
            mnemonic = MnemonicUtils.generateMnemonic(entropy)
            deterministicSeed = DeterministicSeed(emptyList(), entropy, WALLET_PASSWORD, creationTime)
        } else {
            mnemonic = params.mnemonic
            val mnemonicList = mnemonic.split(" ")
            deterministicSeed = DeterministicSeed(mnemonicList, null, WALLET_PASSWORD, creationTime)
        }

        val encryptedMnemonic = StorageCryptHelper.encrypt(mnemonic.toByteArray(), params.password)
        preferences.applicationPreferences.setWalletMnemonic(encryptedMnemonic)

        for (network in Network.values()) {
            val walletPreferences = preferences.getWalletPreferences(network)
            //TODO: Double-check derivation path logic
            val path = network.path + "/0"
            val ecKeyPair = createEthWallet(deterministicSeed, path)

            val address = Keys.getAddress(ecKeyPair)
            walletPreferences.setWalletPrivateKey(StorageCryptHelper.encrypt(ecKeyPair.privateKey.toBytes(), params.password))
            walletPreferences.setWalletAddress(address)
            CardBackgroundHelper(context).draw(address, network, params.displayWidth, WalletSizingUtils.calculateCardHeight(context))
        }

        if (params.mnemonic != null) {
            preferences.applicationPreferences.setBackedUp(true)
        }

        return Either.Right(Any())
    }

    private fun createEthWallet(deterministicSeed: DeterministicSeed, path: String): ECKeyPair {
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

    private fun generateNewEntropy(): ByteArray {
        val initialEntropy = ByteArray(32)
        secureRandom.nextBytes(initialEntropy)
        return initialEntropy
    }

    data class Params(val password: String, val mnemonic: String?, val displayWidth: Int)
}
