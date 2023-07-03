package com.latribu.listadc.common.network

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.latribu.listadc.common.models.DataStoreManager
import kotlinx.coroutines.runBlocking

class BuyMode(appContext: Context, workerParams: WorkerParameters): Worker(appContext, workerParams) {

    private val dataStore = DataStoreManager(appContext.applicationContext)
    override fun doWork(): Result {
        runBlocking { dataStore.setBuyMode(false) }

        return Result.success()
    }
}

