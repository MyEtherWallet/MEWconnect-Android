package com.myetherwallet.mewconnect.feature.scan.utils

import android.content.Context
import com.myetherwallet.mewconnect.core.persist.prefenreces.ApplicationPreferences

/**
 * Created by BArtWell on 28.04.2020.
 */

object NotificationsUtils {

    fun showMewWalletNotification(context: Context, preferences: ApplicationPreferences) {
        if (preferences.shouldShowMewWalletNotification()) {
            NotificationHelper.show(context)
        }
    }
}
