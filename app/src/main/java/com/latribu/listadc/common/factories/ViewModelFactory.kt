package com.latribu.listadc.common.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.latribu.listadc.common.network.ProductRepo
import com.latribu.listadc.common.network.RestApiHelper
import com.latribu.listadc.common.viewmodels.ProductViewModel

class ViewModelFactory(private val apiHelper: RestApiHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            return ProductViewModel(ProductRepo(apiHelper)) as T
        }
        throw IllegalArgumentException("Class not found")
    }
}