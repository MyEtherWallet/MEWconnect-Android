package com.myetherwallet.mewconnect.feature.main.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * Created by BArtWell on 04.09.2018.
 */

private const val ACTION = "android.net.conn.CONNECTIVITY_CHANGE"

class NetworkStateReceiver(private val listener: () -> Unit) : BroadcastReceiver() {

    fun register(context: Context) {
        val filter = IntentFilter()
        filter.addAction(ACTION)
        context.registerReceiver(this, filter)
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(this)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION) {
            listener()
        }
    }
}