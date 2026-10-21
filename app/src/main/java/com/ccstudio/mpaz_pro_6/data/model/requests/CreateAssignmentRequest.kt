package com.ccstudio.mpaz_pro_6.data.model.requests

import java.time.Instant

/**
 * Cuerpo de creación de una asignación (`POST /api/asignaciones`).
 *
 * `cursoId`, `unidadId`, `docenteId` y `fecha` son obligatorios. `maxIntentos` y
 * `pruebaFinalHabilitada` son opcionales: si se omiten, el backend aplica `1` y
 * `false`. El par curso-unidad es único: un duplicado devuelve `409 Conflict`.
 */
data class CreateAssignmentRequest(
    val cursoId: Int,
    val unidadId: Int,
    val docenteId: Int,
    val fecha: Instant,
    val maxIntentos: Int? = null,
    val pruebaFinalHabilitada: Boolean? = null
)
