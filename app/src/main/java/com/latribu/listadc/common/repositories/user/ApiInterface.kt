package com.latribu.listadc.common.repositories.user

import com.latribu.listadc.common.Constants
import com.latribu.listadc.common.models.User
import retrofit2.http.GET

interface ApiInterface {
    @GET(Constants.USER_ENDPOINT)
    suspend fun getAllUsers() : List<User>
}