package com.ccstudio.mpaz_pro_6.ui.components.assignment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.ui.theme.LocalAppColors
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Tarjeta de una asignación.
 *
 * Muestra la unidad, el curso, el docente, la fecha y las reglas de evaluación
 * (`maxIntentos` y `pruebaFinalHabilitada`). La asignación solo trae identificadores,
 * así que los nombres los resuelve la pantalla que lista y los entrega como
 * `courseName`, `unitTitle` y `teacherName`.
 *
 * El borrado siempre se ofrece; si la asignación tiene objetivos, actividades o
 * intentos asociados, el backend responde `409 Conflict` y la pantalla lo explica.
 */
@Composable
fun AssignmentCard(
    assignment: Assignment,
    courseName: String,
    unitTitle: String,
    teacherName: String,
    onViewDetail: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    showActions: Boolean = true
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Curso.
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Text(
                    text = courseName,
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Unidad asignada.
            Text(
                text = unitTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Docente y fecha.
            Text(
                text = "Docente: $teacherName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Fecha: ${formatInstant(assignment.fecha)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Reglas de la prueba final.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FinalTestStatusChip(enabled = assignment.pruebaFinalHabilitada)

                Text(
                    text = maxAttemptsLabel(assignment.maxIntentos),
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
                    Text("Ver detalle")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Editar")
                    }

                    Button(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        )
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}

/** Texto del límite de intentos de la prueba final. */
private fun maxAttemptsLabel(maxAttempts: Int?): String = when (maxAttempts) {
    null -> "Prueba final sin límite de intentos"
    1 -> "Prueba final: hasta 1 intento"
    else -> "Prueba final: hasta $maxAttempts intentos"
}

/** Chip que indica si la prueba final está habilitada para el curso. */
@Composable
private fun FinalTestStatusChip(
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val colors = if (enabled) appColors.success else appColors.neutral

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = colors.container,
        contentColor = colors.content
    ) {
        Text(
            text = if (enabled) {
                "Prueba final habilitada"
            } else {
                "Prueba final deshabilitada"
            },
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/** Formato local de la fecha de la asignación. */
private val ASSIGNMENT_DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

private fun formatInstant(instant: Instant): String =
    ASSIGNMENT_DATE_FORMATTER.withZone(ZoneId.systemDefault()).format(instant)
