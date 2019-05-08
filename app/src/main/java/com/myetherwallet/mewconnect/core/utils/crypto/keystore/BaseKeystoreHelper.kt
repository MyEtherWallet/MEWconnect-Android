package com.myetherwallet.mewconnect.core.utils.crypto.keystore

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.Base64
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.encrypt.BaseEncryptHelper
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream

/**
 * Created by BArtWell on 26.04.2019.
 */
abstract class BaseKeystoreHelper(private val context: Context) : BaseEncryptHelper() {

    internal var keyStore: KeyStore = KeyStore.getInstance(getProvider())

    init {
        keyStore.load(null)
        if (!isAliasExists()) {
            internalCreateKeys()
        }
    }

    private fun isAliasExists() = Collections.list(keyStore.aliases()).contains(getAlias())

    private fun internalCreateKeys() {
        var initialLocale: Locale? = null
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            initialLocale = Locale.getDefault()
            setLocale(Locale.ENGLISH)
        }

        createKeys()

        if (initialLocale != null) {
            setLocale(initialLocale)
        }
    }

    abstract fun createKeys()

    override fun getEncryptCipher(): Cipher {
        val privateKeyEntry = keyStore.getEntry(getAlias(), null) as KeyStore.PrivateKeyEntry
        val publicKey = privateKeyEntry.certificate.publicKey
        val cipher = Cipher.getInstance(getTransformation())
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher
    }

    override fun encrypt(data: ByteArray): String {
        try {
            val outputStream = ByteArrayOutputStream()
            val cipherOutputStream = CipherOutputStream(outputStream, getEncryptCipher())
            cipherOutputStream.write(data)
            cipherOutputStream.close()
            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    override fun getDecryptCipher(): Cipher {
        val privateKeyEntry = keyStore.getEntry(getAlias(), null) as KeyStore.PrivateKeyEntry
        val privateKey = privateKeyEntry.privateKey
        val cipher = Cipher.getInstance(getTransformation())
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return cipher
    }

    override fun decryptToBytes(text: String): ByteArray? {
        if (!TextUtils.isEmpty(text)) {
            try {
                val cipherInputStream = CipherInputStream(ByteArrayInputStream(Base64.decode(text, Base64.DEFAULT)), getDecryptCipher())
                val values = ArrayList<Byte>()
                loop@ while (true) {
                    val byte = cipherInputStream.read()
                    if (byte == -1) {
                        break@loop
                    }
                    values.add(byte.toByte())
                }

                val bytes = ByteArray(values.size)
                for (i in bytes.indices) {
                    bytes[i] = values[i]
                }

                return bytes
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    // Known issue: https://issuetracker.google.com/issues/37095309 (crash with Persian language)
    // Force english locale
    private fun setLocale(locale: Locale) {
        Locale.setDefault(locale)
        val resources = context.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    abstract fun getProvider(): String

    abstract fun getAlias(): String
}