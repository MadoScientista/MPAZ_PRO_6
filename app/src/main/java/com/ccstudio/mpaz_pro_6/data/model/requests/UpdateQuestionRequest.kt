package com.ccstudio.mpaz_pro_6.data.model.requests

import com.ccstudio.mpaz_pro_6.data.enums.Difficulty

/**
 * Cuerpo de actualización de una pregunta (`PUT /api/preguntas/{id}`).
 *
 * Se envía solo lo modificado: `null` significa "no enviar". La actividad, la
 * unidad y el objetivo asociados se mantienen.
 */
data class UpdateQuestionRequest(
    val enunciado: String? = null,
    val criterioRespuesta: String? = null,
    val dificultad: Difficulty? = null,
    val orden: Int? = null
)
