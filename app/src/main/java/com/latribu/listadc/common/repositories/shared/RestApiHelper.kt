package com.latribu.listadc.common.repositories.shared

class RestApiHelper(private val apiInterface: ApiInterface) {
    suspend fun sendMail(point: String, message: String, installationId: String) =
        apiInterface.sendMail(point, message, installationId)
}