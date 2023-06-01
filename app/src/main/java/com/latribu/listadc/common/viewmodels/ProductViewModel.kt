package com.latribu.listadc.common.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.network.ProductRepo
import com.latribu.listadc.common.network.Resource

class ProductViewModel(private val mProductRepo: ProductRepo) : ViewModel() {
    fun getAllProducts() = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mProductRepo.getAllProducts()))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun checkProductItem(productData: ProductItem) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mProductRepo.checkProductItem(productData)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun addProduct(productData: ProductItem) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mProductRepo.addProduct(productData)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun editProduct(productData: ProductItem) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mProductRepo.editProduct(productData)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }
}