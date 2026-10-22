package com.ccstudio.mpaz_pro_6.data.model

/**
 * Objetivo de aprendizaje.
 *
 * Refleja 1:1 el `ObjetivoAprendizajeResponse` del backend
 * (`/api/objetivos-aprendizaje`): `id`, `unidadId`, `codigo` (único dentro de la
 * unidad), `descripcion`, `eje` y `textoReferencia`. El código y el texto de
 * referencia son opcionales, por eso admits `null`.
 */
data class LearningObjective(
    val id: Int,
    val unidadId: Int,
    val codigo: String?,
    val descripcion: String,
    val eje: String,
    val textoReferencia: String?
)
