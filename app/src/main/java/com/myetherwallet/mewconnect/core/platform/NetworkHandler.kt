package com.myetherwallet.mewconnect.core.platform

import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by BArtWell on 16.07.2018.
 */

@Singleton
class NetworkHandler
@Inject constructor(private val context: Context) {

    val isConnected: Boolean?
        get() {
            return (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo?.isConnectedOrConnecting
        }

}