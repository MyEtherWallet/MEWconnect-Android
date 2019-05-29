package com.myetherwallet.mewconnect.core.utils.crypto.keystore

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyPairGenerator
import javax.crypto.Cipher

/**
 * Created by BArtWell on 07.05.2019.
 */

abstract class BaseRsaKeystoreHelper(context: Context) : BaseKeystoreHelper(context) {

    override fun createKeys() {
        val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, getProvider())
        val spec = getKeyGenParameterSpec(KeyGenParameterSpec.Builder(getAlias(), KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT))
        keyPairGenerator.initialize(spec)
        keyPairGenerator.generateKeyPair()
    }

    override fun getDecryptCipher(): Cipher {
        val key = keyStore.getKey(getAlias(), null)
        val cipher = Cipher.getInstance(getTransformation())
        cipher.init(Cipher.DECRYPT_MODE, key)
        return cipher
    }

    abstract fun getKeyGenParameterSpec(builder: KeyGenParameterSpec.Builder): KeyGenParameterSpec
}