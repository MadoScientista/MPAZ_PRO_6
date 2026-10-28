package com.ccstudio.mpaz_pro_6.data.model.requests

import java.time.Instant

/**
 * Cuerpo de creación de una asignación (`POST /api/asignaciones`).
 *
 * `cursoId`, `unidadId` y `fecha` son obligatorios. El docente se deriva de la
 * docencia autenticada para el curso y la asignatura de la unidad; no se envía
 * `docenteId`. `maxIntentos` y `pruebaFinalHabilitada` son opcionales: si se
 * omiten, el backend aplica `1` y `false`. El par curso-unidad es único: un
 * duplicado devuelve `409 Conflict`.
 */
data class CreateAssignmentRequest(
    val cursoId: Int,
    val unidadId: Int,
    val fecha: Instant,
    val maxIntentos: Int? = null,
    val pruebaFinalHabilitada: Boolean? = null
)
