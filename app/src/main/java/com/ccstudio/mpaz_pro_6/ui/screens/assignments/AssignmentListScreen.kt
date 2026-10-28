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
import com.ccstudio.mpaz_pro_6.ui.components.assignment.AssignmentCard
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SearchField
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader

/**
 * Listado de asignaciones del docente.
 *
 * Consume `GET /api/asignaciones`. La asignación solo trae los identificadores
 * de curso y unidad, así que la pantalla recibe esos catálogos para resolver los
 * nombres que muestra. El docente se deriva de la docencia del curso y la
 * asignatura de la unidad.
 *
 * La búsqueda por texto es **client-side**; el backend no expone ese filtro.
 * El botón «Nueva asignación» solo se muestra cuando [onCreate] no es nulo.
 */
@Composable
fun AssignmentListScreen(
    asignaciones: List<Assignment>,
    cursos: List<Course>,
    unidades: List<LearningUnit>,
    onViewDetail: (Assignment) -> Unit,
    onRetry: () -> Unit,
    onCreate: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    var search by rememberSaveable { mutableStateOf("") }

    val courseNames = cursos.associate { it.id to it.nombre }
    val unitTitles = unidades.associate { it.id to it.titulo }

    val filteredAssignments = asignaciones.filter { assignment ->
        val query = search.trim()

        val courseName = courseNames[assignment.cursoId].orEmpty()
        val unitTitle = unitTitles[assignment.unidadId].orEmpty()

        query.isEmpty() ||
                courseName.contains(query, ignoreCase = true) ||
                unitTitle.contains(query, ignoreCase = true)
    }

    Column(modifier = modifier.fillMaxSize()) {
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

                    if (onCreate != null) {
                        AppButton(
                            text = "Nueva asignación",
                            onClick = onCreate
                        )
                    }

                    SearchField(
                        value = search,
                        onValueChange = { search = it },
                        placeholder = "Curso o unidad"
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
                                    onViewDetail = { onViewDetail(assignment) },
                                    // La edición y el borrado llegan con sus formularios.
                                    onEdit = {},
                                    onDelete = {},
                                    // Sin acciones visibles, toda la tarjeta abre el detalle.
                                    showActions = false,
                                    onClick = { onViewDetail(assignment) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
