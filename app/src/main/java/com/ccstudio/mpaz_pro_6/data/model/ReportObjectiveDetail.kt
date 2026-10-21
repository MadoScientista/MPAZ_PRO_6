package com.ccstudio.mpaz_pro_6.data.model

import com.ccstudio.mpaz_pro_6.data.enums.ObjectiveLevel

/**
 * Detalle por objetivo de aprendizaje de un informe pedagógico.
 *
 * Refleja 1:1 el `DetalleInformeOaResponse` del backend
 * (`/api/informes-pedagogicos/{id}/objetivos-aprendizaje`): `oaId`, `total`,
 * `correctas` y `nivel` (`ObjectiveLevel`). El nivel es `FORTALEZA` si
 * `correctas * 100.0 / total >= 70` y `REFUERZO` en caso contrario.
 */
data class ReportObjectiveDetail(
    val oaId: Int,
    val total: Int,
    val correctas: Int,
    val nivel: ObjectiveLevel
)
