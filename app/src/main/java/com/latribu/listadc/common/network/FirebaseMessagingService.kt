package com.latribu.listadc.common.network

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.util.Log
import androidx.collection.SimpleArrayMap
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.latribu.listadc.R
import com.latribu.listadc.common.models.NotificationModel

class FirebaseMessagingService: FirebaseMessagingService() {

    companion object {
        val notification = MutableLiveData<NotificationModel>()
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            val notificationData = NotificationModel(remoteMessage.data as SimpleArrayMap<String, String>)
            notification.postValue(notificationData)
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
            .setSmallIcon(R.drawable.twotone_receipt_long_24)
            .setAutoCancel(true)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        NotificationManagerCompat.from(this).notify(1, notification.build())

        super.onMessageReceived(remoteMessage)
    }
}