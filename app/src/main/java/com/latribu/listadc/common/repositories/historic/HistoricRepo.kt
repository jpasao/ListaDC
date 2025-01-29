package com.latribu.listadc.common.repositories.historic

class HistoricRepo(private val apiHelper: RestApiHelper) {
    suspend fun getHistoric(installationId: String, authorId: Int, days: Int) =
        apiHelper.getHistoric(installationId, authorId, days)
}