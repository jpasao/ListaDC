package com.latribu.listadc.models

data class Author(val id: Int, val name: String, val image: String)

data class AuthorResponse(val data: ArrayList<Author>)