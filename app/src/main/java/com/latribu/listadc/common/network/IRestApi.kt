package com.latribu.listadc.common.network

import com.latribu.listadc.common.models.Product
import com.latribu.listadc.common.models.ResponseModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

// From https://medium.com/@abuhasanbaskara/android-kotlin-retrofit-with-coroutine-c29ec453ba09
// and  https://medium.com/swlh/simplest-post-request-on-android-kotlin-using-retrofit-e0a9db81f11a
// and  https://androidstudioo.com/6-easy-steps-post-request-in-kotlin-with-retrofit/

interface IRestApi {
    @GET(Url.PRODUCT_ENDPOINT)
    suspend fun getProducts(@Query("filter") filter: String) : Response<Product>

    @FormUrlEncoded
    @POST(Url.PRODUCT_ENDPOINT)
    fun addProduct(
        @Field("name") name: String,
        @Field("quantity") quantity: Int,
        @Field("comment") comment: String?
    ): Call<ResponseModel>

    @FormUrlEncoded
    @PUT(Url.PRODUCT_ENDPOINT)
    fun editProduct(
        @Field("productId") productId: Int,
        @Field("name") name: String,
        @Field("quantity") quantity: Int,
        @Field("comment") comment: String?
    ): Call<ResponseModel>
}