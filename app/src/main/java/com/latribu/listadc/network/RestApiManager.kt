package com.latribu.listadc.network

import com.latribu.listadc.models.Product
import com.latribu.listadc.models.ProductItem
import com.latribu.listadc.models.ResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestApiManager {

    private val retrofitInstance: IRestApi = ServiceBuilder.buildInstance(IRestApi::class.java)

    suspend fun getProducts(filter: String): Response<Product> {
        return retrofitInstance.getProducts(filter)
    }

    fun addProduct(productData: ProductItem, onResult: (ResponseModel?) -> Unit) {
        retrofitInstance.addProduct(productData.name).enqueue(
            object : Callback<ResponseModel> {
                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                    onResult(null)
                }
                override fun onResponse(call: Call<ResponseModel>, response: Response<ResponseModel>) {
                    onResult(response.body())
                }
            }
        )
    }
}