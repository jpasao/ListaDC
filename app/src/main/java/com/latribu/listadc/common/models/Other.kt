package com.latribu.listadc.common.models

import java.io.Serializable

data class Other(
    val id: Int,
    val parentId: Int,
    val parentName: String,
    val name: String,
    val isChecked: Int
) : Serializable