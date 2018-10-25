package com.myetherwallet.mewconnect.core.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils

/**
 * Created by BArtWell on 28.08.2018.
 */

private const val URI_PREFIX_PHONE = "tel:"

object LaunchUtils {

    fun openMailApp(context: Context?, email: String?, subject: String? = null) {
        if (context != null && email != null) {
            try {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "message/rfc822"
                if (!TextUtils.isEmpty(subject)) {
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                }
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                context.startActivity(Intent.createChooser(intent, null))
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    fun openCaller(context: Context?, phone: String?) {
        if (context != null && phone != null) {
            try {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse(URI_PREFIX_PHONE + phone)
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    fun openWebSite(context: Context?, url: String?) {
        if (context != null && url != null) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        }
    }
}