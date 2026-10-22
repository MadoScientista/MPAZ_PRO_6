package com.ccstudio.mpaz_pro_6.data.model.requests

import com.ccstudio.mpaz_pro_6.data.enums.ActivityType

/**
 * Cuerpo de creación de una actividad (`POST /api/actividades`).
 *
 * `unidadId`, `titulo` y `tipo` son obligatorios; `orden` es opcional y, si se
 * omite, el backend aplica `1`. El título es único dentro de la unidad y solo
 * se admite una `PRUEBA_FINAL` por unidad: ambos casos devuelven `409 Conflict`.
 */
data class CreateActivityRequest(
    val unidadId: Int,
    val titulo: String,
    val tipo: ActivityType,
    val orden: Int? = null
)
