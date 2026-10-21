package com.ccstudio.mpaz_pro_6.data.model.requests

/**
 * Cuerpo de actualización de una asignatura (`PUT /api/asignaturas/{id}`).
 *
 * El `nombre` sigue siendo obligatorio en la actualización y admite hasta 100
 * caracteres. El backend solo aplica los valores enviados.
 */
data class UpdateSubjectRequest(
    val nombre: String
)
