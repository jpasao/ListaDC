package com.latribu.listadc.common

import com.latribu.listadc.BuildConfig
import com.latribu.listadc.common.models.User

class Constants {
    companion object {
        // Common
        const val EXTRA_PRODUCT = "EXTRA_PRODUCT"
        const val EXTRA_MEAL = "EXTRA_MEAL"
        val DEFAULT_USER: User = User(6, "Alguien", "")
        const val REGULAR_ITEM = 0
        const val SEPARATOR_ITEM = 1
        const val PARENT = 0
        const val CHILD = 1
        const val OPACITY_NORMAL = 0.87f
        const val OPACITY_FADED =  0.54f
        const val SNACKBAR_DURATION = 5000
        const val TAB_MAINLIST = 0
        const val TAB_MEALS = 1
        const val TAB_OTHERS = 2
        const val QUEUE_LIMIT = 3

        // Api
        private val server = BuildConfig.SERVER_URL
        private const val apiPath = "/listacompra/api/"
        val BASE_URL =  "http://${server}/listacompra/api/"
        const val PRODUCT_ENDPOINT = "${apiPath}product"
        const val USER_ENDPOINT = "${apiPath}author"
        const val MEAL_ENDPOINT = "${apiPath}meal"
        const val OTHER_ENDPOINT = "${apiPath}other"
        const val SYSTEM_ENDPOINT = "${apiPath}system"
        const val HISTORIC_ENDPOINT = "${apiPath}historic"
        const val INSTALLATION_HEADER = "INSTALLATIONID"

        // Firebase
        const val MAIN_TOPIC = "MAIN_TOPIC"
        const val MEAL_TOPIC = "MEAL_TOPIC"
        const val OTHER_TOPIC = "OTHER_TOPIC"
    }
}