package com.latribu.listadc.common.models

import java.io.Serializable

data class User(
    val id: Int,
    val name: String,
    val image: String): Serializable