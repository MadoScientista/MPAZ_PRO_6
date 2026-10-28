package com.ccstudio.mpaz_pro_6.ui.screens.assignments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.ui.components.assignment.ActivitySelector
import com.ccstudio.mpaz_pro_6.ui.components.assignment.AssignmentCard
import com.ccstudio.mpaz_pro_6.ui.components.assignment.ObjectiveSelector
import com.ccstudio.mpaz_pro_6.ui.components.common.ConfirmDialog
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader

/**
 * Detalle de una asignación.
 *
 * Presenta el resumen de la asignación y permite al docente editar sus reglas,
 * eliminarla y asociar o desasociar objetivos de aprendizaje y actividades.
 * Los catálogos de la unidad los resuelve el ViewModel.
 *
 * Para roles que no son `DOCENTE`, la pantalla queda en solo lectura: oculta las
 * acciones y deshabilita los selectores. El contrato no documenta autorización
 * por rol para estas operaciones; la restricción de la app sigue el flujo del
 * dominio, donde el docente asigna unidades a cursos.
 */
@Composable
fun AssignmentDetailScreen(
    assignment: Assignment?,
    courseName: String,
    unitTitle: String,
    objectives: List<LearningObjective>,
    activities: List<Activity>,
    associatedObjectiveIds: Set<Int>,
    associatedActivityIds: Set<Int>,
    canManage: Boolean,
    isDeleting: Boolean,
    isSavingObjectives: Boolean,
    isSavingActivities: Boolean,
    mensajeOperacion: String?,
    onViewUnit: () -> Unit,
    onViewCourse: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onObjectiveAssociationChange: (Int, Boolean) -> Unit,
    onActivityAssociationChange: (Int, Boolean) -> Unit,
    onDismissMensaje: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    when {
        isLoading -> LoadingState(
            message = "Cargando la asignación...",
            modifier = Modifier.padding(16.dp)
        )

        error != null -> ErrorState(
            error = error,
            onRetry = onRetry,
            modifier = Modifier.padding(16.dp)
        )

        assignment == null -> EmptyState(
            message = "No se encontró la asignación.",
            modifier = Modifier.padding(16.dp)
        )

        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                AssignmentCard(
                    assignment = assignment,
                    courseName = courseName,
                    unitTitle = unitTitle,
                    onViewDetail = {},
                    onEdit = {},
                    onDelete = {},
                    showActions = false
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewUnit,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ver unidad")
                    }

                    OutlinedButton(
                        onClick = onViewCourse,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Ver curso")
                    }
                }
            }

            if (canManage) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onEdit,
                            modifier = Modifier.weight(1f),
                            enabled = !isDeleting
                        ) {
                            Text("Editar")
                        }

                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.weight(1f),
                            enabled = !isDeleting,
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

            if (mensajeOperacion != null) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mensajeOperacion,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        TextButton(onClick = onDismissMensaje) {
                            Text("Cerrar")
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SectionHeader(
                        title = "Objetivos de aprendizaje",
                        count = objectives.size
                    )

                    Text(
                        text = "${associatedObjectiveIds.size} asociados a esta asignación.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                ObjectiveSelector(
                    objectives = objectives,
                    associatedObjectiveIds = associatedObjectiveIds,
                    onAssociationChange = onObjectiveAssociationChange,
                    isReadOnly = !canManage,
                    isSaving = isSavingObjectives
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SectionHeader(
                        title = "Actividades",
                        count = activities.size
                    )

                    Text(
                        text = "${associatedActivityIds.size} asociadas a esta asignación.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                ActivitySelector(
                    activities = activities,
                    associatedActivityIds = associatedActivityIds,
                    onAssociationChange = onActivityAssociationChange,
                    isReadOnly = !canManage,
                    isSaving = isSavingActivities
                )
            }
        }
    }

    if (showDeleteDialog && assignment != null) {
        ConfirmDialog(
            title = "Eliminar asignación",
            message = "¿Eliminar la asignación de «$unitTitle» en «$courseName»? " +
                "Si tiene objetivos, actividades o intentos asociados, no se podrá eliminar.",
            onConfirm = {
                showDeleteDialog = false
                onDelete()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}
