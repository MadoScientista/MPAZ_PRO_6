package com.ccstudio.mpaz_pro_6.data.model

/**
 * Docencia de un docente en un curso y una asignatura.
 *
 * Refleja 1:1 el `DocenciaResponse` del backend (`/api/docencias`): `id`,
 * `docenteId`, `docenteNombre`, `cursoId`, `cursoNombre`, `asignaturaId` y
 * `asignaturaNombre`. En un mismo curso y asignatura solo puede existir una
 * docencia; esa regla la valida el backend y también la caché local.
 */
data class Docencia(
    val id: Int,
    val docenteId: Int,
    val docenteNombre: String,
    val cursoId: Int,
    val cursoNombre: String,
    val asignaturaId: Int,
    val asignaturaNombre: String
)
