package com.latribu.listadc.common.repositories.product

import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.models.User

class ProductRepo(private val apiHelper: RestApiHelper) {
    suspend fun getAllProducts(installationId: String) = apiHelper.getAllProducts(installationId)

    suspend fun checkProductItem(productData: ProductItem, author: User, installationId: String) =
        apiHelper.checkProductItem(productData.id!!, productData.isChecked!!, author, installationId)

    suspend fun addProduct(productData: ProductItem, author: User) =
        apiHelper.addProduct(productData.name, productData.quantity!!, productData.comment, author)

    suspend fun editProduct(productData: ProductItem, author: User) =
        apiHelper.editProduct(productData.id!!, productData.name, productData.quantity!!, productData.comment, author)
}