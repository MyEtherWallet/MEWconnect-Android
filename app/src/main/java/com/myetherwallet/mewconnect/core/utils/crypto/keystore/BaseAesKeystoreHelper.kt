package com.myetherwallet.mewconnect.core.utils.crypto.keystore

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.myetherwallet.mewconnect.core.persist.prefenreces.IvPreferences
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec

/**
 * Created by BArtWell on 07.05.2019.
 */
abstract class BaseAesKeystoreHelper(context: Context, private val ivPreferences: IvPreferences) : BaseKeystoreHelper(context) {

    private lateinit var ivKey: String

    override fun createKeys() {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, getProvider())
        val builder = KeyGenParameterSpec.Builder(getAlias(), KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        keyGenerator.init(builder.build())
        keyGenerator.generateKey()
    }

    fun encrypt(data: String, ivKey: String): String {
        this.ivKey = ivKey
        return super.encrypt(data)
    }

    fun decrypt(text: String, ivKey: String): String {
        this.ivKey = ivKey
        return super.decrypt(text)
    }

    fun decryptToBytes(text: String, ivKey: String): ByteArray? {
        this.ivKey = ivKey
        return super.decryptToBytes(text)
    }

    override fun getEncryptCipher(): Cipher {
        val key = keyStore.getKey(getAlias(), null)
        val cipher = Cipher.getInstance(getTransformation())
        cipher.init(Cipher.ENCRYPT_MODE, key)
        ivPreferences.saveIv(ivKey, cipher.iv)
        return cipher
    }

    override fun getDecryptCipher(): Cipher {
        val key = keyStore.getKey(getAlias(), null)
        val cipher = Cipher.getInstance(getTransformation())
        val ivParameterSpec = GCMParameterSpec(128, ivPreferences.getIv(ivKey))
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec)
        return cipher
    }
}