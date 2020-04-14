package com.myetherwallet.mewconnect.content.provider

import android.annotation.SuppressLint
import android.content.ContentProvider
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Binder
import android.os.Build
import com.myetherwallet.mewconnect.core.utils.MewLog
import pm.gnosis.utils.toHex
import java.security.MessageDigest

/**
 * Created by BArtWell on 27.02.2020.
 */

private const val TAG = "BaseMewContentProvider"
private const val MEW_WALLET_PACKAGE = "com.myetherwallet.mewwallet"
private const val MEW_WALLET_HASH_PRODUCTION = "1ab39219b62129ad94235448b1d046ea1b9c838fc0eb5f38ccc38ddb059952ae"
private const val MEW_WALLET_HASH_DEVELOPMENT = "669342a3e48184d23f74d318fd1d25961f00ce56dcd3cd190d4fe236992d0cbc"

abstract class BaseMewContentProvider : ContentProvider() {

    protected fun <T> createOneItemCursor(data: T): Cursor {
        val cursor = MatrixCursor(arrayOf("_id", "data"))
        cursor.newRow()
                .add(0)
                .add(data)
        return cursor
    }

    @SuppressLint("PackageManagerGetSignatures")
    protected fun isCallingAppAllowed(): Boolean {
        try {
            context?.let {
                val packageName = it.packageManager.getNameForUid(Binder.getCallingUid())
                MewLog.d(TAG, "Package: $packageName")
                if (packageName == MEW_WALLET_PACKAGE) {
                    MewLog.d(TAG, "Package allowed")
                    val messageDigest = MessageDigest.getInstance("SHA-256")
                    val signature = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        it.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES).signingInfo.signingCertificateHistory[0]
                    } else {
                        @Suppress("DEPRECATION")
                        it.packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures[0]
                    }
                    val hash = messageDigest.digest(signature.toByteArray()).toHex()
                    MewLog.d(TAG, "Hash $hash")
                    if (hash == MEW_WALLET_HASH_PRODUCTION || hash == MEW_WALLET_HASH_DEVELOPMENT) {
                        MewLog.d(TAG, "Hash allowed")
                        return true
                    }
                }
            }
        } catch (e: Exception) {
            MewLog.e(TAG, "isCallingAppAllowed exception", e)
        }
        return false
    }
}
