package com.latribu.listadc.common.repositories.historic

import com.latribu.listadc.common.Constants
import com.latribu.listadc.common.Constants.Companion.INSTALLATION_HEADER
import com.latribu.listadc.common.models.Historic
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiInterface {
    @GET(Constants.HISTORIC_ENDPOINT)
    suspend fun getHistoric(
        @Header(INSTALLATION_HEADER) installationId: String,
        @Query("authorId") authorId: Int,
        @Query("days") days: Int
        ) : List<Historic>
}