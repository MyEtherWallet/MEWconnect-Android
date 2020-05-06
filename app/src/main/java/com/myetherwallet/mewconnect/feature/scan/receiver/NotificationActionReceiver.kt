package com.myetherwallet.mewconnect.feature.scan.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.myetherwallet.mewconnect.core.utils.LaunchUtils
import com.myetherwallet.mewconnect.feature.scan.service.SocketService
import com.myetherwallet.mewconnect.feature.scan.utils.NotificationHelper

/**
 * Created by BArtWell on 11.04.2019.
 */

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra(NotificationHelper.EXTRA_MEW_WALLET)) {
            LaunchUtils.openWebSite(context, "https://bit.ly/mewwallet-android")
        } else {
            ServiceAlarmReceiver.cancel(context)
            SocketService.stop(context)
        }
    }
}
