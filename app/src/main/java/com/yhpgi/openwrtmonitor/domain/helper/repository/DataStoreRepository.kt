package com.yhpgi.openwrtmonitor.domain.helper.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreRepository(context: Context) {

    private val dataStore = context.dataStore

    val getThemeString: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d(MainRepository.KEY_APP_SETTINGS, exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val theme =
                preferences[MainRepository.KEY_APP_THEME_STRING] ?: MainRepository.STRING_DEFAULT_THEME
            theme
        }

    suspend fun saveThemeString(setSelectedThemeString: String) {
        dataStore.edit { preferences ->
            preferences[MainRepository.KEY_APP_THEME_STRING] = setSelectedThemeString
        }
    }

    val getIPString: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d(MainRepository.KEY_APP_SETTINGS, exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val storedIpAddress =
                preferences[MainRepository.KEY_APP_IP_STRING] ?: MainRepository.DEFAULT_IP
            storedIpAddress
        }

    suspend fun saveIPString(newIP: String) {
        dataStore.edit { preferences ->
            preferences[MainRepository.KEY_APP_IP_STRING] = newIP
        }
    }

    val getTokenString: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d(MainRepository.KEY_APP_SETTINGS, exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val storedToken =
                preferences[MainRepository.KEY_APP_TOKEN_STRING] ?: MainRepository.DEFAULT_TOKEN
            storedToken
        }

    suspend fun saveTokenString(newToken: String) {
        dataStore.edit { preferences ->
            preferences[MainRepository.KEY_APP_TOKEN_STRING] = newToken
        }
    }

    val getLuciPathString: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d(MainRepository.KEY_APP_SETTINGS, exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val storedLuciPath =
                preferences[MainRepository.KEY_APP_LUCI_STRING] ?: MainRepository.DEFAULT_LUCI_PATH
            storedLuciPath
        }

    suspend fun saveLuciString(newLuciPath: String) {
        dataStore.edit { preferences ->
            preferences[MainRepository.KEY_APP_LUCI_STRING] = newLuciPath
        }
    }

    val getClashString: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d(MainRepository.KEY_APP_SETTINGS, exception.message.toString())
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val storedOpenClashPath =
                preferences[MainRepository.KEY_APP_CLASH_STRING] ?: MainRepository.DEFAULT_CLASH_PATH
            storedOpenClashPath
        }

    suspend fun saveClashString(newOpenClashPath: String) {
        dataStore.edit { preferences ->
            preferences[MainRepository.KEY_APP_CLASH_STRING] = newOpenClashPath
        }
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(MainRepository.KEY_APP_SETTINGS)
