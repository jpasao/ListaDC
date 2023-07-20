package com.latribu.listadc.common.repositories.user

import com.latribu.listadc.common.Constants
import com.latribu.listadc.common.Constants.Companion.INSTALLATION_HEADER
import com.latribu.listadc.common.models.User
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiInterface {
    @GET(Constants.USER_ENDPOINT)
    suspend fun getAllUsers(@Header(INSTALLATION_HEADER) installationId: String) : List<User>
}