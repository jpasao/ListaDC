package com.latribu.listadc.common.repositories.historic

class RestApiHelper(private val apiInterface: ApiInterface) {
    suspend fun getHistoric(installationId: String, authorId: Int, days: Int) =
        apiInterface.getHistoric(installationId, authorId, days)
}