package com.latribu.listadc.network

import com.latribu.listadc.models.Product
import retrofit2.Response
import retrofit2.http.*

// From https://medium.com/@abuhasanbaskara/android-kotlin-retrofit-with-coroutine-c29ec453ba09

interface ProductService {
    @GET(Url.PRODUCT_ENDPOINT)
    suspend fun getProducts() : Response<Product>
}