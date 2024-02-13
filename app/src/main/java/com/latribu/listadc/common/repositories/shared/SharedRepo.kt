package com.latribu.listadc.common.repositories.shared

class SharedRepo(private val apiHelper: RestApiHelper) {
    suspend fun sendMail(point: String, message: String, installationId: String) =
        apiHelper.sendMail(point, message, installationId)
}