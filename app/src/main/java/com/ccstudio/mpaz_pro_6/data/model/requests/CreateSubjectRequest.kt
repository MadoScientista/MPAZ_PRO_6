package com.ccstudio.mpaz_pro_6.data.model.requests

/**
 * Cuerpo de creación de una asignatura (`POST /api/asignaturas`).
 *
 * El `nombre` es obligatorio y admite hasta 100 caracteres. Un nombre ya
 * registrado devuelve `409 Conflict` y eliminar una asignatura con unidades
 * asociadas también devuelve `409 Conflict`.
 */
data class CreateSubjectRequest(
    val nombre: String
)
