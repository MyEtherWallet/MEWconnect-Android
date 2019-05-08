package com.myetherwallet.mewconnect.core.utils.crypto.keystore.encrypt

import javax.crypto.Cipher

/**
 * Created by BArtWell on 30.04.2019.
 */
abstract class BaseEncryptHelper {

    abstract fun getDecryptCipher(): Cipher

    abstract fun decryptToBytes(text: String): ByteArray?

    open fun decrypt(text: String) = decryptToBytes(text)?.let { String(it) } ?: ""

    abstract fun getEncryptCipher(): Cipher

    fun encrypt(text: String) = encrypt(text.toByteArray(charset("UTF-8")))

    abstract fun encrypt(data: ByteArray): String

    abstract fun getTransformation(): String
}