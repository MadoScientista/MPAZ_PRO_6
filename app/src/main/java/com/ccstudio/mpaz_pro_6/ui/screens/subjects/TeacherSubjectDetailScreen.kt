package com.ccstudio.mpaz_pro_6.ui.screens.subjects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitCard

/**
 * Detalle de una asignatura para el docente.
 *
 * Muestra las unidades de la asignatura y permite filtrarlas por curso. Al
 * elegir un curso solo quedan las unidades que tienen una asignación en ese
 * curso. No ofrece autorización ni creación de unidades: el docente solo
 * consulta el currículo y administra sus asignaciones.
 */
@Composable
fun TeacherSubjectDetailScreen(
    subject: Subject?,
    cursos: List<Course>,
    unidades: List<LearningUnit>,
    selectedCourseId: Int?,
    onSelectCourse: (Int?) -> Unit,
    onViewUnit: (LearningUnit) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    Column(modifier = modifier.fillMaxSize()) {
        when {
            isLoading && subject == null -> LoadingState(
                message = "Cargando la asignatura...",
                modifier = Modifier.padding(16.dp)
            )

            error != null && subject == null -> ErrorState(
                error = error,
                onRetry = onRetry,
                modifier = Modifier.padding(16.dp)
            )

            subject == null -> EmptyState(
                message = "No se encontró la asignatura.",
                modifier = Modifier.padding(16.dp)
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = subject.nombre,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = cursosLabel(cursos),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (cursos.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Filtrar por curso",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            FilterChipRow(
                                options = listOf(TODAS_LAS_UNIDADES) +
                                    cursos.map { it.nombre },
                                selected = cursos
                                    .firstOrNull { it.id == selectedCourseId }
                                    ?.nombre
                                    ?: TODAS_LAS_UNIDADES,
                                onSelect = { label ->
                                    onSelectCourse(
                                        cursos.firstOrNull { it.nombre == label }?.id
                                    )
                                }
                            )
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        SectionHeader(
                            title = "Unidades",
                            count = unidades.size
                        )

                        if (selectedCourseId != null) {
                            Text(
                                text = "Mostrando unidades asignadas al curso seleccionado.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (isLoading && unidades.isEmpty()) {
                    item {
                        LoadingState(message = "Cargando unidades...")
                    }
                } else if (error != null && unidades.isEmpty()) {
                    item {
                        ErrorState(error = error, onRetry = onRetry)
                    }
                } else if (unidades.isEmpty()) {
                    item {
                        EmptyState(
                            message = if (selectedCourseId != null) {
                                "No hay unidades asignadas en este curso."
                            } else {
                                "Esta asignatura no tiene unidades."
                            }
                        )
                    }
                } else {
                    items(unidades, key = { it.id }) { unit ->
                        UnitCard(
                            unit = unit,
                            subjectName = subject.nombre,
                            onViewDetail = { onViewUnit(unit) },
                            onAuthorize = null
                        )
                    }
                }
            }
        }
    }
}

/** Texto de los cursos del docente en la asignatura. */
private fun cursosLabel(courses: List<Course>): String = when (courses.size) {
    0 -> "Sin cursos asignados"
    1 -> "Curso: ${courses.first().nombre}"
    else -> "Cursos: ${courses.joinToString(" · ") { it.nombre }}"
}

/** Opción que representa todas las unidades. */
private const val TODAS_LAS_UNIDADES = "Todas"
