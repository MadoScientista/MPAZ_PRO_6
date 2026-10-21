package com.ccstudio.mpaz_pro_6.ui.screens.courses

/**
 * Listado de cursos que maneja un docente.
 *
 * Consume `GET /api/usuarios/{id}/cursos`, que devuelve los cursos distintos en los
 * que el docente tiene alguna asignación, ordenados por id. El endpoint responde
 * `400 Bad Request` si el usuario no tiene rol `DOCENTE`.
 */
