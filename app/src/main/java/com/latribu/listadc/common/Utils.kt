package com.latribu.listadc.common

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.google.android.material.snackbar.Snackbar
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants.Companion.SNACKBAR_DURATION
import com.latribu.listadc.common.factories.SharedViewModelFactory
import com.latribu.listadc.common.models.Status
import com.latribu.listadc.common.repositories.shared.AppCreator
import com.latribu.listadc.common.viewmodels.SharedViewModel
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
    Snackbar.make(view, message, SNACKBAR_DURATION).show()
}

fun normalize(element: String?): String {
    var res = ""
    if (!element.isNullOrEmpty())
        res = Normalizer.normalize(element, Normalizer.Form.NFD).replace("\\p{M}", "")
    return res
}

fun showYesNoDialog(context: Context, title: String, message: String, listener: DialogInterface.OnClickListener){
    val builder = AlertDialog.Builder(context)
    builder
        .setTitle(title)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(R.string.ok_button_text) { dialog, id ->
            dialog.dismiss()
            listener.onClick(dialog, id)
        }
        .setNegativeButton(R.string.cancel_button_text) { dialog, _ ->
            dialog.dismiss()
        }
    val alert = builder.create()
    alert.show()
}

fun sendEmail(
    viewModel: ViewModelStoreOwner,
    viewLifecycle: LifecycleOwner,
    originView: View,
    page: String,
    message: String,
    installationId: String) {
    val mSharedViewModel = ViewModelProvider(
        viewModel,
        SharedViewModelFactory(AppCreator.getApiHelperInstance())
    )[SharedViewModel::class.java]

    mSharedViewModel
        .sendMail(page, message, installationId)
        .observe(viewLifecycle) {
            when(it.status) {
                Status.SUCCESS -> {
                    showMessage(originView, getString(originView.context, R.string.mail_sent))
                }
                Status.FAILURE -> {
                    showMessage(originView, getString(originView.context, R.string.mail_fail))
                }
                Status.LOADING -> { }
            }
        }

}