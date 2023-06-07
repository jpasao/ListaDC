package com.latribu.listadc.common

class Constants {
    companion object {
        // Common
        const val EXTRA_PRODUCT = "EXTRA_PRODUCT"

        // Api
        const val BASE_URL = "http://192.168.0.21/" //"http://pablosan.es/listacompra/api/"
        const val PRODUCT_ENDPOINT = "/listacompra/api/product"
        const val USER_ENDPOINT = "/listacompra/api/author"

        // Firebase
        const val TOPIC_NAME = "MAIN_TOPIC"
        const val POST_OPERATION = "POST"
        const val PUT_OPERATION = "PUT"
        const val PATCH_OPERATION = "PATCH"
    }
}