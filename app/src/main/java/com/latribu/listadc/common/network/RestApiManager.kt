package com.latribu.listadc.common.network

import com.latribu.listadc.common.models.Product
import com.latribu.listadc.common.models.ProductItem
import com.latribu.listadc.common.models.ResponseModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestApiManager {

    private val retrofitInstance: IRestApi = ServiceBuilder.buildInstance(IRestApi::class.java)

    suspend fun getProducts(filter: String): Response<Product> {
        return retrofitInstance.getProducts(filter)
    }

    fun saveProduct(productData: ProductItem, onResult: (ResponseModel?) -> Unit) {
        if (productData.id != null) {
            retrofitInstance.editProduct(productData.id, productData.name, productData.quantity!!, productData.comment).enqueue(
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
        else {
            retrofitInstance.addProduct(productData.name, productData.quantity!!, productData.comment).enqueue(
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

    fun checkProduct(productData: ProductItem, onResult: (Product?) -> Unit) {
        retrofitInstance.checkProduct(productData.id!!, productData.isChecked!!).enqueue(
            object : Callback<Product> {
                override fun onFailure(call: Call<Product>, t: Throwable) {
                    onResult(null)
                }

                override fun onResponse(call: Call<Product>, response: Response<Product>) {
                    onResult(response.body())
                }
            }
        )
    }
}