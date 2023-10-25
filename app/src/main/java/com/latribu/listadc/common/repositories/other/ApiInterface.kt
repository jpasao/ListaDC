package com.latribu.listadc.common.repositories.other

import com.latribu.listadc.common.Constants
import com.latribu.listadc.common.models.Other
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {
    @GET(Constants.OTHER_ENDPOINT)
    suspend fun getAllOthers(
        @Header(Constants.INSTALLATION_HEADER) installationId: String
    ) : List<Other>

    @FormUrlEncoded
    @POST(Constants.OTHER_ENDPOINT)
    suspend fun saveOther(
        @Field("Id") id: Int,
        @Field("parentId") parentId: Int,
        @Field("name") name: String,
        @Field("check") isChecked: Int,
        @Field("authorId") authorId: Int,
        @Header(Constants.INSTALLATION_HEADER) installationId: String
    )
}