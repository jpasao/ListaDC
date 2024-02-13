package com.latribu.listadc.common.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.latribu.listadc.common.repositories.shared.RestApiHelper
import com.latribu.listadc.common.repositories.shared.SharedRepo
import com.latribu.listadc.common.viewmodels.SharedViewModel

class SharedViewModelFactory(private val apiHelper: RestApiHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel(SharedRepo(apiHelper)) as T
        }
        throw IllegalArgumentException("Class not found")
    }
}