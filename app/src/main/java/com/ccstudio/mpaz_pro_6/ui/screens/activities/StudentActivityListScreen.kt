package com.ccstudio.mpaz_pro_6.ui.screens.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.ui.components.activity.StudentActivityCard
import com.ccstudio.mpaz_pro_6.ui.components.activity.StudentActivityStatus
import com.ccstudio.mpaz_pro_6.ui.components.common.CounterCard
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader

/**
 * Listado de actividades del estudiante.
 *
 * Muestra todas las actividades disponibles con su estado y permite filtrarlas.
 * Una actividad pendiente o en progreso abre el intento; una completada abre su
 * último resultado.
 */
@Composable
fun StudentActivityListScreen(
    items: List<StudentActivityItem>,
    sinCurso: Boolean,
    onOpenActivity: (Activity) -> Unit,
    onViewResult: (Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    var filter by rememberSaveable { mutableStateOf("Todas") }

    val pendientes = items.count { it.status == StudentActivityStatus.PENDING }
    val enProgreso = items.count { it.status == StudentActivityStatus.IN_PROGRESS }
    val completadas = items.count { it.status == StudentActivityStatus.COMPLETED }

    val filteredItems = items.filter { item ->
        when (filter) {
            "Pendientes" -> item.status == StudentActivityStatus.PENDING
            "En progreso" -> item.status == StudentActivityStatus.IN_PROGRESS
            "Completadas" -> item.status == StudentActivityStatus.COMPLETED
            else -> true
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        when {
            isLoading && items.isEmpty() -> LoadingState(message = "Cargando actividades...")

            error != null -> ErrorState(error = error, onRetry = onRetry)

            sinCurso -> EmptyState(
                message = "No tienes curso asignado.",
                description = "La UTP debe asignarte un curso para ver tus actividades.",
                modifier = Modifier.padding(16.dp)
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Tus actividades",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CounterCard(
                            titulo = "Pendientes",
                            valor = pendientes,
                            modifier = Modifier.weight(1f)
                        )

                        CounterCard(
                            titulo = "En progreso",
                            valor = enProgreso,
                            modifier = Modifier.weight(1f)
                        )

                        CounterCard(
                            titulo = "Completadas",
                            valor = completadas,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    FilterChipRow(
                        options = listOf(
                            "Todas",
                            "Pendientes",
                            "En progreso",
                            "Completadas"
                        ),
                        selected = filter,
                        onSelect = { filter = it }
                    )
                }

                item {
                    SectionHeader(
                        title = "Actividades",
                        count = filteredItems.size
                    )
                }

                if (items.isEmpty()) {
                    item {
                        EmptyState(
                            message = "No tienes actividades asignadas.",
                            description = "Cuando el docente asigne una actividad aparecerá aquí."
                        )
                    }
                } else if (filteredItems.isEmpty()) {
                    item {
                        EmptyState(message = "No hay actividades con este filtro.")
                    }
                } else {
                    items(
                        items = filteredItems,
                        key = { it.activity.id }
                    ) { item ->
                        StudentActivityCard(
                            activity = item.activity,
                            unitTitle = item.unitTitle,
                            status = item.status,
                            attemptsCount = item.attemptsCount,
                            onClick = {
                                val completedAttemptId = item.latestCompletedAttemptId

                                if (item.status == StudentActivityStatus.COMPLETED &&
                                    completedAttemptId != null
                                ) {
                                    onViewResult(completedAttemptId)
                                } else {
                                    onOpenActivity(item.activity)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
