package com.ccstudio.mpaz_pro_6.data.model

import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus

/**
 * Contenido.
 *
 * Refleja 1:1 el `ContenidoResponse` del backend (`/api/contenidos`): `id`, `oaId`,
 * `titulo`, `explicacion`, `ejemplos`, `instrucciones`, `orden`, `origen`
 * (`ContentOrigin`) y `estado` (`ContentStatus`). Al eliminar un contenido se
 * borran en cascada sus recursos de contenido.
 *
 * `ejemplos` e `instrucciones` son opcionales y admiten `null`.
 */
data class Content(
    val id: Int,
    val oaId: Int,
    val titulo: String,
    val explicacion: String,
    val ejemplos: String?,
    val instrucciones: String?,
    val orden: Int,
    val origen: ContentOrigin,
    val estado: ContentStatus
)
