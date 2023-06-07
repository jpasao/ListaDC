package com.latribu.listadc.common.repositories.user

class UserRepo(private val apiHelper: RestApiHelper) {
    suspend fun getAllUsers() = apiHelper.getAllUsers()
}