package com.ccstudio.mpaz_pro_6.ui.components.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.ObjectiveLevel
import com.ccstudio.mpaz_pro_6.data.model.ReportObjectiveDetail

/**
 * Tarjeta del nivel alcanzado en un objetivo de aprendizaje.
 *
 * Muestra las respuestas correctas sobre el total y el nivel resultante:
 * `FORTALEZA` si `correctas * 100.0 / total >= 70`, `REFUERZO` en caso contrario.
 * El nivel se toma del `ReportObjectiveDetail` ya calculado por el backend; la
 * tarjeta solo lo presenta. El código y la descripción del OA los resuelve la
 * pantalla, porque el detalle del informe solo trae el `oaId`.
 */
@Composable
fun ReportObjectiveLevelCard(
    detail: ReportObjectiveDetail,
    modifier: Modifier = Modifier,
    objectiveCode: String? = null,
    objectiveDescription: String? = null
) {
    val fraction = if (detail.total > 0) {
        detail.correctas.toFloat() / detail.total
    } else {
        0f
    }
    val percentage = if (detail.total > 0) {
        detail.correctas * 100 / detail.total
    } else {
        0
    }

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
            // Código del OA y nivel.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = objectiveCode ?: "OA ${detail.oaId}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                ObjectiveLevelChip(level = detail.nivel)
            }

            if (objectiveDescription != null) {
                Text(
                    text = objectiveDescription,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Progreso.
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = if (detail.total > 0) {
                    "Respuestas correctas: ${detail.correctas} de ${detail.total} ($percentage%)"
                } else {
                    "Sin preguntas respondidas para este objetivo"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Chip del nivel alcanzado por el estudiante en un objetivo. */
@Composable
private fun ObjectiveLevelChip(
    level: ObjectiveLevel,
    modifier: Modifier = Modifier
) {
    val color = when (level) {
        ObjectiveLevel.STRENGTH -> MaterialTheme.colorScheme.primaryContainer
        ObjectiveLevel.SUPPORT -> MaterialTheme.colorScheme.tertiaryContainer
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color,
        contentColor = contentColorFor(color)
    ) {
        Text(
            text = level.wireValue,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelLarge
        )
    }
}
