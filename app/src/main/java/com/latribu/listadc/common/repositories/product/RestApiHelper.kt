package com.latribu.listadc.common.repositories.product

import com.latribu.listadc.common.models.User

class RestApiHelper (private val apiInterface: ApiInterface) {
    suspend fun getAllProducts(installationId: String) =
        apiInterface.getAllProducts(installationId, "")

    suspend fun addProduct(name: String, quantity: Int, comment: String?, author: User, installationId: String) =
        apiInterface.addProduct(name, quantity, comment, author.id, author.name, installationId)

    suspend fun editProduct(productId: Int, name: String, quantity: Int, comment: String?, author: User, installationId: String) =
        apiInterface.editProduct(productId, name, quantity, comment, author.id, author.name, installationId)

    suspend fun checkProductItem(productId: Int, isChecked: String, author: User, installationId: String) =
        apiInterface.checkProductItem(productId, isChecked, author.id, author.name, installationId)
}