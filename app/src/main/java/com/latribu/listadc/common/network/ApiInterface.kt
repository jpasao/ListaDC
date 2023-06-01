package com.latribu.listadc.common.network

import com.latribu.listadc.common.Constants
import com.latribu.listadc.common.models.ProductItem
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiInterface {
    @GET(Constants.PRODUCT_ENDPOINT)
    suspend fun getAllProducts(
        @Query("filter") filter: String
    ) : List<ProductItem>

    @FormUrlEncoded
    @PATCH(Constants.PRODUCT_ENDPOINT)
    suspend fun checkProductItem(
        @Field("productId") productId: Int,
        @Field("check") isChecked: String
    ) : List<ProductItem>

    @Headers("XDEBUG_SESSION_START: PHPSTORM")
    @FormUrlEncoded
    @POST(Constants.PRODUCT_ENDPOINT)
    suspend fun addProduct(
        @Field("name") name: String,
        @Field("quantity") quantity: Int,
        @Field("comment") comment: String?
    ) : ProductItem

    @Headers("XDEBUG_SESSION_START: PHPSTORM")
    @FormUrlEncoded
    @PUT(Constants.PRODUCT_ENDPOINT)
    suspend fun editProduct(
        @Field("productId") productId: Int,
        @Field("name") name: String,
        @Field("quantity") quantity: Int,
        @Field("comment") comment: String?
    ) : ProductItem
}