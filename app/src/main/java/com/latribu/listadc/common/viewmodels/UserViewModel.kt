package com.latribu.listadc.common.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.latribu.listadc.common.repositories.user.UserRepo
import com.latribu.listadc.common.network.Resource

class UserViewModel(private val mUserRepo: UserRepo) : ViewModel() {
    fun getAllUsers(installationId: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mUserRepo.getAllUsers(installationId)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }
}