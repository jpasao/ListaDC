package com.latribu.listadc.common.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Handler
import android.os.Looper
import androidx.collection.SimpleArrayMap
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.latribu.listadc.R
import com.latribu.listadc.common.models.FirebaseData
import com.latribu.listadc.common.models.User
import com.latribu.listadc.main.ListFragment
import com.latribu.listadc.main.MainActivity

class FirebaseMessagingService: FirebaseMessagingService() {
    private lateinit var savedUser: User
    private lateinit var notificationData: FirebaseData
    companion object {
        val firebaseData = MutableLiveData<FirebaseData>()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        readUser()
        val hasData = remoteMessage.data.isNotEmpty()
        val hasNotification = remoteMessage.notification != null
        if (hasData) {
            notificationData = FirebaseData(remoteMessage.data as SimpleArrayMap<String, String>)
            firebaseData.postValue(notificationData)
        }

        if (hasData && hasNotification && notificationData.user!! != savedUser.id) {
            sendNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
        }

        super.onMessageReceived(remoteMessage)
    }

    private fun readUser() {
        val userObserver = Observer<User> { data ->
            savedUser = User(data.id, data.name, "")
        }
        Handler(Looper.getMainLooper()).post {
            ListFragment.user.observeForever(userObserver)
        }
    }

    private fun sendNotification(messageTitle: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = com.latribu.listadc.common.Constants.TOPIC_NAME
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.twotone_receipt_long_24)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Channel human readable title",
            NotificationManager.IMPORTANCE_DEFAULT,
        )
        notificationManager.createNotificationChannel(channel)

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}