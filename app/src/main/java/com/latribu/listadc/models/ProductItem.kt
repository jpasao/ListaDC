package com.latribu.listadc.models

import com.google.gson.annotations.SerializedName

data class ProductItem(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String,
    @SerializedName("isChecked")
    val isChecked: String?,
    @SerializedName("quantity")
    val quantity: Int?,
    @SerializedName("comments")
    val comments: String?)
