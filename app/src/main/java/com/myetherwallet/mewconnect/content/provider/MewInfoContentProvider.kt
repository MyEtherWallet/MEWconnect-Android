package com.myetherwallet.mewconnect.content.provider

import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.myetherwallet.mewconnect.BuildConfig
import com.myetherwallet.mewconnect.MewApplication
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.core.utils.MewLog
import javax.inject.Inject

private const val TAG = "MewContentProvider"
private const val AUTHORITY = "com.myetherwallet.mewconnect.info"
private const val PATH_VERSION = "version"
private const val ID_VERSION = 0
private const val PATH_IS_WALLET_AVAILABLE = "is_wallet_available"
private const val ID_IS_AVAILABLE = 1

class MewInfoContentProvider : BaseMewContentProvider() {

    @Inject
    lateinit var preferences: PreferencesManager
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    override fun onCreate(): Boolean {
        MewLog.d(TAG, "onCreate")
        (context?.applicationContext as MewApplication?)?.appComponent?.inject(this)
        uriMatcher.addURI(AUTHORITY, PATH_VERSION, ID_VERSION)
        uriMatcher.addURI(AUTHORITY, PATH_IS_WALLET_AVAILABLE, ID_IS_AVAILABLE)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        MewLog.d(TAG, "query")
        when (uriMatcher.match(uri)) {
            ID_VERSION -> {
                MewLog.d(TAG, "Version")
                return createOneItemCursor(BuildConfig.VERSION_CODE)
            }
            ID_IS_AVAILABLE -> {
                MewLog.d(TAG, "Is wallet available")
                val data = if (preferences.getCurrentWalletPreferences().isWalletExists() &&
                        !preferences.applicationPreferences.wasExportedToMewWallet() &&
                        !preferences.applicationPreferences.isExportToMewWalletDenied()) {
                    1
                } else {
                    0
                }
                return createOneItemCursor(data)
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
