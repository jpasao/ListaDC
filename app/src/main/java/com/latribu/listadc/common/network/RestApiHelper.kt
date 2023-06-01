package com.latribu.listadc.common.network

class RestApiHelper (private val apiInterface: ApiInterface) {
    suspend fun getAllProducts() =
        apiInterface.getAllProducts("")

    suspend fun addProduct(name: String, quantity: Int, comment: String?) =
        apiInterface.addProduct(name, quantity, comment)

    suspend fun editProduct(productId: Int, name: String, quantity: Int, comment: String?) =
        apiInterface.editProduct(productId, name, quantity, comment)

    suspend fun checkProductItem(productId: Int, isChecked: String) =
        apiInterface.checkProductItem(productId, isChecked)
}