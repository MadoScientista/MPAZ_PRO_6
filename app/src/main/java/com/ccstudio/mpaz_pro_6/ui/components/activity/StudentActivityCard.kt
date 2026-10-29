package com.ccstudio.mpaz_pro_6.ui.components.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
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
import com.ccstudio.mpaz_pro_6.ui.theme.SemanticColor

/** Estado de una actividad para el estudiante. */
enum class StudentActivityStatus {
    /** Sin intentos registrados. */
    PENDING,

    /** Tiene un intento con `fechaFin` nula. */
    IN_PROGRESS,

    /** Tiene al menos un intento completado. */
    COMPLETED
}

/**
 * Tarjeta de una actividad en el listado del estudiante.
 *
 * Muestra el tipo, el título, la unidad, el contenido opcional y el estado de
 * realización. El texto de acción cambia según el estado: resolver, continuar o
 * ver el resultado. La pantalla decide a dónde navegar; la tarjeta solo emite el
 * toque.
 */
@Composable
fun StudentActivityCard(
    activity: Activity,
    unitTitle: String,
    status: StudentActivityStatus,
    attemptsCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentTitle: String? = null,
    maxAttempts: Int? = null
) {
    val isExhausted = maxAttempts != null && attemptsCount >= maxAttempts && status == StudentActivityStatus.PENDING
    val actionText = if (isExhausted) "Ya no quedan más intentos" else status.actionLabel

    Card(
        onClick = if (isExhausted) { {} } else onClick,
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
                ActivityTypeChip(activityType = activity.tipo)


                StatusChip(
                    label = status.label,
                    containerColor = status.colors().container,
                    contentColor = status.colors().content
                )
            }

            Text(
                text = activity.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = unitTitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (contentTitle != null) {
                Text(
                    text = "Contenido: $contentTitle",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val attemptsText = if (maxAttempts != null) {
                    "Intentos: $attemptsCount de $maxAttempts"
                } else {
                    when (attemptsCount) {
                        0 -> "Sin intentos"
                        1 -> "1 intento"
                        else -> "$attemptsCount intentos"
                    }
                }

                Text(
                    text = attemptsText,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isExhausted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Tarjeta compacta de una actividad para el carrusel del panel del estudiante.
 *
 * Muestra el tipo, el estado, el título, la unidad y la acción
 * (“Resolver” o “Continuar”). Tocar la tarjeta abre el intento correspondiente.
 */
@Composable
fun StudentActivityCarouselCard(
    activity: Activity,
    unitTitle: String,
    status: StudentActivityStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(176.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActivityTypeChip(activityType = activity.tipo)

                StatusChip(
                    label = status.label,
                    containerColor = status.colors().container,
                    contentColor = status.colors().content
                )
            }

            Text(
                text = activity.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = unitTitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = status.actionLabel,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/** Etiqueta visible del estado. */
private val StudentActivityStatus.label: String
    get() = when (this) {
        StudentActivityStatus.PENDING -> "Pendiente"
        StudentActivityStatus.IN_PROGRESS -> "En progreso"
        StudentActivityStatus.COMPLETED -> "Completada"
    }

/** Etiqueta de la acción que ejecuta el toque. */
private val StudentActivityStatus.actionLabel: String
    get() = when (this) {
        StudentActivityStatus.PENDING -> "Resolver"
        StudentActivityStatus.IN_PROGRESS -> "Continuar"
        StudentActivityStatus.COMPLETED -> "Ver resultado"
    }

/** Colores del chip según el estado. */
@Composable
private fun StudentActivityStatus.colors(): SemanticColor {
    val appColors = LocalAppColors.current
    return when (this) {
        StudentActivityStatus.PENDING -> appColors.neutral
        StudentActivityStatus.IN_PROGRESS -> appColors.warning
        StudentActivityStatus.COMPLETED -> appColors.success
    }
}
