package com.ccstudio.mpaz_pro_6.data.model

import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin
import com.ccstudio.mpaz_pro_6.data.enums.ContentResourceType
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus

/**
 * Recurso de contenido.
 *
 * Refleja 1:1 el `RecursoContenidoResponse` del backend
 * (`/api/recursos-contenido`): `id`, `contenidoId`, `tipo` (`ContentResourceType`),
 * `url`, `descripcion`, `origen` (`ContentOrigin`) y `estado` (`ContentStatus`).
 *
 * `descripcion` es opcional y admite `null`.
 */
data class ContentResource(
    val id: Int,
    val contenidoId: Int,
    val tipo: ContentResourceType,
    val url: String,
    val descripcion: String?,
    val origen: ContentOrigin,
    val estado: ContentStatus
)
