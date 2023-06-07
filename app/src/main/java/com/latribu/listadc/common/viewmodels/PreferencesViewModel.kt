package com.latribu.listadc.common.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.latribu.listadc.common.models.DataStoreManager
import com.latribu.listadc.common.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PreferencesViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = DataStoreManager(application)

    val getUser = dataStore.readUser().asLiveData(Dispatchers.IO)

    fun setUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.saveUser(user)
        }
    }
}