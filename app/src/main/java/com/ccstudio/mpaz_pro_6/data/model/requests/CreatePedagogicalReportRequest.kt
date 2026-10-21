package com.ccstudio.mpaz_pro_6.data.model.requests

/**
 * Cuerpo de generación de un informe pedagógico
 * (`POST /api/informes-pedagogicos`).
 *
 * `estudianteId`, `unidadId` y `docenteId` son obligatorios. El backend calcula
 * la `fechaGeneracion`, deja la `fechaValidacion` nula, fija el estado inicial
 * `PENDIENTE_VALIDACION` y genera el detalle por objetivo de aprendizaje.
 * El par estudiante-unidad es único: un duplicado devuelve `409 Conflict`.
 */
data class CreatePedagogicalReportRequest(
    val estudianteId: Int,
    val unidadId: Int,
    val docenteId: Int
)
