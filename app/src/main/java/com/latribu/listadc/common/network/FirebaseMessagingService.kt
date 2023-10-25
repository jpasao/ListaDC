package com.latribu.listadc.common.network

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.firebase.messaging.FirebaseMessagingService
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants.Companion.MAIN_TOPIC
import com.latribu.listadc.common.Constants.Companion.MEAL_TOPIC
import com.latribu.listadc.common.Constants.Companion.OTHER_TOPIC
import com.latribu.listadc.common.models.FirebaseData
import com.latribu.listadc.common.models.User
import com.latribu.listadc.main.ListFragment
import com.latribu.listadc.main.MainActivity

class FirebaseMessagingService: FirebaseMessagingService() {
    private lateinit var savedUser: User
    private var buyMode: Boolean = false
    private lateinit var notificationData: FirebaseData
    companion object {
        // Observed in ListFragment.getNotification()
        val firebaseData = MutableLiveData<FirebaseData>()
        // Observed in MainActivity.readFirebaseMessage()
        val notificationMessage = MutableLiveData<String>()
        // Observed in MealFragment.getNotification()
        val mealNotificationMessage = MutableLiveData<Int>()
        // Observed in OtherFragment.getNotification()
        val otherNotificationMessage = MutableLiveData<Int>()
    }

    override fun handleIntent(intent: Intent?) {
        readPreferences()
        try {
            val body = intent!!.getStringExtra("gcm.notification.body").toString()
            val title = intent!!.getStringExtra("gcm.notification.title").toString()
            val hasData = intent!!.getStringExtra("productId")?.toIntOrNull() != null

            when (intent!!.getStringExtra("from").toString().substringAfterLast("/")) {
                MAIN_TOPIC -> {
                    notificationData = FirebaseData(intent)
                    firebaseData.postValue(notificationData)
                }
                MEAL_TOPIC -> {
                    val userId = body.toIntOrNull() ?: -1
                    if (userId != savedUser.id) mealNotificationMessage.postValue(userId)
                }
                OTHER_TOPIC -> {
                    val userId = body.toIntOrNull() ?: -1
                    if (userId != savedUser.id) otherNotificationMessage.postValue(userId)
                }
            }

            val sendNotification = hasData && notificationData.user != savedUser.id

            if (sendNotification) {
                if (buyMode) {
                    sendNotification(title, body)
                } else {
                    notificationMessage.postValue(body)
                }
            }
        } catch (e: Exception) {
            super.handleIntent(intent)
        }
    }

    private fun readPreferences() {
        val userObserver = Observer<User> { data ->
            savedUser = User(data.id, data.name, "")
        }
        val buyModeObserver = Observer<Boolean> { data ->
            buyMode = data
        }
        Handler(Looper.getMainLooper()).post {
            ListFragment.user.observeForever(userObserver)
            ListFragment.buyMode.observeForever(buyModeObserver)
        }
        if (!this::savedUser.isInitialized) {
            savedUser = User(-1, "Alguien", "")
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

        val channelId = MAIN_TOPIC
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