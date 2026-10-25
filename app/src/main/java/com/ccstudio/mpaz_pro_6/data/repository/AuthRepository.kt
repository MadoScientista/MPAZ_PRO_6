package com.ccstudio.mpaz_pro_6.data.repository

import com.ccstudio.mpaz_pro_6.data.model.User

/** Contrato de autenticación contra `/api/auth`. */
interface AuthRepository {

    /** Inicia sesión y persiste el token. */
    suspend fun login(username: String, password: String): Result<User>

    /** Valida la sesión guardada y refresca el usuario desde `/api/auth/me`. */
    suspend fun bootstrap(): Result<User>

    /** Cierra la sesión local y notifica al backend sin bloquear el cierre. */
    suspend fun logout(): Result<Unit>

    /** Limpia la sesión local sin llamar al backend. */
    suspend fun clearLocalSession()
}
