package com.ccstudio.mpaz_pro_6.ui.screens.attempts

/**
 * Pantalla de resolución de una actividad.
 *
 * Consume `GET /api/actividades/{id}`, `GET /api/intentos?actividadId=` y
 * `GET /api/intentos/{id}/respuestas`. Debe iniciar el intento con
 * `POST /api/estudiantes/{id}/actividades/{id}/intentos`, registrar cada respuesta
 * con `POST /api/intentos/{id}/respuestas` y cerrarlo con
 * `PATCH /api/intentos/{id}/completar`.
 *
 * El `esCorrecta` que devuelve la respuesta viene congelado del backend.
 */
