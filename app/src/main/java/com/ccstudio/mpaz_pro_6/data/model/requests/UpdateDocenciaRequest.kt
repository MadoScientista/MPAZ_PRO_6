package com.ccstudio.mpaz_pro_6.data.model.requests

/**
 * Cuerpo de actualización de una docencia (`PUT /api/docencias/{id}`).
 *
 * Solo se puede cambiar el docente responsable; el curso y la asignatura quedan
 * fijos para no romper las asignaciones existentes.
 */
data class UpdateDocenciaRequest(
    val docenteId: Int
)
