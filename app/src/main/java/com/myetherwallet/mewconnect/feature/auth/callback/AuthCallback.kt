package com.myetherwallet.mewconnect.feature.auth.callback

import com.myetherwallet.mewconnect.core.persist.prefenreces.KeyStore
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.encrypt.BaseEncryptHelper

/**
 * Created by BArtWell on 10.09.2018.
 */
interface AuthCallback {

    fun onAuthResult(helper: BaseEncryptHelper, keyStore: KeyStore)

    fun onAuthCancel()
}