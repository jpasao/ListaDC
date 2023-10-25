package com.latribu.listadc.common.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.latribu.listadc.common.repositories.other.OtherRepo
import com.latribu.listadc.common.repositories.other.RestApiHelper
import com.latribu.listadc.common.viewmodels.OtherViewModel

class OtherViewModelFactory(private val apiHelper: RestApiHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OtherViewModel::class.java)) {
            return OtherViewModel(OtherRepo(apiHelper)) as T
        }
        throw IllegalArgumentException("Class not found")
    }
}