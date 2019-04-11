package com.myetherwallet.mewconnect.feature.scan.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.myetherwallet.mewconnect.feature.scan.service.SocketService

class SocketServiceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        SocketService.shutdown(context)
    }
}
