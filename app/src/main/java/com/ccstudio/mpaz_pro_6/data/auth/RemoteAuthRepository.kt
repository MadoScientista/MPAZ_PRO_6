package com.ccstudio.mpaz_pro_6.data.auth

import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.error.requireSuccess
import com.ccstudio.mpaz_pro_6.data.model.AuthSession
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.dto.LoginRequestDto
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.repository.AuthRepository
import com.ccstudio.mpaz_pro_6.data.session.Session
import com.ccstudio.mpaz_pro_6.data.session.SessionStore
import java.time.Instant

/**
 * Autenticación remota con persistencia local del token.
 *
 * Si `/me` falla por red se conserva la sesión para permitir el modo offline;
 * un `401` limpia el token y devuelve la app al login.
 */
class RemoteAuthRepository(
    private val api: MpazApi,
    private val sessionStore: SessionStore,
    private val session: Session
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<User> {
        val result = apiCall {
            api.login(
                LoginRequestDto(
                    username = username.trim(),
                    password = password
                )
            )
        }

        return result.fold(
            onSuccess = { response ->
                val sessionData = AuthSession(
                    token = response.token,
                    tokenType = response.tokenType,
                    expiresIn = response.expiresIn,
                    usuario = response.usuario.toDomain(),
                    expiresAt = Instant.now().plusSeconds(response.expiresIn.coerceAtLeast(60))
                )

                sessionStore.save(sessionData)
                session.authenticate(sessionData)

                Result.success(sessionData.usuario)
            },
            onFailure = { Result.failure(it) }
        )
    }

    override suspend fun bootstrap(): Result<User> {
        session.loading()

        val stored = sessionStore.load()

        if (stored == null) {
            session.unauthenticated()
            return Result.failure(ApiError.Unauthorized("No hay una sesión guardada."))
        }

        if (!stored.expiresAt.isAfter(Instant.now())) {
            clearLocalSession()
            return Result.failure(ApiError.Unauthorized("La sesión guardada expiró."))
        }

        session.authenticate(stored)

        return apiCall { api.me().toDomain() }.fold(
            onSuccess = { user ->
                val refreshed = stored.copy(usuario = user)
                sessionStore.save(refreshed)
                session.authenticate(refreshed)
                Result.success(user)
            },
            onFailure = { error ->
                if (error is ApiError.Unauthorized) {
                    clearLocalSession()
                } else {
                    session.authenticate(stored)
                }

                Result.failure(error)
            }
        )
    }

    override suspend fun logout(): Result<Unit> {
        apiCall { api.logout().requireSuccess() }
        clearLocalSession()
        return Result.success(Unit)
    }

    override suspend fun clearLocalSession() {
        sessionStore.clear()
        session.unauthenticated()
    }
}
