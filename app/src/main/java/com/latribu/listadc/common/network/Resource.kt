package com.latribu.listadc.common.network

import com.latribu.listadc.common.models.Status

data class Resource<out T>
    (val status: Status, val data:T?, val message:String?){

    companion object{

        fun <T> success(data:T): Resource<T> =
            Resource(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(data:T?, message: String?):Resource<T> =
            Resource(status = Status.FAILURE, data = data, message = message)

        fun <T> loading(data:T?):Resource<T> =
            Resource(status = Status.LOADING, data = data, message = null)
    }

}