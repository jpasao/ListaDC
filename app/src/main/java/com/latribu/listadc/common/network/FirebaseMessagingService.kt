package com.latribu.listadc.common.network

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.latribu.listadc.R

class FirebaseMessagingService  : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }

        val CHANNEL_ID = com.latribu.listadc.common.Constants.TOPIC_NAME

        val title = remoteMessage.notification!!.title
        val text = remoteMessage.notification!!.body

        val channel = NotificationChannel(CHANNEL_ID, "Heads Up Notification", NotificationManager.IMPORTANCE_HIGH)

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        val notification: Notification.Builder = Notification.Builder(this, CHANNEL_ID)
        notification
            .setContentText(text)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        NotificationManagerCompat.from(this).notify(1, notification.build())

        super.onMessageReceived(remoteMessage)
    }
}