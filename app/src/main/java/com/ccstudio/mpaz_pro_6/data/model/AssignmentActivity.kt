package com.ccstudio.mpaz_pro_6.data.model

/**
 * Asociación entre una asignación y una actividad.
 *
 * Refleja 1:1 el `AsignacionActividadResponse` del backend
 * (`/api/asignaciones/{id}/actividades`): `asignacionId`, `actividadId` y
 * `unidadId`. La actividad debe pertenecer a la unidad de la asignación y no
 * puede ser una `PRUEBA_FINAL`.
 */
data class AssignmentActivity(
    val asignacionId: Int,
    val actividadId: Int,
    val unidadId: Int
)
