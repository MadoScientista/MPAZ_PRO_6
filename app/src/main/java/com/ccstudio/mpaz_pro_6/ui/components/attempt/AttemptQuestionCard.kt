package com.ccstudio.mpaz_pro_6.ui.components.attempt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.Alternative
import com.ccstudio.mpaz_pro_6.data.model.Question
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState

/**
 * Tarjeta de pregunta durante un intento.
 *
 * Muestra el enunciado, el criterio de respuesta opcional y las alternativas
 * seleccionables. La pantalla conserva un único `selectedAlternativeId` porque solo
 * una alternativa puede ser la respuesta de cada pregunta; la tarjeta solo emite la
 * alternativa tocada.
 *
 * En modo de solo lectura (`isReadOnly`) las opciones dejan de ser interactivas. Con
 * `showCorrectness` se revela además el estado congelado de cada alternativa; durante
 * la resolución debe quedar en `false` para no anticipar la respuesta correcta.
 */
@Composable
fun AttemptQuestionCard(
    question: Question,
    alternatives: List<Alternative>,
    selectedAlternativeId: Int?,
    onSelectAlternative: (Int) -> Unit,
    modifier: Modifier = Modifier,
    questionNumber: Int? = null,
    isReadOnly: Boolean = false,
    isLocked: Boolean = false,
    showCorrectness: Boolean = false,
    enabled: Boolean = true
) {
    val canSelect = enabled && !isReadOnly && !isLocked

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Número de la pregunta y enunciado.
            Text(
                text = if (questionNumber != null) {
                    "Pregunta $questionNumber"
                } else {
                    "Pregunta"
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            if (isLocked && !showCorrectness) {
                Text(
                    text = "Respuesta ya registrada",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = question.enunciado,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

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

            // Alternativas seleccionables.
            if (alternatives.isEmpty()) {
                EmptyState(
                    message = "Sin alternativas",
                    description = "Esta pregunta todavía no tiene alternativas para responder."
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    alternatives.forEach { alternative ->
                        AnswerOption(
                            alternative = alternative,
                            isSelected = alternative.id == selectedAlternativeId,
                            isCorrect = if (showCorrectness) {
                                alternative.esCorrecta
                            } else {
                                null
                            },
                            enabled = canSelect,
                            onSelect = if (canSelect) {
                                { onSelectAlternative(alternative.id) }
                            } else {
                                null
                            }
                        )
                    }
                }
            }
        }
    }
}
