package com.myetherwallet.mewconnect.core.utils.crypto.keystore.encrypt

import android.util.Base64
import com.myetherwallet.mewconnect.core.utils.crypto.CryptoUtils
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Created by BArtWell on 30.04.2019.
 */

private const val ALGORITHM = "AES_256"
private const val TRANSFORMATION = "AES/ECB/PKCS5Padding"

class PasswordKeystoreHelper(private val password: String) : BaseEncryptHelper() {

    private fun getSpec() = SecretKeySpec(CryptoUtils.sha3Bytes(password.toByteArray()), ALGORITHM)

    override fun getDecryptCipher(): Cipher {
        val cipher = Cipher.getInstance(getTransformation())
        cipher.init(Cipher.DECRYPT_MODE, getSpec())
        return cipher
    }

    override fun decryptToBytes(text: String): ByteArray? {
        try {
            return crypt(Cipher.DECRYPT_MODE, Base64.decode(text, Base64.DEFAULT), password)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun getEncryptCipher(): Cipher {
        val cipher = Cipher.getInstance(getTransformation())
        cipher.init(Cipher.ENCRYPT_MODE, getSpec())
        return cipher
    }

    override fun encrypt(data: ByteArray): String {
        return Base64.encodeToString(crypt(Cipher.ENCRYPT_MODE, data, password), Base64.DEFAULT)
    }

    private fun crypt(mode: Int, input: ByteArray, password: String): ByteArray {
        val key = SecretKeySpec(CryptoUtils.sha3Bytes(password.toByteArray()), ALGORITHM)
        val cipher = Cipher.getInstance(getTransformation())
        cipher.init(mode, key)
        return cipher.doFinal(input)
    }

    override fun getTransformation() = TRANSFORMATION
}