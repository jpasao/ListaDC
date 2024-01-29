package com.latribu.listadc.common.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.models.User
import com.latribu.listadc.common.network.Resource
import com.latribu.listadc.common.repositories.product.ProductRepo

class ProductViewModel(private val mProductRepo: ProductRepo) : ViewModel() {
    fun getAllProducts(installationId: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mProductRepo.getAllProducts(installationId)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun checkProductItem(productData: ProductItem, author: User, installationId: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mProductRepo.checkProductItem(productData, author, installationId)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun addProduct(productData: ProductItem, author: User, installationId: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mProductRepo.addProduct(productData, author, installationId)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun editProduct(productData: ProductItem, author: User, installationId: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mProductRepo.editProduct(productData, author, installationId)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }

    fun deleteProduct(productData: ProductItem, author: User, installationId: String) = liveData {
        emit(Resource.loading(null))
        try {
            emit(Resource.success(mProductRepo.deleteProduct(productData, author, installationId)))
        } catch (e: Exception) {
            emit(Resource.error(null, e.message.toString()))
        }
    }
}