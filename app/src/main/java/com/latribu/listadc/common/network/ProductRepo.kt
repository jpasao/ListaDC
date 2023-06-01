package com.latribu.listadc.common.network

import com.latribu.listadc.common.models.ProductItem

class ProductRepo(private val apiHelper: RestApiHelper) {
    suspend fun getAllProducts() = apiHelper.getAllProducts()

    suspend fun checkProductItem(productData: ProductItem) =
        apiHelper.checkProductItem(productData.id!!, productData.isChecked!!)

    suspend fun addProduct(productData: ProductItem) =
        apiHelper.addProduct(productData.name, productData.quantity!!, productData.comment)

    suspend fun editProduct(productData: ProductItem) =
        apiHelper.editProduct(productData.id!!, productData.name, productData.quantity!!, productData.comment)
}