package com.ccstudio.mpaz_pro_6.ui.components.assignment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState

/**
 * Selector de actividades de una asignación.
 *
 * Lista las actividades de la unidad y permite asociar o desasociar cada una. El
 * estado de cada casilla lo mantiene la pantalla en `associatedActivityIds`; este
 * componente solo emite el nuevo estado deseado.
 *
 * Las `PRUEBA_FINAL` no se pueden asociar: el backend las rechaza con
 * `400 Bad Request`. Por eso su casilla queda deshabilitada y se identifican con un
 * chip propio, sin inventar una asociación que el contrato no permite.
 */
@Composable
fun ActivitySelector(
    activities: List<Activity>,
    associatedActivityIds: Set<Int>,
    onAssociationChange: (activityId: Int, isAssociated: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = false,
    isSaving: Boolean = false
) {
    if (activities.isEmpty()) {
        EmptyState(
            message = "Sin actividades",
            description = "La unidad todavía no tiene actividades para asociar.",
            modifier = modifier
        )
        return
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        activities.forEach { activity ->
            val isFinalTest = activity.tipo == ActivityType.FINAL_TEST
            val isAssociated = activity.id in associatedActivityIds
            val isSelectable = !isFinalTest && !isReadOnly && !isSaving

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isFinalTest) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surface
                },
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = !isFinalTest && isAssociated,
                        onCheckedChange = { checked ->
                            if (isSelectable) {
                                onAssociationChange(activity.id, checked)
                            }
                        },
                        enabled = isSelectable
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = activity.titulo,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text = "${activity.tipo.label} · Orden ${activity.orden}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (isFinalTest) {
                            Text(
                                text = "No se puede asociar a una asignación",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    if (isFinalTest) {
                        FinalTestChip()
                    }
                }
            }
        }
    }
}

/** Chip que identifica una actividad no asociable. */
@Composable
private fun FinalTestChip(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.errorContainer

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color,
        contentColor = contentColorFor(color)
    ) {
        Text(
            text = ActivityType.FINAL_TEST.label,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelLarge
        )
    }
}
