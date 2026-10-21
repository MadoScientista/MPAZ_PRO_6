package com.ccstudio.mpaz_pro_6.data.model.requests

/**
 * Cuerpo de registro de una respuesta de estudiante
 * (`POST /api/intentos/{intentoId}/respuestas`).
 *
 * `preguntaId` y `alternativaId` son obligatorios; el `intentoId` proviene de la
 * ruta. El backend deriva `esCorrecta` de la alternativa seleccionada, así que
 * no se envía. El par intento-pregunta es único: responder dos veces a la misma
 * pregunta devuelve `409 Conflict`.
 */
data class CreateStudentAnswerRequest(
    val preguntaId: Int,
    val alternativaId: Int
)
