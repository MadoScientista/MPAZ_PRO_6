package com.ccstudio.mpaz_pro_6.data.model.requests

import com.ccstudio.mpaz_pro_6.data.enums.UserRole

/**
 * Cuerpo de actualización de un usuario (`PUT /api/usuarios/{id}`).
 *
 * Se envía solo lo modificado: `null` significa "no enviar". Cambiar el `rol` a
 * uno distinto de `ESTUDIANTE` limpia el curso en el backend.
 */
data class UpdateUserRequest(
    val nombre: String? = null,
    val username: String? = null,
    val password: String? = null,
    val rol: UserRole? = null,
    val cursoId: Int? = null
)
