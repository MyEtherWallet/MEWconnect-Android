package com.myetherwallet.mewconnect.feature.scan.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.myetherwallet.mewconnect.BuildConfig
import com.myetherwallet.mewconnect.R
import com.myetherwallet.mewconnect.feature.main.activity.MainActivity
import com.myetherwallet.mewconnect.feature.scan.receiver.SocketServiceReceiver


/**
 * Created by BArtWell on 11.04.2019.
 */

private const val NOTIFICATION_ID = 1

class ServiceNotificationHelper {

    fun create(context: Context): Notification? {
        val channelId = BuildConfig.APPLICATION_ID + ".socket_service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.getString(R.string.service_notification_channel_name)
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            notificationChannel.description = context.getString(R.string.service_notification_channel_description)
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)
        }

        val action = NotificationCompat.Action.Builder(0,
                context.getString(R.string.service_notification_disconnect), getServicePendingIntent(context))
                .build()

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
        return notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.notification_icon)
                .setColor(ContextCompat.getColor(context, R.color.notification_icon_background))
                .setChannelId(channelId)
                .setContentTitle(context.getString(R.string.service_notification_title))
                .setContentText(context.getString(R.string.service_notification_text))
                .setContentIntent(getActivityPendingIntent(context))
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .addAction(action)
                .build()
    }

    private fun getActivityPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getServicePendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, SocketServiceReceiver::class.java)
        return PendingIntent.getBroadcast(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}