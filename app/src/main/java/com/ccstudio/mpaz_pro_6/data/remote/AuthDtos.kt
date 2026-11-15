package com.ccstudio.mpaz_pro_6.data.remote

import com.ccstudio.mpaz_pro_6.data.dto.identidad.UsuarioResponse
import kotlinx.serialization.Serializable

/** Credenciales de `POST /api/auth/login`. */
@Serializable
data class LoginRequestDto(
    val username: String,
    val password: String
)

/** Respuesta de `POST /api/auth/login`. */
@Serializable
data class LoginResponseDto(
    val token: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long = 0,
    val usuario: UsuarioResponse
)
