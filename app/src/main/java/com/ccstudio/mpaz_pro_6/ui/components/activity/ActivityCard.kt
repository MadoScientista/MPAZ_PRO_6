package com.ccstudio.mpaz_pro_6.ui.components.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.Activity

/**
 * Tarjeta de una actividad.
 *
 * Muestra el tipo (`ActivityType`), el orden, el título y la cantidad de preguntas.
 * La actividad solo trae `unidadId`, así que el título de la unidad lo resuelve la
 * pantalla que lista y se entrega como `unitTitle` (opcional).
 *
 * El borrado siempre se ofrece cuando la pantalla lo habilita; si la actividad
 * tiene preguntas, asignaciones o intentos asociados, el backend responde
 * `409 Conflict` y la pantalla lo explica.
 *
 * Las acciones son opcionales: en una pantalla de solo consulta (por ejemplo, la
 * revisión de la UTP) se omiten y la tarjeta queda como resumen. `detailLabel`
 * permite que el estudiante vea «Resolver» en lugar de «Ver detalle».
 */
@Composable
fun ActivityCard(
    activity: Activity,
    questionsCount: Int,
    modifier: Modifier = Modifier,
    unitTitle: String? = null,
    onViewDetail: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    detailLabel: String = "Ver detalle"
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
            // Tipo y orden dentro de la unidad.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActivityTypeChip(activityType = activity.tipo)

                Text(
                    text = "Orden ${activity.orden}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Título.
            Text(
                text = activity.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Unidad, si la pantalla pudo resolver su título.
            if (unitTitle != null) {
                Text(
                    text = "Unidad: $unitTitle",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Cantidad de preguntas.
            Text(
                text = questionsCountLabel(questionsCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Acceso al detalle, solo si la pantalla lo habilita.
            if (onViewDetail != null) {
                OutlinedButton(
                    onClick = onViewDetail,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(detailLabel)
                }
            }

            // Acciones de gestión, solo si la pantalla las habilita.
            if (onEdit != null || onDelete != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onEdit != null) {
                        OutlinedButton(
                            onClick = onEdit,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Editar")
                        }
                    }

                    if (onDelete != null) {
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
}

/** Texto de la cantidad de preguntas, con singular y plural. */
private fun questionsCountLabel(count: Int): String = when (count) {
    0 -> "Sin preguntas"
    1 -> "1 pregunta"
    else -> "$count preguntas"
}
