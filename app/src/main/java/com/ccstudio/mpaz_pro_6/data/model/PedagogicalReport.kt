package com.ccstudio.mpaz_pro_6.data.model

import com.ccstudio.mpaz_pro_6.data.enums.ReportStatus
import java.time.Instant

/**
 * Informe pedagógico de un estudiante en una unidad.
 *
 * Refleja 1:1 el `InformePedagogicoResponse` del backend
 * (`/api/informes-pedagogicos`): `id`, `estudianteId`, `unidadId`, `docenteId`,
 * `fechaGeneracion`, `fechaValidacion` y `estado` (`ReportStatus`). El par
 * estudiante-unidad es único: generar dos veces el mismo informe devuelve
 * `409 Conflict`.
 *
 * `fechaValidacion` es `null` mientras el informe no está validado.
 */
data class PedagogicalReport(
    val id: Int,
    val estudianteId: Int,
    val unidadId: Int,
    val docenteId: Int,
    val fechaGeneracion: Instant,
    val fechaValidacion: Instant?,
    val estado: ReportStatus
)
