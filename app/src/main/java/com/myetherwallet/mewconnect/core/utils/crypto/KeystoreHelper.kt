package com.myetherwallet.mewconnect.core.utils.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.text.TextUtils
import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.security.auth.x500.X500Principal

/**
 * Created by BArtWell on 08.07.2018.
 */

private const val PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore"
private const val TYPE_RSA = "RSA"
private const val TRANSFORMATION = "RSA/ECB/PKCS1Padding"
private const val ALIAS = "MewWalletKeys"

class KeystoreHelper {

    private var keyStore: KeyStore = KeyStore.getInstance(PROVIDER_ANDROID_KEYSTORE)

    init {
        keyStore.load(null)
        if (!isAliasExists()) {
            createKeys()
        }
    }

    private fun isAliasExists() = Collections.list(keyStore.aliases()).contains(ALIAS)

    private fun createKeys() {
        val keyPairGenerator = KeyPairGenerator.getInstance(TYPE_RSA, PROVIDER_ANDROID_KEYSTORE)

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setCertificateSubject(X500Principal("CN=$ALIAS"))
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .build()
        keyPairGenerator.initialize(keyGenParameterSpec)

        keyPairGenerator.generateKeyPair()
    }

    fun encrypt(encryptedText: String): String {
        try {
            val privateKeyEntry = keyStore.getEntry(ALIAS, null) as KeyStore.PrivateKeyEntry
            val publicKey = privateKeyEntry.certificate.publicKey

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)

            val outputStream = ByteArrayOutputStream()
            val cipherOutputStream = CipherOutputStream(outputStream, cipher)
            cipherOutputStream.write(encryptedText.toByteArray(charset("UTF-8")))
            cipherOutputStream.close()

            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun decrypt(text: String): String {
        if (!TextUtils.isEmpty(text)) {
            try {
                val privateKeyEntry = keyStore.getEntry(ALIAS, null) as KeyStore.PrivateKeyEntry
                val privateKey = privateKeyEntry.privateKey

                val cipher = Cipher.getInstance(TRANSFORMATION)
                cipher.init(Cipher.DECRYPT_MODE, privateKey)

                val cipherInputStream = CipherInputStream(ByteArrayInputStream(Base64.decode(text, Base64.DEFAULT)), cipher)
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
                    bytes[i] = values.get(i)
                }

                return String(bytes, 0, bytes.size, Charset.forName("UTF-8"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ""
    }
}