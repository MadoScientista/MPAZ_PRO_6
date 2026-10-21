package com.ccstudio.mpaz_pro_6.data.model.requests

/**
 * Cuerpo de actualización de un curso (`PUT /api/cursos/{id}`).
 *
 * Se envía solo lo modificado: `null` significa "no enviar". El backend exige al
 * menos un campo informado.
 */
data class UpdateCourseRequest(
    val nombre: String? = null,
    val anio: Int? = null
)
