package com.latribu.listadc.common.repositories.user

class RestApiHelper(private val apiInterface: ApiInterface) {
    suspend fun getAllUsers() =
        apiInterface.getAllUsers()
}