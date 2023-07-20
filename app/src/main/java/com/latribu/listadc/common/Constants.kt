package com.latribu.listadc.common

import com.latribu.listadc.common.models.User

class Constants {
    companion object {
        // Common
        const val EXTRA_PRODUCT = "EXTRA_PRODUCT"
        val DEFAULT_USER: User = User(6, "Alguien", "")
        const val REGULAR_ITEM = 0
        const val SEPARATOR_ITEM = 1

        // Api
        const val BASE_URL = "http://pablosan.es/listacompra/api/"
        const val PRODUCT_ENDPOINT = "/listacompra/api/product"
        const val USER_ENDPOINT = "/listacompra/api/author"
        const val INSTALLATION_HEADER = "INSTALLATIONID"

        // Firebase
        const val TOPIC_NAME = "MAIN_TOPIC"
    }
}