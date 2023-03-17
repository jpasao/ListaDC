package com.latribu.listadc.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.latribu.listadc.MainActivity
import com.latribu.listadc.models.Product

class MainViewModel: ViewModel() {
    // Share data between MainActivity and Fragments
    val arrayListLiveData = MutableLiveData<Product>()

    private val _productId = MutableLiveData<Int?>()
    val productId get() = _productId

    fun onProductClicked(id: Int?) {
        _productId.value = id
        Log.d("pruebas", "onproductClicked!, id ${id}")
    }

    fun onImageClicked(id: Int?) {
        _productId.value = id
        Log.d("pruebas", "onImageClicked!, id ${id}")
    }
}