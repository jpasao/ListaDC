package com.latribu.listadc.common.repositories.other

import android.app.Application
import com.latribu.listadc.common.network.RetrofitClientCalling.client

class AppCreator : Application() {
    companion object {
        private var mApiHelper: RestApiHelper? = null

        fun getApiHelperInstance(): RestApiHelper {
            if (mApiHelper == null) {
                mApiHelper = RestApiHelper(client!!.create(ApiInterface::class.java))
            }
            return mApiHelper!!
        }
    }
}