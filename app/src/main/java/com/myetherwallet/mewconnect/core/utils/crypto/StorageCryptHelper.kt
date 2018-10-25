package com.myetherwallet.mewconnect.core.utils.crypto

import android.util.Base64
import com.myetherwallet.mewconnect.core.utils.crypto.CryptoUtils.sha3Bytes
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Created by BArtWell on 11.07.2018.
 */

private const val KEY_ALGORITHM = "AES_256"
private const val CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding"

object StorageCryptHelper {

    fun encrypt(data: ByteArray, password: String): String {
        return Base64.encodeToString(crypt(Cipher.ENCRYPT_MODE, data, password), Base64.DEFAULT)
    }

    fun decrypt(data: String, password: String): ByteArray? {
        try {
            return crypt(Cipher.DECRYPT_MODE, Base64.decode(data, Base64.DEFAULT), password)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun crypt(mode: Int, input: ByteArray, password: String): ByteArray {
        val key = SecretKeySpec(sha3Bytes(password.toByteArray()), KEY_ALGORITHM)
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        cipher.init(mode, key)
        return cipher.doFinal(input)
    }
}
