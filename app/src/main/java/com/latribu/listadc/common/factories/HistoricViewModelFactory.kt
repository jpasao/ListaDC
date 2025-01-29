package com.latribu.listadc.common.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.latribu.listadc.common.repositories.historic.HistoricRepo
import com.latribu.listadc.common.repositories.historic.RestApiHelper
import com.latribu.listadc.common.viewmodels.HistoricViewModel

class HistoricViewModelFactory(private val apiHelper: RestApiHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoricViewModel::class.java)) {
            return HistoricViewModel(HistoricRepo(apiHelper)) as T
        }
        throw IllegalArgumentException("Class not found")
    }
}