package com.ccstudio.mpaz_pro_6.data.model.requests

/**
 * Cuerpo de actualización de un objetivo de aprendizaje
 * (`PUT /api/objetivos-aprendizaje/{id}`).
 *
 * Se envía solo lo modificado: `null` significa "no enviar". La unidad no se
 * puede cambiar y el backend exige al menos un campo informado.
 */
data class UpdateLearningObjectiveRequest(
    val codigo: String? = null,
    val descripcion: String? = null,
    val eje: String? = null,
    val textoReferencia: String? = null
)
