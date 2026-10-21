package com.ccstudio.mpaz_pro_6.data.model.requests

/**
 * Cuerpo de creación de una alternativa (`POST /api/alternativas`).
 *
 * `preguntaId` y `texto` son obligatorios; `esCorrecta` es opcional y, si se
 * omite, el backend aplica `false`. Solo se admite una alternativa correcta por
 * pregunta: marcar una segunda devuelve `409 Conflict`.
 */
data class CreateAlternativeRequest(
    val preguntaId: Int,
    val texto: String,
    val esCorrecta: Boolean? = null
)
