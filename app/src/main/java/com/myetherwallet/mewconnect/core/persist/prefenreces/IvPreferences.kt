package com.myetherwallet.mewconnect.core.persist.prefenreces

import android.content.SharedPreferences
import android.util.Base64

/**
 * Created by BArtWell on 07.05.2019.
 */

private const val IV_KEY_SUFFIX = "_IV"

interface IvPreferences {

    fun saveIv(ivKey: String, iv: ByteArray) {
        getSharedPreferences()
                .edit()
                .putString(ivKey + IV_KEY_SUFFIX,
                        Base64.encodeToString(iv, Base64.DEFAULT))
                .apply()
    }

    fun getIv(ivKey: String) =
            getSharedPreferences().getString(ivKey + IV_KEY_SUFFIX, null)?.let {
                Base64.decode(it, Base64.DEFAULT)
            }

    fun getSharedPreferences(): SharedPreferences
}