package com.ccstudio.mpaz_pro_6.ui.components.question

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
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.Difficulty
import com.ccstudio.mpaz_pro_6.data.model.Question
import com.ccstudio.mpaz_pro_6.ui.theme.LocalAppColors

/**
 * Tarjeta de una pregunta.
 *
 * Muestra el enunciado, el criterio de respuesta y la cantidad de alternativas.
 * La actividad solo llega como `activityId`, así que el título lo resuelve la
 * pantalla que lista y se entrega como `activityTitle` (opcional).
 * El borrado siempre se ofrece; si la pregunta tiene alternativas o respuestas
 * de estudiantes, el backend responde `409 Conflict` y la pantalla lo explica.
 */
@Composable
fun QuestionCard(
    question: Question,
    alternativesCount: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    activityTitle: String? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dificultad y orden.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuestionDifficultyChip(difficulty = question.dificultad)

                Text(
                    text = "Orden ${question.orden}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Enunciado.
            Text(
                text = question.enunciado,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // Actividad, si la pantalla pudo resolver su título.
            if (activityTitle != null) {
                Text(
                    text = "Actividad: $activityTitle",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Criterio de respuesta, solo si está definido.
            if (!question.criterioRespuesta.isNullOrBlank()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Criterio de respuesta",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = question.criterioRespuesta,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Cantidad de alternativas.
            Text(
                text = alternativesCountLabel(alternativesCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Acciones.
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

/** Texto de la cantidad de alternativas, con singular y plural. */
private fun alternativesCountLabel(count: Int): String = when (count) {
    0 -> "Sin alternativas"
    1 -> "1 alternativa"
    else -> "$count alternativas"
}

/** Chip de la dificultad de la pregunta. */
@Composable
private fun QuestionDifficultyChip(
    difficulty: Difficulty,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val colors = when (difficulty) {
        Difficulty.LOW -> appColors.success
        Difficulty.MEDIUM -> appColors.warning
        Difficulty.HIGH -> appColors.danger
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = colors.container,
        contentColor = colors.content
    ) {
        Text(
            text = difficulty.wireValue,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelLarge
        )
    }
}
