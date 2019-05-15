package com.myetherwallet.mewconnect.feature.scan.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.myetherwallet.mewconnect.feature.scan.service.SocketService

/**
 * Created by BArtWell on 11.04.2019.
 */

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        ServiceAlarmReceiver.cancel(context)
        SocketService.stop(context)
    }
}
