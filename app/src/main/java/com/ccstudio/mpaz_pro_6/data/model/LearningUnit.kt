package com.ccstudio.mpaz_pro_6.data.model

import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import java.time.Instant

/**
 * Unidad de aprendizaje.
 *
 * Refleja 1:1 el `UnidadResponse` del backend (`/api/unidades`): `id`,
 * `asignaturaId`, `nivelEducativo`, `titulo`, `orden`, `estado` (`ContentStatus`),
 * `autorizadaPorId` y `fechaAutorizacion`.
 *
 * Se llama `LearningUnit` y no `Unit` a propósito: una clase llamada `Unit` en este
 * paquete sombrearía `kotlin.Unit` y rompería los tipos de retorno `-> Unit` de las
 * screens y los componentes que la importan.
 *
 * `autorizadaPorId` y `fechaAutorizacion` son `null` mientras la unidad no esté
 * autorizada ni publicada.
 */
data class LearningUnit(
    val id: Int,
    val asignaturaId: Int,
    val nivelEducativo: String,
    val titulo: String,
    val orden: Int,
    val estado: ContentStatus,
    val autorizadaPorId: Int?,
    val fechaAutorizacion: Instant?
)
