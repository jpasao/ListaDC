package com.latribu.listadc.common.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.latribu.listadc.common.repositories.meal.MealRepo
import com.latribu.listadc.common.repositories.meal.RestApiHelper
import com.latribu.listadc.common.viewmodels.MealViewModel

class MealViewModelFactory(private val apiHelper: RestApiHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealViewModel::class.java)) {
            return MealViewModel(MealRepo((apiHelper))) as T
        }
        throw IllegalArgumentException("Class not found")
    }
}