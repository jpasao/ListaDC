package com.latribu.listadc.common.models

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreManager(private val context: Context) {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "settings")
        private val USER = intPreferencesKey("user")
        private val USER_NAME = stringPreferencesKey("username")
        private val BUY_MODE = booleanPreferencesKey("buymode")
    }

    suspend fun saveUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER] = user.id
            preferences[USER_NAME] = user.name
        }
    }

    fun readUser(): Flow<User> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                User(preferences[USER] ?: 0, preferences[USER_NAME] ?: "", "")
            }
    }

    suspend fun setBuyMode(buyMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BUY_MODE] = buyMode
        }
    }

    fun readBuyMode(): Flow<Boolean> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map {preferences ->
                preferences[BUY_MODE] ?: false
            }
    }
}