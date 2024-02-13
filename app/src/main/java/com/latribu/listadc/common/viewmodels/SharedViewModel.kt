package com.latribu.listadc.common.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.latribu.listadc.common.network.Resource
import com.latribu.listadc.common.repositories.shared.SharedRepo

class SharedViewModel(private val mSharedRepo: SharedRepo) : ViewModel() {
    fun sendMail(point: String, message: String, installationId: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mSharedRepo.sendMail(point, message, installationId)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }
}