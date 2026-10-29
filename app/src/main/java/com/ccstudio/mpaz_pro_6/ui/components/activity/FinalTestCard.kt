package com.ccstudio.mpaz_pro_6.ui.components.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import com.ccstudio.mpaz_pro_6.ui.components.common.StatusChip
import com.ccstudio.mpaz_pro_6.ui.theme.LocalAppColors
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Tarjeta de la prueba final de una unidad.
 *
 * Presenta el chip de tipo (`Prueba final`), el título, la cantidad de preguntas,
 * la fecha de aplicación y el máximo de intentos. La fecha y el límite de intentos
 * viven en la asignación de la unidad, por eso llegan resueltos desde la pantalla.
 *
 * El chip de habilitación solo se muestra cuando la pantalla conoce el estado de
 * la asignación (`enabled` no nulo). Las acciones son opcionales: el estudiante
 * recibe «Resolver», la UTP «Vista previa» y el docente «Vista previa» más
 * «Editar» para abrir la configuración de la prueba final.
 */
@Composable
fun FinalTestCard(
    activity: Activity,
    questionsCount: Int,
    modifier: Modifier = Modifier,
    applicationDate: Instant? = null,
    maxAttempts: Int? = null,
    enabled: Boolean? = null,
    onOpen: (() -> Unit)? = null,
    onEdit: (() -> Unit)? = null,
    openLabel: String = "Vista previa"
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActivityTypeChip(activityType = activity.tipo)

                if (enabled != null) {
                    val colors = if (enabled) {
                        LocalAppColors.current.success
                    } else {
                        LocalAppColors.current.neutral
                    }

                    StatusChip(
                        label = if (enabled) "Habilitada" else "Deshabilitada",
                        containerColor = colors.container,
                        contentColor = colors.content
                    )
                }
            }

            Text(
                text = activity.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = questionsCountLabel(questionsCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Fecha de aplicación: ${dateLabel(applicationDate)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "Número máximo de intentos: ${maxAttemptsLabel(maxAttempts)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (onOpen != null && onEdit != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpen,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(openLabel)
                    }

                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Editar")
                    }
                }
            } else {
                if (onOpen != null) {
                    OutlinedButton(
                        onClick = onOpen,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(openLabel)
                    }
                }

                if (onEdit != null) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Editar")
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

/** Texto del máximo de intentos configurado para la prueba final. */
private fun maxAttemptsLabel(maxAttempts: Int?): String = when (maxAttempts) {
    null -> "Sin límite"
    1 -> "1 intento"
    else -> "$maxAttempts intentos"
}

/** Fecha de aplicación local o aviso de que todavía no se definió. */
private fun dateLabel(instant: Instant?): String = instant
    ?.let { FINAL_TEST_DATE_FORMATTER.withZone(ZoneId.systemDefault()).format(it) }
    ?: "Sin definir"

/** Formato local de la fecha de aplicación. */
private val FINAL_TEST_DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
