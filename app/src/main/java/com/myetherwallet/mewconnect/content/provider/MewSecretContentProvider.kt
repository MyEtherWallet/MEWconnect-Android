package com.myetherwallet.mewconnect.content.provider

import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.myetherwallet.mewconnect.MewApplication
import com.myetherwallet.mewconnect.core.persist.prefenreces.KeyStore
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.utils.MewLog
import com.myetherwallet.mewconnect.core.utils.crypto.keystore.encrypt.PasswordKeystoreHelper
import javax.inject.Inject

private const val TAG = "MewContentProvider"
private const val AUTHORITY = "com.myetherwallet.mewconnect.secret"
private const val PATH_GET_MNEMONIC = "mnemonic"
private const val ID_MNEMONIC = 0
private const val QUERY_PASSWORD = "password"
private const val PATH_PROTECT_MNEMONIC = "protect_mnemonic"
private const val ID_PROTECT_MNEMONIC = 1

class MewSecretContentProvider : BaseMewContentProvider() {

    @Inject
    lateinit var preferences: PreferencesManager
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    override fun onCreate(): Boolean {
        MewLog.d(TAG, "onCreate")
        (context?.applicationContext as MewApplication?)?.appComponent?.inject(this)
        uriMatcher.addURI(AUTHORITY, PATH_GET_MNEMONIC, ID_MNEMONIC)
        uriMatcher.addURI(AUTHORITY, PATH_PROTECT_MNEMONIC, ID_PROTECT_MNEMONIC)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        MewLog.d(TAG, "query")
        when (uriMatcher.match(uri)) {
            ID_MNEMONIC -> {
                MewLog.d(TAG, "Mnemonic")
                if (isCallingAppAllowed()) {
                    if (!preferences.applicationPreferences.wasExportedToMewWallet() &&
                            !preferences.applicationPreferences.isExportToMewWalletDenied()) {
                        val password = uri.getQueryParameter(QUERY_PASSWORD)
                        if (!password.isNullOrEmpty()) {
                            val keystoreHelper = PasswordKeystoreHelper(password)
                            val mnemonic = keystoreHelper.decrypt(preferences.applicationPreferences.getWalletMnemonic(KeyStore.PASSWORD))
                            if (mnemonic.isEmpty()) {
                                preferences.applicationPreferences.updateExportToMewWalletDenied()
                            } else {
                                preferences.applicationPreferences.resetExportToMewWalletDenied()
                                return createOneItemCursor(mnemonic)
                            }
                        }
                    }
                }
            }
            ID_PROTECT_MNEMONIC -> {
                preferences.applicationPreferences.setWasExportedToMewWallet(true)
                return createOneItemCursor(1)
            }
        }
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        return 0
    }
}
