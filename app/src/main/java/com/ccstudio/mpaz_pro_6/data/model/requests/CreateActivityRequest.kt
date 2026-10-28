package com.ccstudio.mpaz_pro_6.data.model.requests

import com.ccstudio.mpaz_pro_6.data.enums.ActivityType

/**
 * Cuerpo de creación de una actividad (`POST /api/actividades`).
 *
 * `unidadId`, `titulo` y `tipo` son obligatorios; `contenidoId` y `orden` son
 * opcionales. `contenidoId` nulo crea una actividad transversal; si se informa,
 * el contenido debe pertenecer a la misma unidad. `orden`, si se omite, toma
 * `1`. El título es único dentro de la unidad y solo se admite una
 * `PRUEBA_FINAL` por unidad: ambos casos devuelven `409 Conflict`.
 */
data class CreateActivityRequest(
    val unidadId: Int,
    val titulo: String,
    val tipo: ActivityType,
    val contenidoId: Int? = null,
    val orden: Int? = null
)
