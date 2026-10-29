package com.ccstudio.mpaz_pro_6.ui.components.attempt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.Attempt
import com.ccstudio.mpaz_pro_6.ui.theme.LocalAppColors

/**
 * Barra de progreso de un intento.
 *
 * Muestra cuántas preguntas se han respondido sobre el total y si el intento sigue
 * en progreso o ya se completó. La finalización se deduce de `attempt.fechaFin`:
 * `null` significa que el intento está en curso, tal como indica el contrato. No
 * existe un campo aparte de estado que el componente pueda recibir.
 *
 * La pantalla entrega el conteo de respuestas registradas; el componente lo
 * normaliza para no dibujar una barra fuera de rango.
 */
@Composable
fun AttemptProgress(
    attempt: Attempt,
    answeredCount: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier,
    maxIntentos: Int? = null
) {
    val total = totalQuestions.coerceAtLeast(0)
    val answered = if (total > 0) {
        answeredCount.coerceIn(0, total)
    } else {
        0
    }
    val isCompleted = attempt.fechaFin != null
    val fraction = if (total > 0) {
        answered.toFloat() / total
    } else {
        0f
    }
    val percentage = if (total > 0) {
        answered * 100 / total
    } else {
        0
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Número del intento y estado de realización.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val attemptLabel = if (maxIntentos != null) {
                    "Intento ${attempt.numero} de $maxIntentos"
                } else {
                    "Intento ${attempt.numero}"
                }
                Text(
                    text = attemptLabel,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                AttemptStatusChip(isCompleted = isCompleted)
            }

            // Progreso sobre las preguntas de la actividad.
            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = if (total > 0) {
                    "$answered de $total preguntas respondidas ($percentage%)"
                } else {
                    "La actividad no tiene preguntas para responder"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Chip del estado de realización del intento. */
@Composable
private fun AttemptStatusChip(
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val colors = if (isCompleted) {
        appColors.success
    } else {
        appColors.warning
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = colors.container,
        contentColor = colors.content
    ) {
        Text(
            text = if (isCompleted) "Completado" else "En progreso",
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelLarge
        )
    }
}
