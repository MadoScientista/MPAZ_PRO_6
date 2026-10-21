package com.ccstudio.mpaz_pro_6.data.model

/**
 * Asignatura.
 *
 * Refleja 1:1 el `AsignaturaResponse` del backend (`/api/asignaturas`): `id` y
 * `nombre`, que es obligatorio, admite hasta 100 caracteres y es único (un nombre
 * duplicado devuelve `409 Conflict`).
 */
data class Subject(
    val id: Int,
    val nombre: String
)
