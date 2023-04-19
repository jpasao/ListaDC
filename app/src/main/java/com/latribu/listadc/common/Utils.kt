package com.latribu.listadc.common

import android.app.Activity
import android.os.Build
import java.io.Serializable

const val EXTRA_PRODUCT = "EXTRA_PRODUCT"

fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T
{
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        activity.intent.getSerializableExtra(name, clazz)!!
    else
        activity.intent.getSerializableExtra(name) as T
}