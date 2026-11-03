package com.ccstudio.mpaz_pro_6.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "mpaz_session")

data class UserSession(
    val userId: Long,
    val username: String,
    val rol: String
)

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_USER_ROL = stringPreferencesKey("user_rol")
    }

    val userSessionFlow: Flow<UserSession?> = context.sessionDataStore.data
        .map { prefs ->
            val isLoggedIn = prefs[KEY_IS_LOGGED_IN] ?: false
            if (isLoggedIn) {
                val userId = prefs[KEY_USER_ID] ?: 0L
                val username = prefs[KEY_USERNAME] ?: ""
                val rol = prefs[KEY_USER_ROL] ?: ""
                if (username.isNotEmpty() && rol.isNotEmpty()) {
                    UserSession(userId = userId, username = username, rol = rol)
                } else null
            } else {
                null
            }
        }

    suspend fun saveSession(userId: Long, username: String, rol: String) {
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = true
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USERNAME] = username
            prefs[KEY_USER_ROL] = rol
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
