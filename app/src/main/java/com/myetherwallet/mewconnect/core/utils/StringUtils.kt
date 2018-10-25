package com.myetherwallet.mewconnect.core.utils

import android.os.Build
import android.text.Html
import android.text.Spanned

object StringUtils {

    fun fromHtml(source: String): Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(source, Html.FROM_HTML_MODE_COMPACT)
    } else {
        Html.fromHtml(source)
    }
}