package com.ccstudio.mpaz_pro_6.ui.components.unit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.ui.components.common.StatusChip

/**
 * Tarjeta de una unidad.
 *
 * Muestra el resumen de una `LearningUnit`: asignatura, título, nivel educativo y
 * estado. La unidad solo trae el `asignaturaId`, así que el nombre de la asignatura
 * lo resuelve la pantalla que la lista y se lo entrega como `subjectName`.
 *
 * Cuando la pantalla conoce el resumen de la unidad, puede entregar
 * `contentCount`, `activityCount` y `hasFinalTest`; la tarjeta los muestra como
 * chips. Si los tres quedan en `null` (por ejemplo, en el panel de inicio), la
 * sección de resumen se omite.
 *
 * Las acciones dependen del `ContentStatus`: el botón de autorizar solo debe
 * aparecer cuando la unidad está pendiente de revisión y el usuario tiene rol
 * `UTP`.
 */
@Composable
fun UnitCard(
    unit: LearningUnit,
    subjectName: String,
    onViewDetail: () -> Unit,
    onAuthorize: (() -> Unit)?,
    modifier: Modifier = Modifier,
    contentCount: Int? = null,
    activityCount: Int? = null,
    hasFinalTest: Boolean? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subjectName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )

                UnitStatusChip(status = unit.estado)
            }

            Text(
                text = unit.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = unit.nivelEducativo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (contentCount != null || activityCount != null || hasFinalTest != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (contentCount != null) {
                        StatusChip(label = contentCountLabel(contentCount))
                    }

                    if (activityCount != null) {
                        StatusChip(label = activityCountLabel(activityCount))
                    }
                }

                if (hasFinalTest != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Prueba final",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        StatusChip(label = if (hasFinalTest) "Sí" else "No")
                    }
                }
            }

            // Acciones de la jefa de UTP.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetail,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ver detalle")
                }

                if (onAuthorize != null) {
                    Button(
                        onClick = onAuthorize,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Autorizar")
                    }
                }
            }
        }
    }
}

/** Texto de la cantidad de contenidos, con singular y plural. */
private fun contentCountLabel(count: Int): String = when (count) {
    1 -> "1 contenido"
    else -> "$count contenidos"
}

/** Texto de la cantidad de actividades, con singular y plural. */
private fun activityCountLabel(count: Int): String = when (count) {
    1 -> "1 actividad"
    else -> "$count actividades"
}
