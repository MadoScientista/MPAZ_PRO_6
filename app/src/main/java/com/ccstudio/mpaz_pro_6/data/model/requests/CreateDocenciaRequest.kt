package com.ccstudio.mpaz_pro_6.data.model.requests

/**
 * Cuerpo de creación de una docencia (`POST /api/docencias`).
 *
 * `docenteId`, `cursoId` y `asignaturaId` son obligatorios. El backend exige que
 * el usuario tenga rol `DOCENTE` y que no exista otra docencia para el mismo
 * curso y asignatura; un duplicado devuelve `409 Conflict`.
 */
data class CreateDocenciaRequest(
    val docenteId: Int,
    val cursoId: Int,
    val asignaturaId: Int
)
