package com.ccstudio.mpaz_pro_6.data.model

/**
 * Curso.
 *
 * Refleja 1:1 el `CursoResponse` del backend (`/api/cursos`): `id`, `nombre` (hasta
 * 50 caracteres) y `anio` (entre 2000 y 2100). El par nombre-año es único.
 */
data class Course(
    val id: Int,
    val nombre: String,
    val anio: Int
)
