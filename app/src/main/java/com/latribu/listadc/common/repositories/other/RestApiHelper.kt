package com.latribu.listadc.common.repositories.other

class RestApiHelper(private val apiInterface: ApiInterface) {
    suspend fun getAllOthers(installationId: String) =
        apiInterface.getAllOthers(installationId)

    suspend fun saveOther(id: Int, parentId: Int, name: String, isChecked: Int, authorId: Int, installationId: String) =
        apiInterface.saveOther(id, parentId, name, isChecked, authorId, installationId)
}