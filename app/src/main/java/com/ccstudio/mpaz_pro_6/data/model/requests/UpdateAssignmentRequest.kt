package com.ccstudio.mpaz_pro_6.data.model.requests

import java.time.Instant

/**
 * Cuerpo de actualización de una asignación (`PUT /api/asignaciones/{id}`).
 *
 * Se envía solo lo modificado: `null` significa "no enviar". El curso, la
 * unidad y el docente no se pueden cambiar.
 */
data class UpdateAssignmentRequest(
    val maxIntentos: Int? = null,
    val pruebaFinalHabilitada: Boolean? = null,
    val fecha: Instant? = null
)
