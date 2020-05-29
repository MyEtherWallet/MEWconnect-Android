package com.myetherwallet.mewconnect.feature.scan.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.myetherwallet.mewconnect.BuildConfig
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.feature.scan.receiver.NotificationActionReceiver


/**
 * Created by BArtWell on 28.04.2020.
 */

private const val NOTIFICATION_ID = 2

object NotificationHelper {

    val EXTRA_MEW_WALLET = "mew_wallet_notification"

    fun show(context: Context) {
        val channelId = BuildConfig.APPLICATION_ID + ".internal_notifications"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.getString(R.string.update_notification_channel_name)
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationChannel.description = context.getString(R.string.update_notification_channel_description)
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
        val notification = notificationBuilder
                .setSmallIcon(R.drawable.notification_icon)
                .setColor(ContextCompat.getColor(context, R.color.notification_icon_background))
                .setChannelId(channelId)
                .setContentTitle(context.getString(R.string.update_notification_title))
                .setContentText(context.getString(R.string.update_notification_text))
                .setContentIntent(getActivityPendingIntent(context))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(Notification.CATEGORY_REMINDER)
                .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    private fun getActivityPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java)
        intent.putExtra(EXTRA_MEW_WALLET, true)
        return PendingIntent.getBroadcast(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}