package com.latribu.listadc.common.repositories.product

import com.latribu.listadc.common.models.User

class RestApiHelper (private val apiInterface: ApiInterface) {
    suspend fun getAllProducts() =
        apiInterface.getAllProducts("")

    suspend fun addProduct(name: String, quantity: Int, comment: String?, author: User) =
        apiInterface.addProduct(name, quantity, comment, author.id, author.name)

    suspend fun editProduct(productId: Int, name: String, quantity: Int, comment: String?, author: User) =
        apiInterface.editProduct(productId, name, quantity, comment, author.id, author.name)

    suspend fun checkProductItem(productId: Int, isChecked: String, author: User) =
        apiInterface.checkProductItem(productId, isChecked, author.id, author.name)
}