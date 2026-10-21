package com.ccstudio.mpaz_pro_6.data.model.requests

import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin
import com.ccstudio.mpaz_pro_6.data.enums.ContentResourceType

/**
 * Cuerpo de creación de un recurso de contenido
 * (`POST /api/recursos-contenido`).
 *
 * `contenidoId`, `tipo`, `url` y `origen` son obligatorios; `descripcion` es
 * opcional. La URL y la descripción admiten hasta 500 caracteres.
 */
data class CreateContentResourceRequest(
    val contenidoId: Int,
    val tipo: ContentResourceType,
    val url: String,
    val origen: ContentOrigin,
    val descripcion: String? = null
)
