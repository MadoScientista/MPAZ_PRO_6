package com.ccstudio.mpaz_pro_6.data.session

import com.ccstudio.mpaz_pro_6.data.model.AuthSession
import com.ccstudio.mpaz_pro_6.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Estado de autenticación que consume la navegación. */
sealed interface SessionState {

    /** Se está leyendo y validando la sesión persistida. */
    data object Loading : SessionState

    /** Hay un usuario autenticado. */
    data class Authenticated(
        val user: User,
        val session: AuthSession? = null
    ) : SessionState

    /** No hay sesión válida. */
    data object Unauthenticated : SessionState
}

/**
 * Sesión local de la app.
 *
 * Conserva el usuario y el token en memoria para que la UI y el interceptor HTTP
 * los consulten sin bloquear. La persistencia real vive en `SessionStore`.
 */
class Session {

    private val _state = MutableStateFlow<SessionState>(SessionState.Loading)

    /** Estado de autenticación expuesto a la UI. */
    val state: StateFlow<SessionState> = _state.asStateFlow()

    /** Usuario autenticado, o `null` si no hay sesión. */
    val user: User?
        get() = (_state.value as? SessionState.Authenticated)?.user

    /** Token JWT actual, o `null` si no hay sesión. */
    val token: String?
        get() = (_state.value as? SessionState.Authenticated)?.session?.token

    /** Marca el inicio de la lectura de la sesión persistida. */
    fun loading() {
        _state.value = SessionState.Loading
    }

    /** Registra una sesión autenticada. */
    fun authenticate(session: AuthSession) {
        _state.value = SessionState.Authenticated(session.usuario, session)
    }

    /** Actualiza el usuario de una sesión ya autenticada. */
    fun updateUser(user: User) {
        val current = _state.value as? SessionState.Authenticated ?: return
        _state.value = current.copy(user = user)
    }

    /** Limpia la sesión en memoria. */
    fun unauthenticated() {
        _state.value = SessionState.Unauthenticated
    }
}
