package com.myetherwallet.mewconnect.feature.register.interactor

import android.content.Context
import com.myetherwallet.mewconnect.content.data.Network
import com.myetherwallet.mewconnect.core.extenstion.toBytes
import com.myetherwallet.mewconnect.core.persist.prefenreces.KeyStore
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.platform.BaseInteractor
import com.myetherwallet.mewconnect.core.platform.Either
import com.myetherwallet.mewconnect.core.platform.Failure
import com.myetherwallet.mewconnect.core.utils.ApplicationUtils
import com.myetherwallet.mewconnect.core.utils.CardBackgroundHelper
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.encrypt.PasswordKeystoreHelper
import com.myetherwallet.mewconnect.feature.main.utils.WalletSizingUtils
import com.myetherwallet.mewconnect.feature.register.utils.MnemonicUtils
import org.bitcoinj.crypto.MnemonicCode
import org.bitcoinj.wallet.DeterministicSeed
import org.web3j.crypto.Keys
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
        ApplicationUtils.removeAllData(context, preferences)

        val mnemonic: String
        val creationTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        mnemonic = if (params.mnemonic == null) {
            val entropy = generateNewEntropy()
            MnemonicCode.INSTANCE.toMnemonic(entropy).joinToString(" ")
        } else {
            params.mnemonic
        }
        val deterministicSeed = DeterministicSeed(mnemonic.split(" "), null, WALLET_PASSWORD, creationTime)

        val encryptedMnemonic = PasswordKeystoreHelper(params.password).encrypt(mnemonic)
        preferences.applicationPreferences.setWalletMnemonic(KeyStore.PASSWORD, encryptedMnemonic)

        for (network in Network.values()) {
            val walletPreferences = preferences.getWalletPreferences(network)
            val ecKeyPair = MnemonicUtils.getEcKeyPair(deterministicSeed, network)
            val address = Keys.getAddress(ecKeyPair)
            walletPreferences.setWalletPrivateKey(KeyStore.PASSWORD, PasswordKeystoreHelper(params.password).encrypt(ecKeyPair.privateKey.toBytes()))
            walletPreferences.setWalletAddress(address)
            CardBackgroundHelper(context).draw(address, network, params.displayWidth, WalletSizingUtils.calculateCardHeight())
        }

        if (params.mnemonic != null) {
            preferences.applicationPreferences.setBackedUp(true)
        }

        return Either.Right(Any())
    }

    private fun generateNewEntropy(): ByteArray {
        val initialEntropy = ByteArray(32)
        secureRandom.nextBytes(initialEntropy)
        return initialEntropy
    }

    data class Params(val password: String, val mnemonic: String?, val displayWidth: Int)
}
