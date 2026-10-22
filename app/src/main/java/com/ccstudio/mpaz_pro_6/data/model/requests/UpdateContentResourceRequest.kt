package com.ccstudio.mpaz_pro_6.data.model.requests

import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin
import com.ccstudio.mpaz_pro_6.data.enums.ContentResourceType

/**
 * Cuerpo de actualización de un recurso de contenido
 * (`PUT /api/recursos-contenido/{id}`).
 *
 * Se envía solo lo modificado: `null` significa "no enviar". El contenido
 * asociado se mantiene.
 */
data class UpdateContentResourceRequest(
    val tipo: ContentResourceType? = null,
    val url: String? = null,
    val descripcion: String? = null,
    val origen: ContentOrigin? = null
)
