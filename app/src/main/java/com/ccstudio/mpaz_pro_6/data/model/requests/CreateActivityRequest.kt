package com.ccstudio.mpaz_pro_6.data.model.requests

import com.ccstudio.mpaz_pro_6.data.enums.ActivityType

/**
 * Cuerpo de creación de una actividad (`POST /api/actividades`).
 *
 * `unidadId`, `titulo` y `tipo` son obligatorios; `contenidoId` y `orden` son
 * opcionales. Si se omite `orden`, el backend aplica `1`. `contenidoId` nulo u
 * omitido crea una actividad transversal de la unidad. El título es único
 * dentro de la unidad y solo se admite una `PRUEBA_FINAL` por unidad: ambos
 * casos devuelven `409 Conflict`.
 */
data class CreateActivityRequest(
    val unidadId: Int,
    val titulo: String,
    val tipo: ActivityType,
    val contenidoId: Int? = null,
    val orden: Int? = null
)
