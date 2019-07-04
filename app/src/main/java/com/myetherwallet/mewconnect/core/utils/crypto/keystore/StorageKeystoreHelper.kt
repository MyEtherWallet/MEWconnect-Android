package com.myetherwallet.mewconnect.core.utils.crypto.keystore

import android.content.Context
import android.security.keystore.KeyProperties
import com.myetherwallet.mewconnect.core.persist.prefenreces.IvPreferences
import javax.crypto.Cipher

/**
 * Created by BArtWell on 08.07.2018.
 */

private const val PROVIDER_ANDROID_KEYSTORE = "AndroidKeyStore"
private const val TRANSFORMATION = "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}"
private const val ALIAS = "MewConnectStorageKeys"

class StorageKeystoreHelper(context: Context, ivPreferences: IvPreferences) : BaseAesKeystoreHelper(context, ivPreferences) {

    override fun getProvider() = PROVIDER_ANDROID_KEYSTORE

    override fun getTransformation() = TRANSFORMATION

    override fun getAlias() = ALIAS
}