package com.latribu.listadc.common

import android.app.Activity
import android.os.Build
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.io.Serializable
import java.text.Normalizer

fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T
{
    return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        activity.intent.getSerializableExtra(name, clazz)!!
    else
        activity.intent.getSerializableExtra(name) as T
}

fun showMessage(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
}

fun normalize(element: String?): String {
    var res = ""
    if (!element.isNullOrEmpty())
        res = Normalizer.normalize(element, Normalizer.Form.NFD).replace("\\p{M}", "")
    return res
}