package com.ccstudio.mpaz_pro_6.data.model

import java.time.Instant

/**
 * Asignación de una unidad a un curso.
 *
 * Refleja 1:1 el `AsignacionResponse` del backend (`/api/asignaciones`): `id`,
 * `cursoId`, `unidadId`, `docenteId`, `maxIntentos`, `pruebaFinalHabilitada` y
 * `fecha`. El par curso-unidad es único y el docente debe tener rol `DOCENTE`.
 *
 * `maxIntentos` limita los intentos de la `PRUEBA_FINAL` y es `null` cuando la
 * asignación no impone ningún límite. `pruebaFinalHabilitada` decide si la prueba
 * final de la unidad está disponible para el curso. `fecha` es el instante en
 * ISO-8601 con zona horaria que declara el backend.
 */
data class Assignment(
    val id: Int,
    val cursoId: Int,
    val unidadId: Int,
    val docenteId: Int,
    val maxIntentos: Int?,
    val pruebaFinalHabilitada: Boolean,
    val fecha: Instant
)
