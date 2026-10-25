package com.ccstudio.mpaz_pro_6.ui.components.attempt

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.Alternative

/**
 * Opción de respuesta de una pregunta.
 *
 * Muestra el texto de la alternativa y, cuando la pantalla ya conoce el resultado,
 * su estado de corrección. Ese estado llega congelado desde el backend en
 * `StudentAnswer.esCorrecta`; el componente no lo recalcula. Mientras no se conozca,
 * `isCorrect` queda en `null` y solo se muestra la opción.
 *
 * La selección es exclusiva por pregunta: la pantalla mantiene un único
 * `selectedAlternativeId` y este componente solo emite la alternativa tocada.
 * Con `onSelect` nulo la opción queda como solo lectura, pero conserva la marca de
 * selección para presentar el resultado de un intento.
 */
@Composable
fun AnswerOption(
    alternative: Alternative,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isCorrect: Boolean? = null,
    enabled: Boolean = true,
    onSelect: (() -> Unit)? = null
) {
    val containerColor = when {
        isCorrect == true -> MaterialTheme.colorScheme.primaryContainer
        isCorrect == false && isSelected -> MaterialTheme.colorScheme.errorContainer
        isSelected -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val borderColor = when {
        isCorrect == true -> MaterialTheme.colorScheme.primary
        isCorrect == false && isSelected -> MaterialTheme.colorScheme.error
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    val canSelect = enabled && onSelect != null

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                enabled = canSelect,
                role = Role.RadioButton,
                onClick = { onSelect?.invoke() }
            )
            .testTag("alternative-${alternative.id}"),
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        contentColor = contentColorFor(containerColor),
        border = BorderStroke(
            width = if (isSelected || isCorrect == true) 2.dp else 1.dp,
            color = borderColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 48.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = null,
                enabled = enabled
            )

            Text(
                text = alternative.texto,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            )

            if (isCorrect != null) {
                AnswerCorrectionChip(isCorrect = isCorrect)
            }
        }
    }
}

/** Chip que indica si la alternativa es la correcta de su pregunta. */
@Composable
private fun AnswerCorrectionChip(
    isCorrect: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        contentColor = if (isCorrect) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.error
        }
    ) {
        Text(
            text = if (isCorrect) "Correcta" else "Incorrecta",
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelLarge
        )
    }
}
