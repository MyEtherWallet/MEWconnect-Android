package com.myetherwallet.mewconnect.core.utils.crypto.keystore

import android.content.Context
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.myetherwallet.mewconnect.core.utils.MewLog
import java.security.KeyFactory
import java.security.KeyStoreException
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource


/**
 * Created by BArtWell on 16.04.2019.
 */

private const val TAG = "BiometricKeystoreHelper"
private const val PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore"
private const val TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
private const val ALIAS = "MewConnectBiometricKeys"

class BiometricKeystoreHelper(context: Context) : BaseRsaKeystoreHelper(context) {

    private var signedDecryptCipher: Cipher? = null

    constructor(context: Context, cipher: Cipher) : this(context) {
        signedDecryptCipher = cipher
    }

    override fun getDecryptCipher() = signedDecryptCipher ?: super.getDecryptCipher()

    override fun decryptToBytes(text: String): ByteArray? {
        return getDecryptCipher().doFinal(Base64.decode(text, Base64.NO_WRAP))
    }

    override fun getEncryptCipher(): Cipher {
        val key = keyStore.getCertificate(getAlias()).publicKey
        val unrestricted = KeyFactory.getInstance(key.algorithm).generatePublic(X509EncodedKeySpec(key.encoded))
        val spec = OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT)
        val cipher = Cipher.getInstance(getTransformation())
        cipher.init(Cipher.ENCRYPT_MODE, unrestricted, spec)
        return cipher
    }

    override fun encrypt(data: ByteArray): String {
        val bytes = getEncryptCipher().doFinal(data)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    override fun getKeyGenParameterSpec(builder: KeyGenParameterSpec.Builder): KeyGenParameterSpec = with(builder) {
        setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
        setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
        setUserAuthenticationRequired(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setInvalidatedByBiometricEnrollment(true)
        }
        build()
    }

    fun removeKey() {
        try {
            keyStore.deleteEntry(getAlias())
        } catch (e: KeyStoreException) {
            MewLog.i(TAG, "", e)
        }
    }

    override fun getProvider() = PROVIDER_ANDROID_KEYSTORE

    override fun getTransformation() = TRANSFORMATION

    override fun getAlias() = ALIAS
}