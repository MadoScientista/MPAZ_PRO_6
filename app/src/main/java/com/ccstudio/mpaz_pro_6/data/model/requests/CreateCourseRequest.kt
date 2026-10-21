package com.ccstudio.mpaz_pro_6.data.model.requests

/**
 * Cuerpo de creación de un curso (`POST /api/cursos`).
 *
 * El `nombre` es obligatorio (hasta 50 caracteres) y el `anio` es obligatorio y
 * debe estar entre 2000 y 2100. El par nombre-año es único: un duplicado
 * devuelve `409 Conflict`.
 */
data class CreateCourseRequest(
    val nombre: String,
    val anio: Int
)
