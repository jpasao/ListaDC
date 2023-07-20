package com.latribu.listadc.common.repositories.product

import com.latribu.listadc.common.Constants
import com.latribu.listadc.common.Constants.Companion.INSTALLATION_HEADER
import com.latribu.listadc.common.models.ProductItem
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface ApiInterface {
    @GET(Constants.PRODUCT_ENDPOINT)
    suspend fun getAllProducts(
        @Header(INSTALLATION_HEADER) installationId: String,
        @Query("filter") filter: String
    ) : List<ProductItem>

    @FormUrlEncoded
    @PATCH(Constants.PRODUCT_ENDPOINT)
    suspend fun checkProductItem(
        @Field("productId") productId: Int,
        @Field("check") isChecked: String,
        @Field("authorId") authorId: Int,
        @Field("authorName") authorName: String
    ) : List<ProductItem>

    @FormUrlEncoded
    @POST(Constants.PRODUCT_ENDPOINT)
    suspend fun addProduct(
        @Field("name") name: String,
        @Field("quantity") quantity: Int,
        @Field("comment") comment: String?,
        @Field("authorId") authorId: Int,
        @Field("authorName") authorName: String
    ) : ProductItem

    @FormUrlEncoded
    @PUT(Constants.PRODUCT_ENDPOINT)
    suspend fun editProduct(
        @Field("productId") productId: Int,
        @Field("name") name: String,
        @Field("quantity") quantity: Int,
        @Field("comment") comment: String?,
        @Field("authorId") authorId: Int,
        @Field("authorName") authorName: String
    ) : ProductItem
}