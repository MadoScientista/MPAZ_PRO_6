package com.ccstudio.mpaz_pro_6.ui.components.subject

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.common.StatusChip
import com.ccstudio.mpaz_pro_6.ui.theme.LocalAppColors
import com.ccstudio.mpaz_pro_6.ui.theme.subjectAccent

/**
 * Tarjeta de una asignatura en el listado.
 *
 * Muestra el badge con el color de la materia, el nombre, la cantidad de
 * unidades y un chip con las unidades pendientes de revisión. Toda la tarjeta
 * abre el detalle de la asignatura; los iconos de editar y eliminar quedan como
 * acciones separadas.
 *
 * El borrado siempre se ofrece; si la asignatura tiene unidades asociadas, el
 * backend responde `409 Conflict` y la pantalla lo explica.
 */
@Composable
fun SubjectCard(
    subject: Subject,
    unitCount: Int,
    pendingCount: Int,
    onViewDetail: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    showActions: Boolean = true,
    showPendingChip: Boolean = true
) {
    val accent = subjectAccent(subject.nombre) ?: MaterialTheme.colorScheme.primary
    val appColors = LocalAppColors.current
    val pendingColors = if (pendingCount > 0) appColors.warning else appColors.success

    Card(
        onClick = onViewDetail,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(accent.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Category,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = subject.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = unitCountLabel(unitCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (showActions) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar asignatura"
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Eliminar asignatura",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (showPendingChip) {
                StatusChip(
                    label = pendingLabel(pendingCount),
                    containerColor = pendingColors.container,
                    contentColor = pendingColors.content
                )
            }
        }
    }
}

/** Texto de la cantidad de unidades, con singular y plural. */
private fun unitCountLabel(count: Int): String = when (count) {
    0 -> "Sin unidades"
    1 -> "1 unidad"
    else -> "$count unidades"
}

/** Texto del chip de unidades pendientes de revisión. */
private fun pendingLabel(count: Int): String = when (count) {
    0 -> "Sin pendientes"
    1 -> "1 pendiente"
    else -> "$count pendientes"
}
