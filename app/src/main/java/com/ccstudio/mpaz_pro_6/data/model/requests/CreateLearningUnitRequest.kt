package com.ccstudio.mpaz_pro_6.data.model.requests

import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus

/**
 * Cuerpo de creación de una unidad de aprendizaje (`POST /api/unidades`).
 *
 * `asignaturaId`, `titulo` y `estado` son obligatorios. `nivelEducativo` y
 * `orden` son opcionales: si se omiten, el backend aplica `"Sexto Basico"` y
 * `1`. La creación solo admite los estados iniciales.
 */
data class CreateLearningUnitRequest(
    val asignaturaId: Int,
    val titulo: String,
    val estado: ContentStatus,
    val nivelEducativo: String? = null,
    val orden: Int? = null
)
