package com.ccstudio.mpaz_pro_6.data.remote

import com.ccstudio.mpaz_pro_6.data.dto.identidad.UsuarioResponse
import com.ccstudio.mpaz_pro_6.data.local.preferences.SessionManager
import com.ccstudio.mpaz_pro_6.data.local.preferences.UserSession
import retrofit2.HttpException
import java.io.IOException

data class RemoteLoginResult(
    val session: UserSession,
    val usuario: UsuarioResponse
)

/**
 * Autenticacion contra el backend con persistencia del JWT.
 */
class RemoteAuthRepository(
    private val api: MpazApi,
    private val sessionManager: SessionManager
) {

    suspend fun login(username: String, password: String): Result<RemoteLoginResult> =
        runCatching {
            val response = api.login(
                LoginRequestDto(username.trim(), password)
            )
            val usuario = response.usuario
            val ahora = System.currentTimeMillis()
            val expiraEn = response.expiresIn.coerceAtLeast(60)
            val session = UserSession(
                userId = usuario.id,
                username = usuario.nombreUsuario,
                rol = usuario.rol.nombre,
                token = response.token,
                tokenType = response.tokenType,
                expiresIn = response.expiresIn,
                expiresAt = ahora + expiraEn * 1000
            )
            sessionManager.saveRemoteSession(
                userId = session.userId,
                username = session.username,
                rol = session.rol,
                token = session.token,
                tokenType = session.tokenType,
                expiresIn = session.expiresIn,
                expiresAt = session.expiresAt
            )
            RemoteLoginResult(session, usuario)
        }.mapError()

    /**
     * Restaura la sesion guardada y valida el token contra `/api/auth/me`.
     */
    suspend fun bootstrap(): Result<RemoteLoginResult> = runCatching {
        val stored = sessionManager.loadSession()
            ?: throw UnauthorizedException("No hay una sesion guardada")
        if (stored.token.isNullOrBlank()) {
            throw UnauthorizedException("La sesion no tiene token")
        }
        if (stored.expiresAt > 0 && stored.expiresAt <= System.currentTimeMillis()) {
            sessionManager.clearSession()
            throw UnauthorizedException("La sesion guardada expiro")
        }
        sessionManager.restoreToken()
        val usuario = api.me()
        val session = stored.copy(
            userId = usuario.id,
            username = usuario.nombreUsuario,
            rol = usuario.rol.nombre
        )
        sessionManager.saveRemoteSession(
            userId = session.userId,
            username = session.username,
            rol = session.rol,
            token = session.token,
            tokenType = session.tokenType,
            expiresIn = session.expiresIn,
            expiresAt = session.expiresAt
        )
        RemoteLoginResult(session, usuario)
    }.mapError()

    suspend fun logout(): Result<Unit> = runCatching {
        runCatching { api.logout() }
        sessionManager.clearSession()
        Unit
    }

    private fun <T> Result<T>.mapError(): Result<T> = fold(
        onSuccess = { Result.success(it) },
        onFailure = { error -> Result.failure(error.toFriendlyError()) }
    )

    private fun Throwable.toFriendlyError(): Exception = when (this) {
        is UnauthorizedException -> this
        is HttpException -> when (code()) {
            401 -> UnauthorizedException("Usuario o contraseña incorrectos")
            403 -> Exception("No tienes permisos para realizar esta acción")
            else -> Exception("Error del servidor (${code()})")
        }
        is IOException -> Exception("No se pudo conectar con el servidor")
        else -> Exception(message ?: "Ocurrió un error inesperado")
    }

    private class UnauthorizedException(message: String) : Exception(message)
}
