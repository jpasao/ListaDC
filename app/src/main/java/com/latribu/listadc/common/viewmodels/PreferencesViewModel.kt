package com.latribu.listadc.common.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.latribu.listadc.common.models.DataStoreManager
import com.latribu.listadc.common.models.User
import com.latribu.listadc.common.network.BuyMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class PreferencesViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = DataStoreManager(application)

    val getUser = dataStore.readUser().asLiveData(Dispatchers.IO)

    fun setUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.saveUser(user)
        }
    }

    val getBuyMode = dataStore.readBuyMode().asLiveData(Dispatchers.IO)

    fun setBuyMode(buyMode: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.setBuyMode(buyMode)

            val switchOffTag = "buymode"
            if (buyMode) {
                val now = System.currentTimeMillis()
                val calendar: Calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, 22)
                calendar.set(Calendar.MINUTE, 0)

                val tenOClock = (calendar.timeInMillis - now) / 1000 / 60

                val switchOffWorkRequest: WorkRequest =
                    OneTimeWorkRequestBuilder<BuyMode>()
                        .setInitialDelay(tenOClock, TimeUnit.MINUTES)
                        .addTag(switchOffTag)
                        .build()

                WorkManager
                    .getInstance(getApplication())
                    .enqueue(switchOffWorkRequest)
            } else {
                WorkManager
                    .getInstance(getApplication())
                    .cancelAllWorkByTag(switchOffTag)
            }
        }
    }
}