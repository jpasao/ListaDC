package com.latribu.listadc.common.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.latribu.listadc.common.network.Resource
import com.latribu.listadc.common.repositories.other.OtherRepo

class OtherViewModel(private val mOtherRepo: OtherRepo) : ViewModel() {
    fun getAllOthers(installationId: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mOtherRepo.getAllOthers(installationId)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun saveOther(id: Int, parentId: Int, name: String, isChecked: Int, authorId: Int, installationId: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mOtherRepo.saveOther(id, parentId, name, isChecked, authorId, installationId)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }
}