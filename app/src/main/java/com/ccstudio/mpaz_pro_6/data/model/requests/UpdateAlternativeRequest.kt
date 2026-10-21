package com.ccstudio.mpaz_pro_6.data.model.requests

/**
 * Cuerpo de actualización de una alternativa (`PUT /api/alternativas/{id}`).
 *
 * Se envía solo lo modificado: `null` significa "no enviar". El backend aplica
 * solo los valores no nulos, de modo que `esCorrecta = false` sí se envía.
 */
data class UpdateAlternativeRequest(
    val texto: String? = null,
    val esCorrecta: Boolean? = null
)
