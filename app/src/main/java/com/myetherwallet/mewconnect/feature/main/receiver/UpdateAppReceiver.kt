package com.myetherwallet.mewconnect.feature.main.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.myetherwallet.mewconnect.MewApplication
import com.myetherwallet.mewconnect.core.persist.prefenreces.PreferencesManager
import com.myetherwallet.mewconnect.feature.scan.utils.NotificationsUtils
import javax.inject.Inject

/**
 * Created by BArtWell on 28.04.2020.
 */

private const val ACTION = "android.intent.action.MY_PACKAGE_REPLACED"

class UpdateAppReceiver : BroadcastReceiver() {

    @Inject
    lateinit var preferences: PreferencesManager

    override fun onReceive(context: Context, intent: Intent) {
        (context.applicationContext as MewApplication).appComponent.inject(this)

        if (intent.action == ACTION) {
            NotificationsUtils.showMewWalletNotification(context, preferences.applicationPreferences)
        }
    }
}
