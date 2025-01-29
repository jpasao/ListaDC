package com.latribu.listadc.common.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.latribu.listadc.common.network.Resource
import com.latribu.listadc.common.repositories.historic.HistoricRepo

class HistoricViewModel(private val mHistoricRepo: HistoricRepo) : ViewModel() {
    fun getHistoric(installationId: String, authorId: Int, days: Int) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mHistoricRepo.getHistoric(installationId, authorId, days)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }
}