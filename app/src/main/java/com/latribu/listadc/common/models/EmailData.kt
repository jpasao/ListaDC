package com.latribu.listadc.common.models

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner

class EmailData (
    val viewModel: ViewModelStoreOwner,
    val viewLifecycle: LifecycleOwner,
    val originView: View,
    val subject: String,
    val message: String,
    val installationId: String,
    val userName: String,
    val isErrorMessage: Boolean = true,
)