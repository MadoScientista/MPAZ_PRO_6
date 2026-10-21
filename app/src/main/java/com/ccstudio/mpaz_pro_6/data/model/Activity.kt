package com.ccstudio.mpaz_pro_6.data.model

import com.ccstudio.mpaz_pro_6.data.enums.ActivityType

/**
 * Actividad.
 *
 * Refleja 1:1 el `ActividadResponse` del backend (`/api/actividades`): `id`,
 * `unidadId`, `titulo` (único dentro de la unidad), `tipo` (`ActivityType`) y
 * `orden`. Solo se admite una `PRUEBA_FINAL` por unidad y no se puede asociar
 * como actividad de una asignación.
 *
 * El `tipo` viaja como `ActivityType`, que ya conoce el valor wire del backend
 * (`ACTIVIDAD`, `DESAFIO` y `PRUEBA_FINAL`).
 */
data class Activity(
    val id: Int,
    val unidadId: Int,
    val titulo: String,
    val tipo: ActivityType,
    val orden: Int
)
