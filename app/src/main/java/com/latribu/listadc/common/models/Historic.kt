package com.latribu.listadc.common.models

import java.io.Serializable

data class Historic(
    val id: Int,
    val userId: Int,
    val userName: String,
    val itemId: Int,
    val itemName: String,
    val operationId: Int,
    val createdAt: String
) : Serializable