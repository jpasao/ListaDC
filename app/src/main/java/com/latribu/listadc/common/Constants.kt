package com.latribu.listadc.common

import com.latribu.listadc.common.models.User

class Constants {
    companion object {
        // Common
        const val EXTRA_PRODUCT = "EXTRA_PRODUCT"
        val DEFAULT_USER: User = User(6, "Alguien", "")
        // Api
        const val BASE_URL = "http://pablosan.es/listacompra/api/"
        const val PRODUCT_ENDPOINT = "/listacompra/api/product"
        const val USER_ENDPOINT = "/listacompra/api/author"

        // Firebase
        const val TOPIC_NAME = "MAIN_TOPIC"
    }
}