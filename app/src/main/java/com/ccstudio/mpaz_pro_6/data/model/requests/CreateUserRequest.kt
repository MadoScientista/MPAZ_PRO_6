package com.ccstudio.mpaz_pro_6.data.model.requests

import com.ccstudio.mpaz_pro_6.data.enums.UserRole

/**
 * Cuerpo de creación de un usuario (`POST /api/usuarios`).
 *
 * `nombre`, `username`, `password` y `rol` son obligatorios; `cursoId` es
 * opcional y solo se admite para estudiantes. El `username` admite letras,
 * dígitos, punto, guion y guion bajo (3 a 100 caracteres). La contraseña viaja
 * en texto plano por HTTPS y el backend la guarda como hash BCrypt: nunca se
 * devuelve en las respuestas.
 */
data class CreateUserRequest(
    val nombre: String,
    val username: String,
    val password: String,
    val rol: UserRole,
    val cursoId: Int? = null
)
