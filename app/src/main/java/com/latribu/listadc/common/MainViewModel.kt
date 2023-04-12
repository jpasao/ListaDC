package com.latribu.listadc.common

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.latribu.listadc.common.models.Product

class MainViewModel: ViewModel() {
    // Share data between MainActivity and Fragments
    val arrayListLiveData = MutableLiveData<Product>()
}