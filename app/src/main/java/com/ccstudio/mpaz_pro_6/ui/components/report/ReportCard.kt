package com.ccstudio.mpaz_pro_6.ui.components.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Tarjeta de un informe pedagógico.
 *
 * Muestra el estudiante, la unidad, la fecha de generación y el estado de
 * validación. El informe solo trae los identificadores, así que los nombres los
 * resuelve la pantalla que lista y los entrega como `studentName` y `unitName`.
 */
@Composable
fun ReportCard(
    report: PedagogicalReport,
    studentName: String,
    unitName: String,
    onViewDetail: () -> Unit,
    modifier: Modifier = Modifier,
    showActions: Boolean = true,
    showStudentName: Boolean = true
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Estudiante e informe. Para el estudiante, la unidad es el título.
            Text(
                text = if (showStudentName) studentName else unitName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            if (showStudentName) {
                Text(
                    text = unitName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Estado y fechas.
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

            // Acciones.
            if (showActions) {
                OutlinedButton(
                    onClick = onViewDetail,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver informe")
                }
            }
        }
    }
}

/** Formato local de las fechas del informe. */
private val REPORT_DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

private fun formatInstant(instant: Instant): String =
    REPORT_DATE_FORMATTER.withZone(ZoneId.systemDefault()).format(instant)
