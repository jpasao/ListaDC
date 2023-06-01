package com.latribu.listadc.common.models

import java.io.Serializable

data class ProductItem(
    val id: Int?,
    val name: String,
    var isChecked: String?,
    var quantity: Int?,
    val comment: String?): Serializable
