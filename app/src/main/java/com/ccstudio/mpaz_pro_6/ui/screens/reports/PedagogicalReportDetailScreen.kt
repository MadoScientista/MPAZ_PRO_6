package com.ccstudio.mpaz_pro_6.ui.screens.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.ReportStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import com.ccstudio.mpaz_pro_6.data.model.ReportObjectiveDetail
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.report.ReportObjectiveLevelCard
import com.ccstudio.mpaz_pro_6.ui.components.report.ReportStatusChip
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Detalle de un informe pedagógico.
 *
 * Consume `GET /api/informes-pedagogicos/{id}` y el detalle por objetivo de
 * aprendizaje. El nivel de cada objetivo llega calculado por el backend. La vista
 * del estudiante es de solo lectura.
 */
@Composable
fun PedagogicalReportDetailScreen(
    report: PedagogicalReport?,
    unitTitle: String?,
    details: List<ReportObjectiveDetail>,
    objectives: Map<Int, LearningObjective>,
    isLoading: Boolean,
    error: ApiError?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    canValidate: Boolean = false,
    isValidating: Boolean = false,
    mensaje: String? = null,
    onValidate: () -> Unit = {},
    onDismissMensaje: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()) {
        when {
            isLoading && report == null -> LoadingState(message = "Cargando el informe...")

            error != null -> ErrorState(error = error, onRetry = onRetry)

            report == null -> EmptyState(
                message = "No se encontró el informe.",
                modifier = Modifier.padding(16.dp)
            )

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ReportHeader(
                    report = report,
                    unitTitle = unitTitle ?: "Unidad #${report.unidadId}"
                )

                if (canValidate) {
                    if (report.estado != ReportStatus.VALIDATED) {
                        AppButton(
                            text = "Validar informe",
                            onClick = onValidate,
                            enabled = !isValidating
                        )
                    } else {
                        Text(
                            text = "Informe validado",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (mensaje != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mensaje,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        TextButton(onClick = onDismissMensaje) {
                            Text("Cerrar")
                        }
                    }
                }

                SectionHeader(
                    title = "Nivel por objetivo de aprendizaje",
                    count = details.size
                )

                if (details.isEmpty()) {
                    EmptyState(
                        message = "El informe no tiene detalle por objetivo."
                    )
                } else {
                    details.forEach { detail ->
                        val objective = objectives[detail.oaId]

                        ReportObjectiveLevelCard(
                            detail = detail,
                            objectiveCode = objective?.codigo,
                            objectiveDescription = objective?.descripcion
                        )
                    }
                }
            }
        }
    }
}

/** Encabezado con la unidad, el estado y las fechas del informe. */
@Composable
private fun ReportHeader(
    report: PedagogicalReport,
    unitTitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = unitTitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ReportStatusChip(status = report.estado)
            }

            Text(
                text = "Generado: ${formatInstant(report.fechaGeneracion)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (report.fechaValidacion != null) {
                Text(
                    text = "Validado: ${formatInstant(report.fechaValidacion)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** Formato local de las fechas del informe. */
private val REPORT_DETAIL_DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

private fun formatInstant(instant: Instant): String =
    REPORT_DETAIL_DATE_FORMATTER.withZone(ZoneId.systemDefault()).format(instant)
