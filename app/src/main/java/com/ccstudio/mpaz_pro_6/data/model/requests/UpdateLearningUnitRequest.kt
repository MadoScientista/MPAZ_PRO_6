package com.ccstudio.mpaz_pro_6.data.model.requests

import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import java.time.Instant

/**
 * Cuerpo de actualización de una unidad de aprendizaje (`PUT /api/unidades/{id}`).
 *
 * Se envía solo lo modificado: `null` significa "no enviar". La asignatura no se
 * puede cambiar. Para `AUTHORIZED` o `PUBLISHED` el backend exige un
 * `autorizadaPorId` de un usuario con rol `UTP` y una `fechaAutorizacion`; si
 * falta alguno devuelve `400 Bad Request`.
 */
data class UpdateLearningUnitRequest(
    val nivelEducativo: String? = null,
    val titulo: String? = null,
    val orden: Int? = null,
    val estado: ContentStatus? = null,
    val autorizadaPorId: Int? = null,
    val fechaAutorizacion: Instant? = null
)
