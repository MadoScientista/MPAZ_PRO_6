package com.ccstudio.mpaz_pro_6.data.model.requests

import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin

/**
 * Cuerpo de creación de un contenido (`POST /api/contenidos`).
 *
 * `oaId`, `titulo`, `explicacion` y `origen` son obligatorios; `ejemplos`,
 * `instrucciones` y `orden` son opcionales. Si se omite el orden, el backend
 * aplica `1`.
 */
data class CreateContentRequest(
    val oaId: Int,
    val titulo: String,
    val explicacion: String,
    val origen: ContentOrigin,
    val ejemplos: String? = null,
    val instrucciones: String? = null,
    val orden: Int? = null
)
