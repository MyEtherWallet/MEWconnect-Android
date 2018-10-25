package com.myetherwallet.mewconnect.feature.buy.data

import java.net.URLEncoder

/**
 * Created by BArtWell on 17.09.2018.
 */
data class PostRequest(
        val url: String,
        val postData: Map<String, String>
) {

    fun getEncodedPostData(): String {
        val result = StringBuilder()
        for ((key, value) in postData) {
            result.append(URLEncoder.encode(key, "UTF-8"))
            result.append("=")
            result.append(URLEncoder.encode(value, "UTF-8"))
            result.append("&")
        }

        

        return result.toString()
    }
}