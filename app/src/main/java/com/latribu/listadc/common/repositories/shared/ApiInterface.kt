package com.latribu.listadc.common.repositories.shared

import com.latribu.listadc.common.Constants
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {
    @FormUrlEncoded
    @POST(Constants.SYSTEM_ENDPOINT)
    suspend fun sendMail(
        @Field("point") point: String,
        @Field("message") message: String,
        @Header(Constants.INSTALLATION_HEADER) installationId: String
    )
}