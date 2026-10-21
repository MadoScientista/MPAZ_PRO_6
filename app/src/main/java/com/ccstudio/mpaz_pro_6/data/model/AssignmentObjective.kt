package com.ccstudio.mpaz_pro_6.data.model

/**
 * Asociación entre una asignación y un objetivo de aprendizaje.
 *
 * Refleja 1:1 el `AsignacionOaResponse` del backend
 * (`/api/asignaciones/{id}/objetivos-aprendizaje`): `asignacionId`, `oaId` y
 * `unidadId`. El objetivo debe pertenecer a la unidad de la asignación.
 */
data class AssignmentObjective(
    val asignacionId: Int,
    val oaId: Int,
    val unidadId: Int
)
