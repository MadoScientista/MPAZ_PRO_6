package com.ccstudio.mpaz_pro_6.data.model

/**
 * Respuesta de un estudiante a una pregunta.
 *
 * Refleja 1:1 el `RespuestaEstudianteResponse` del backend
 * (`/api/intentos/{id}/respuestas`): `id`, `intentoId`, `preguntaId`, `alternativaId`
 * y `esCorrecta`. El campo `esCorrecta` llega congelado desde el backend: la app no
 * lo recalcula.
 */
data class StudentAnswer(
    val id: Int,
    val intentoId: Int,
    val preguntaId: Int,
    val alternativaId: Int,
    val esCorrecta: Boolean
)
