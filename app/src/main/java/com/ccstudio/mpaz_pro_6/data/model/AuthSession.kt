package com.ccstudio.mpaz_pro_6.data.model

import java.time.Instant

/**
 * Sesión autenticada contra `/api/auth/login`.
 *
 * `expiresAt` se calcula en el dispositivo a partir de `expiresIn` y permite
 * descartar el token antes de llamar al backend.
 */
data class AuthSession(
    val token: String,
    val tokenType: String,
    val expiresIn: Long,
    val usuario: User,
    val expiresAt: Instant
)
