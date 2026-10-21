package com.ccstudio.mpaz_pro_6.data.model.requests

/**
 * Cuerpo de creación de un objetivo de aprendizaje
 * (`POST /api/objetivos-aprendizaje`).
 *
 * `unidadId`, `descripcion` y `eje` son obligatorios; `codigo` y `textoReferencia`
 * son opcionales. El código, cuando se informa, es único dentro de la unidad: un
 * duplicado devuelve `409 Conflict`.
 */
data class CreateLearningObjectiveRequest(
    val unidadId: Int,
    val descripcion: String,
    val eje: String,
    val codigo: String? = null,
    val textoReferencia: String? = null
)
