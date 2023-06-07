package com.latribu.listadc.common.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.latribu.listadc.common.repositories.user.RestApiHelper
import com.latribu.listadc.common.repositories.user.UserRepo
import com.latribu.listadc.common.viewmodels.UserViewModel

class UserViewModelFactory(private val apiHelper: RestApiHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(UserRepo(apiHelper)) as T
        }
        throw IllegalArgumentException("Class not found")
    }
}