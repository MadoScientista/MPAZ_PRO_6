package com.ccstudio.mpaz_pro_6.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.model.AuthSession
import com.ccstudio.mpaz_pro_6.data.model.User
import kotlinx.coroutines.flow.first
import java.time.Instant

private val Context.sessionDataStore by preferencesDataStore(name = "mpaz_session")

/**
 * Persistencia de la sesión en DataStore Preferences.
 *
 * El token se mantiene además en memoria para que el interceptor de OkHttp lo
 * lea de forma síncrona.
 */
class SessionStore(private val context: Context) {

    @Volatile
    private var tokenInMemory: String? = null

    /** Token actual, sin suspender. */
    fun currentToken(): String? = tokenInMemory

    /** Lee la sesión guardada, o `null` si no existe o está incompleta. */
    suspend fun load(): AuthSession? {
        val preferences = context.sessionDataStore.data.first()
        val token = preferences[Keys.TOKEN] ?: return null
        val expiration = preferences[Keys.EXPIRES_AT] ?: return null
        val userId = preferences[Keys.USER_ID] ?: return null
        val roleValue = preferences[Keys.ROLE] ?: return null
        val role = UserRole.fromWire(roleValue) ?: return null

        tokenInMemory = token

        return AuthSession(
            token = token,
            tokenType = preferences[Keys.TOKEN_TYPE] ?: "Bearer",
            expiresIn = preferences[Keys.EXPIRES_IN] ?: 0L,
            usuario = User(
                id = userId,
                nombre = preferences[Keys.USER_NAME] ?: "",
                username = preferences[Keys.USERNAME] ?: "",
                rol = role,
                cursoId = preferences[Keys.COURSE_ID]
            ),
            expiresAt = Instant.ofEpochMilli(expiration)
        )
    }

    /** Guarda una sesión completa. */
    suspend fun save(session: AuthSession) {
        tokenInMemory = session.token

        context.sessionDataStore.edit { preferences ->
            preferences[Keys.TOKEN] = session.token
            preferences[Keys.TOKEN_TYPE] = session.tokenType
            preferences[Keys.EXPIRES_IN] = session.expiresIn
            preferences[Keys.EXPIRES_AT] = session.expiresAt.toEpochMilli()
            preferences[Keys.USER_ID] = session.usuario.id
            preferences[Keys.USER_NAME] = session.usuario.nombre
            preferences[Keys.USERNAME] = session.usuario.username
            preferences[Keys.ROLE] = session.usuario.rol.wireValue
            session.usuario.cursoId?.let { preferences[Keys.COURSE_ID] = it }
                ?: preferences.remove(Keys.COURSE_ID)
        }
    }

    /** Elimina la sesión persistida. */
    suspend fun clear() {
        tokenInMemory = null
        context.sessionDataStore.edit { preferences -> preferences.clear() }
    }

    private object Keys {
        val TOKEN = stringPreferencesKey("token")
        val TOKEN_TYPE = stringPreferencesKey("token_type")
        val EXPIRES_IN = longPreferencesKey("expires_in")
        val EXPIRES_AT = longPreferencesKey("expires_at")
        val USER_ID = intPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USERNAME = stringPreferencesKey("username")
        val ROLE = stringPreferencesKey("role")
        val COURSE_ID = intPreferencesKey("course_id")
    }
}
