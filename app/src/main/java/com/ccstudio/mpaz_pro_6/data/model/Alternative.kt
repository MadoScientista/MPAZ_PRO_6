package com.ccstudio.mpaz_pro_6.data.model

/**
 * Alternativa de respuesta.
 *
 * Refleja 1:1 el `AlternativaResponse` del backend (`/api/alternativas`): `id`,
 * `preguntaId`, `texto` y `esCorrecta`. Solo se admite una alternativa correcta
 * por pregunta, por lo que el editor debe usar selección exclusiva.
 */
data class Alternative(
    val id: Int,
    val preguntaId: Int,
    val texto: String,
    val esCorrecta: Boolean
)
