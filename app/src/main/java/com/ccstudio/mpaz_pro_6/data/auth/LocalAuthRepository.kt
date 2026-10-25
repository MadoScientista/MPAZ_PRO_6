package com.ccstudio.mpaz_pro_6.data.auth

import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.local.dao.UserDao
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.model.AuthSession
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.repository.AuthRepository
import com.ccstudio.mpaz_pro_6.data.session.Session
import com.ccstudio.mpaz_pro_6.data.session.SessionStore
import java.time.Instant

/**
 * Autenticación local para desarrollo sin backend.
 *
 * Valida el `username` contra la tabla `usuario` y usa una contraseña de
 * desarrollo compartida. El token no sale del dispositivo y no hay llamadas de
 * red. Solo se debe inyectar cuando `MPAZ_LOCAL_ONLY` está activo.
 */
class LocalAuthRepository(
    private val userDao: UserDao,
    private val sessionStore: SessionStore,
    private val session: Session
) : AuthRepository {

    override suspend fun login(username: String, password: String): Result<User> {
        val entity = userDao.getUserByUsername(username.trim())
            ?: return invalidCredentials()
        if (password != LOCAL_PASSWORD) {
            return invalidCredentials()
        }

        val user = entity.toDomain()
        val authSession = AuthSession(
            token = "local-${user.id}",
            tokenType = "Local",
            expiresIn = 0,
            usuario = user,
            expiresAt = Instant.ofEpochMilli(Long.MAX_VALUE / 4)
        )
        sessionStore.save(authSession)
        session.authenticate(authSession)
        return Result.success(user)
    }

    override suspend fun bootstrap(): Result<User> {
        val stored = sessionStore.load()
        if (stored == null) {
            session.unauthenticated()
            return Result.failure(ApiError.Unauthorized("No hay una sesión local guardada."))
        }

        val entity = userDao.getUserById(stored.usuario.id)
        if (entity == null) {
            sessionStore.clear()
            session.unauthenticated()
            return Result.failure(ApiError.Unauthorized("El usuario local ya no existe."))
        }

        val user = entity.toDomain()
        val refreshed = stored.copy(usuario = user, expiresAt = Instant.ofEpochMilli(Long.MAX_VALUE / 4))
        sessionStore.save(refreshed)
        session.authenticate(refreshed)
        return Result.success(user)
    }

    override suspend fun logout(): Result<Unit> {
        clearLocalSession()
        return Result.success(Unit)
    }

    override suspend fun clearLocalSession() {
        sessionStore.clear()
        session.unauthenticated()
    }

    private fun invalidCredentials(): Result<User> =
        Result.failure(ApiError.Unauthorized("Usuario o contraseña incorrectos."))

    companion object {
        /** Contraseña común del modo local; visible solo en builds de desarrollo. */
        const val LOCAL_PASSWORD = "mpaz123"
    }
}
