package com.ccstudio.mpaz_pro_6.data.model.requests

import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin

/**
 * Cuerpo de actualización de un contenido (`PUT /api/contenidos/{id}`).
 *
 * Se envía solo lo modificado: `null` significa "no enviar". El objetivo
 * asociado se mantiene y el backend exige al menos un campo informado.
 */
data class UpdateContentRequest(
    val titulo: String? = null,
    val explicacion: String? = null,
    val ejemplos: String? = null,
    val instrucciones: String? = null,
    val orden: Int? = null,
    val origen: ContentOrigin? = null
)
