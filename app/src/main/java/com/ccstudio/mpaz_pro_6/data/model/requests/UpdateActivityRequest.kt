package com.ccstudio.mpaz_pro_6.data.model.requests

import com.ccstudio.mpaz_pro_6.data.enums.ActivityType

/**
 * Cuerpo de actualización de una actividad (`PUT /api/actividades/{id}`).
 *
 * Se envía solo lo modificado: `null` significa "no enviar". La unidad no se
 * puede cambiar y el backend exige al menos un campo informado.
 */
data class UpdateActivityRequest(
    val titulo: String? = null,
    val tipo: ActivityType? = null,
    val orden: Int? = null
)
