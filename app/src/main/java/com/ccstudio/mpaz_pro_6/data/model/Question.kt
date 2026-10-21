package com.ccstudio.mpaz_pro_6.data.model

import com.ccstudio.mpaz_pro_6.data.enums.Difficulty

/**
 * Pregunta.
 *
 * Refleja 1:1 el `PreguntaResponse` del backend (`/api/preguntas`): `id`,
 * `actividadId`, `unidadId`, `oaId`, `enunciado`, `criterioRespuesta`, `dificultad`
 * (`Difficulty`) y `orden`. El criterio de respuesta es opcional y admite `null`.
 */
data class Question(
    val id: Int,
    val actividadId: Int,
    val unidadId: Int,
    val oaId: Int,
    val enunciado: String,
    val criterioRespuesta: String?,
    val dificultad: Difficulty,
    val orden: Int
)
