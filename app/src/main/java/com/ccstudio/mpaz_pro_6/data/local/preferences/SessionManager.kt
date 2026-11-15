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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "mpaz_session")

data class UserSession(
    val userId: Long,
    val username: String,
    val rol: String,
    val token: String? = null,
    val tokenType: String = "Bearer",
    val expiresIn: Long = 0,
    val expiresAt: Long = 0
)

class SessionManager(private val context: Context) {

    @Volatile
    private var tokenInMemory: String? = null

    companion object {
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_USER_ID = longPreferencesKey("user_id")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_USER_ROL = stringPreferencesKey("user_rol")
        private val KEY_TOKEN = stringPreferencesKey("token")
        private val KEY_TOKEN_TYPE = stringPreferencesKey("token_type")
        private val KEY_EXPIRES_IN = longPreferencesKey("expires_in")
        private val KEY_EXPIRES_AT = longPreferencesKey("expires_at")
    }

    val userSessionFlow: Flow<UserSession?> = context.sessionDataStore.data
        .map { prefs ->
            val isLoggedIn = prefs[KEY_IS_LOGGED_IN] ?: false
            if (isLoggedIn) {
                val userId = prefs[KEY_USER_ID] ?: 0L
                val username = prefs[KEY_USERNAME] ?: ""
                val rol = prefs[KEY_USER_ROL] ?: ""
                if (username.isNotEmpty() && rol.isNotEmpty()) {
                    UserSession(
                        userId = userId,
                        username = username,
                        rol = rol,
                        token = prefs[KEY_TOKEN],
                        tokenType = prefs[KEY_TOKEN_TYPE] ?: "Bearer",
                        expiresIn = prefs[KEY_EXPIRES_IN] ?: 0,
                        expiresAt = prefs[KEY_EXPIRES_AT] ?: 0
                    )
                } else null
            } else {
                null
            }
        }

    /** Token actual, sin suspender. Lo usa el interceptor de OkHttp. */
    fun currentToken(): String? = tokenInMemory

    /** Lee la sesión completa desde DataStore. */
    suspend fun loadSession(): UserSession? = userSessionFlow.first()

    /** Restaura el token en memoria al arrancar la app. */
    suspend fun restoreToken() {
        tokenInMemory = loadSession()?.token
    }

    suspend fun saveSession(userId: Long, username: String, rol: String) {
        saveRemoteSession(
            userId = userId,
            username = username,
            rol = rol,
            token = null,
            tokenType = "Bearer",
            expiresIn = 0,
            expiresAt = 0
        )
    }

    suspend fun saveRemoteSession(
        userId: Long,
        username: String,
        rol: String,
        token: String?,
        tokenType: String,
        expiresIn: Long,
        expiresAt: Long
    ) {
        tokenInMemory = token
        context.sessionDataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = true
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USERNAME] = username
            prefs[KEY_USER_ROL] = rol
            if (token.isNullOrBlank()) {
                prefs.remove(KEY_TOKEN)
            } else {
                prefs[KEY_TOKEN] = token
            }
            prefs[KEY_TOKEN_TYPE] = tokenType
            prefs[KEY_EXPIRES_IN] = expiresIn
            prefs[KEY_EXPIRES_AT] = expiresAt
        }
    }

    suspend fun clearSession() {
        tokenInMemory = null
        context.sessionDataStore.edit { prefs -> prefs.clear() }
    }
}
