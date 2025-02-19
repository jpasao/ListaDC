package com.latribu.listadc.common

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.latribu.listadc.R
import com.latribu.listadc.common.Constants.Companion.SNACKBAR_DURATION
import com.latribu.listadc.common.factories.SharedViewModelFactory
import com.latribu.listadc.common.models.EmailData
import com.latribu.listadc.common.models.Status
import com.latribu.listadc.common.models.User
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

fun sendMail(data: EmailData) {
    val mSharedViewModel = ViewModelProvider(
        data.viewModel,
        SharedViewModelFactory(AppCreator.getApiHelperInstance())
    )[SharedViewModel::class.java]

    var subject: String = data.subject
    var message: String = data.message
    if (data.isErrorMessage) {
        val traceDetails = getTraceData(data.originView, data.userName, data.message)
        subject = traceDetails[0]
        message = traceDetails[1]
    }

    mSharedViewModel
        .sendMail(subject, message, data.installationId)
        .observe(data.viewLifecycle) {
            when(it.status) {
                Status.SUCCESS -> {
                    if (data.isErrorMessage) {
                        showMessage(data.originView, getString(data.originView.context, R.string.mail_sent))
                    }
                }
                Status.FAILURE -> { showMessage(data.originView, getString(data.originView.context, R.string.mail_fail)) }
                Status.LOADING -> { }
            }
        }
}

fun getTraceData(originView: View, userName: String, errorMessage: String): Array<String> {
    val response = arrayOf(
        getString(originView.context, R.string.mail_default_subject).plus(". $errorMessage"),
        getString(originView.context, R.string.mail_default_body).replace("{}", "[${userName}]")
    )
    val trace = Thread.currentThread().stackTrace
    if (trace.isNotEmpty()) {
        val sendMailIndex = trace.indexOfFirst { it.methodName == "sendEmail" }
        if (trace.size >= sendMailIndex + 1) {
            val callerTrace = trace[sendMailIndex + 1]
            val traceSubject = response[0].replace("[]", "[${callerTrace.fileName}]")
            val traceBody = response[1]
                .replace("[]", "[${callerTrace.lineNumber}]")
                .replace("()", "[${callerTrace.methodName}]")
            response[0] = traceSubject
            response[1] = traceBody
        } else {
            response[1] = getString(originView.context, R.string.mail_trace_not_found)
        }
    } else {
        response[1] = getString(originView.context, R.string.mail_empty_trace)
    }
    return response
}

fun getUserInitialCharacter(data: User): CharSequence {
    return if(data.name.isNotEmpty()) data.name.subSequence(0, 1) else "?"
}