package com.ccstudio.mpaz_pro_6.data.model

import java.time.Instant

/**
 * Intento de un estudiante sobre una actividad.
 *
 * Refleja 1:1 el `IntentoResponse` del backend (`/api/intentos`): `id`,
 * `estudianteId`, `actividadId`, `numero`, `fechaInicio` y `fechaFin`. El estado de
 * realización se deduce de `fechaFin`: nula significa intento en progreso.
 */
data class Attempt(
    val id: Int,
    val estudianteId: Int,
    val actividadId: Int,
    val numero: Int,
    val fechaInicio: Instant,
    val fechaFin: Instant?
)
