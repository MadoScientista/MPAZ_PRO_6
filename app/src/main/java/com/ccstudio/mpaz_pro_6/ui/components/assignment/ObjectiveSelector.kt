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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState

/**
 * Selector de objetivos de aprendizaje de una asignación.
 *
 * Lista los objetivos de la unidad y permite asociar o desasociar cada uno. El
 * estado de cada casilla lo mantiene la pantalla en `associatedObjectiveIds`; este
 * componente solo emite el nuevo estado deseado. La pantalla traduce ese cambio a
 * `POST` o `DELETE` en `/api/asignaciones/{id}/objetivos-aprendizaje/{oaId}`.
 */
@Composable
fun ObjectiveSelector(
    objectives: List<LearningObjective>,
    associatedObjectiveIds: Set<Int>,
    onAssociationChange: (objectiveId: Int, isAssociated: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isReadOnly: Boolean = false,
    isSaving: Boolean = false
) {
    if (objectives.isEmpty()) {
        EmptyState(
            message = "Sin objetivos de aprendizaje",
            description = "La unidad todavía no tiene objetivos para asociar.",
            modifier = modifier
        )
        return
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        objectives.forEach { objective ->
            val isAssociated = objective.id in associatedObjectiveIds

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isAssociated,
                        onCheckedChange = { checked ->
                            onAssociationChange(objective.id, checked)
                        },
                        enabled = !isReadOnly && !isSaving
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = objective.codigo ?: "Sin código",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text(
                            text = objective.descripcion,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Eje: ${objective.eje}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
