package com.myetherwallet.mewconnect.core.utils.crypto.keystore

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import javax.security.auth.x500.X500Principal

/**
 * Created by BArtWell on 08.07.2018.
 */

private const val PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore"
private const val TRANSFORMATION = "${KeyProperties.KEY_ALGORITHM_RSA}/${KeyProperties.BLOCK_MODE_ECB}/${KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1}"
private const val ALIAS = "MewWalletKeys"

@Deprecated("Use StorageKeystoreHelper instead")
class DeprecatedStorageKeystoreHelper(context: Context) : BaseRsaKeystoreHelper(context) {

    override fun getKeyGenParameterSpec(builder: KeyGenParameterSpec.Builder): KeyGenParameterSpec = builder
            .setCertificateSubject(X500Principal("CN=" + getAlias()))
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
            .build()

    override fun getProvider() = PROVIDER_ANDROID_KEYSTORE

    override fun getTransformation() = TRANSFORMATION

    override fun getAlias() = ALIAS
}