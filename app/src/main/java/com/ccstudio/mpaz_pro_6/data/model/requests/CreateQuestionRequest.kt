package com.ccstudio.mpaz_pro_6.data.model.requests

import com.ccstudio.mpaz_pro_6.data.enums.Difficulty

/**
 * Cuerpo de creación de una pregunta (`POST /api/preguntas`).
 *
 * `actividadId`, `unidadId`, `oaId`, `enunciado` y `dificultad` son obligatorios;
 * `criterioRespuesta` y `orden` son opcionales. La actividad, la unidad y el
 * objetivo deben pertenecer a la misma unidad; si no, el backend devuelve
 * `400 Bad Request`.
 */
data class CreateQuestionRequest(
    val actividadId: Int,
    val unidadId: Int,
    val oaId: Int,
    val enunciado: String,
    val dificultad: Difficulty,
    val criterioRespuesta: String? = null,
    val orden: Int? = null
)
