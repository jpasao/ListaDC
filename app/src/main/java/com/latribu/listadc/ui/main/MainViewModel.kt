package com.latribu.listadc.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.latribu.listadc.models.Product

class MainViewModel: ViewModel() {
    // Share data between MainActivity and Fragments
    val arrayListLiveData = MutableLiveData<Product>()
}