package com.ccstudio.mpaz_pro_6.data.model

import com.ccstudio.mpaz_pro_6.data.enums.ActivityType

/**
 * Actividad.
 *
 * Refleja 1:1 el `ActividadResponse` del backend (`/api/actividades`): `id`,
 * `unidadId`, `contenidoId` opcional, `titulo` (único dentro de la unidad),
 * `tipo` (`ActivityType`) y `orden`. Solo se admite una `PRUEBA_FINAL` por
 * unidad y no se puede asociar como actividad de una asignación.
 *
 * `contenidoId` nulo significa que la actividad es transversal a la unidad. Si
 * tiene valor, el contenido pertenece a la misma unidad que la actividad.
 *
 * El `tipo` viaja como `ActivityType`, que ya conoce el valor wire del backend
 * (`ACTIVIDAD`, `DESAFIO` y `PRUEBA_FINAL`).
 */
data class Activity(
    val id: Int,
    val unidadId: Int,
    val contenidoId: Int?,
    val titulo: String,
    val tipo: ActivityType,
    val orden: Int
)
