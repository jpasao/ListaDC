package com.latribu.listadc.common.repositories.other

class OtherRepo(private val apiHelper: RestApiHelper) {
    suspend fun getAllOthers(installationId: String) =
        apiHelper.getAllOthers(installationId)

    suspend fun saveOther(id: Int, parentId: Int, name: String, isChecked: Int, authorId: Int, installationId: String) =
        apiHelper.saveOther(id, parentId, name, isChecked, authorId, installationId)
}