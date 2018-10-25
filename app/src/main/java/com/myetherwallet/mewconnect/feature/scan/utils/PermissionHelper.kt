package com.myetherwallet.mewconnect.feature.scan.utils

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import java.util.*

/**
 * Created by BArtWell on 14.09.2018.
 */

private const val REQUEST_CODE = 1

class PermissionHelper {

    private var permissions: Array<String>
    private var callback: (isGranted: Boolean) -> Unit

    constructor(permissions: Array<String>, callback: (isGranted: Boolean) -> Unit) {
        this.permissions = permissions
        this.callback = callback
    }

    constructor(permission: String, callback: (isGranted: Boolean) -> Unit) {
        permissions = arrayOf(permission)
        this.callback = callback
    }

    fun requestPermissions(fragment: Fragment) {
        if (checkPermission(fragment.requireContext())) {
            callback(true)
        } else {
            fragment.requestPermissions(permissions, REQUEST_CODE)
        }
    }

    fun checkPermission(context: Context): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun shouldShowRequestPermissionsRationale(fragment: Fragment): Boolean {
        for (permission in permissions) {
            if (shouldShowRequestPermissionRationale(fragment, permission)) {
                return true
            }
        }
        return false
    }

    fun shouldShowRequestPermissionRationale(fragment: Fragment, permission: String): Boolean {
        return fragment.shouldShowRequestPermissionRationale(permission)
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE && Arrays.equals(permissions, this.permissions)) {
            var isGranted = true
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false
                    break
                }
            }
            callback(isGranted)
        }
    }
}