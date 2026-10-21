package com.ccstudio.mpaz_pro_6.data.model

import com.ccstudio.mpaz_pro_6.data.enums.UserRole

/**
 * Usuario del sistema.
 *
 * Refleja 1:1 el `UsuarioResponse` del backend (`/api/usuarios`): `id`, `nombre`,
 * `username`, `rol` (`UserRole`) y `cursoId` (solo para estudiantes). El
 * `passwordHash` nunca se expone en las respuestas y la contraseña viaja en claro
 * únicamente en el formulario de login, por eso no forma parte del modelo.
 *
 * `cursoId` es `null` para los usuarios `UTP` y `DOCENTE`.
 */
data class User(
    val id: Int,
    val nombre: String,
    val username: String,
    val rol: UserRole,
    val cursoId: Int?
)
