package com.ccstudio.mpaz_pro_6.ui.screens.assignments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.ui.components.assignment.AssignmentCard
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SearchField
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.common.ThemeToggleButton

/**
 * Listado de asignaciones del docente.
 *
 * Consume `GET /api/asignaciones` con el filtro `?docenteId`. La asignación solo
 * trae los identificadores de curso, unidad y docente, así que la pantalla
 * recibe esos catálogos para resolver los nombres que muestra.
 *
 * La búsqueda por texto es **client-side**; el backend no expone ese filtro.
 */
@Composable
fun AssignmentListScreen(
    asignaciones: List<Assignment>,
    cursos: List<Course>,
    unidades: List<LearningUnit>,
    docentes: List<User>,
    onViewDetail: (Assignment) -> Unit,
    onRetry: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    var search by rememberSaveable { mutableStateOf("") }

    val courseNames = cursos.associate { it.id to it.nombre }
    val unitTitles = unidades.associate { it.id to it.titulo }
    val teacherNames = docentes.associate { it.id to it.nombre }

    val filteredAssignments = asignaciones.filter { assignment ->
        val query = search.trim()

        val courseName = courseNames[assignment.cursoId].orEmpty()
        val unitTitle = unitTitles[assignment.unidadId].orEmpty()
        val teacherName = teacherNames[assignment.docenteId].orEmpty()

        query.isEmpty() ||
                courseName.contains(query, ignoreCase = true) ||
                unitTitle.contains(query, ignoreCase = true) ||
                teacherName.contains(query, ignoreCase = true)
    }

    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            title = "Mis asignaciones",
            actions = {
                ThemeToggleButton(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                isLoading && asignaciones.isEmpty() -> LoadingState(message = "Cargando asignaciones...")

                error != null -> ErrorState(error = error, onRetry = onRetry)

                else -> {
                    Text(
                        text = "Unidades asignadas a tus cursos",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${asignaciones.size} asignaciones",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    SearchField(
                        value = search,
                        onValueChange = { search = it },
                        placeholder = "Curso, unidad o docente"
                    )

                    SectionHeader(
                        title = "Asignaciones",
                        count = filteredAssignments.size
                    )

                    if (filteredAssignments.isEmpty()) {
                        EmptyState(
                            message = if (asignaciones.isEmpty()) {
                                "No tienes asignaciones."
                            } else {
                                "No se encontraron asignaciones."
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(
                                items = filteredAssignments,
                                key = { it.id }
                            ) { assignment ->
                                AssignmentCard(
                                    assignment = assignment,
                                    courseName = courseNames[assignment.cursoId]
                                        ?: "Sin curso",
                                    unitTitle = unitTitles[assignment.unidadId]
                                        ?: "Sin unidad",
                                    teacherName = teacherNames[assignment.docenteId]
                                        ?: "Sin docente",
                                    onViewDetail = { onViewDetail(assignment) },
                                    // La edición y el borrado llegan con sus formularios.
                                    onEdit = {},
                                    onDelete = {},
                                    // Sin detalle todavía: no se ofrecen acciones muertas.
                                    showActions = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
