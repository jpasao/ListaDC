package com.latribu.listadc.common.models

import java.io.Serializable

data class ProductItem(
    val id: Int?,
    val name: String,
    val isChecked: String?,
    val quantity: Int?,
    val comment: String?): Serializable
