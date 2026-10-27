package com.ccstudio.mpaz_pro_6.ui.screens.courses

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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.ConfirmDialog
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SearchField
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.course.CourseCard

/**
 * Listado de cursos.
 *
 * Consume `GET /api/cursos`. El filtro por año se envía al backend (`?anio`);
 * la búsqueda por nombre es client-side. Permite crear, editar y borrar; el
 * `409 Conflict` del borrado se explica con el mensaje del ViewModel.
 *
 * Con [canManage] en `false` queda en modo consulta: oculta «Nuevo curso» y las
 * acciones de edición/borrado, para el listado de cursos del docente.
 */
@Composable
fun CourseListScreen(
    cursos: List<Course>,
    aniosDisponibles: List<Int>,
    anioSeleccionado: Int?,
    estudiantesPorCurso: Map<Int, Int>,
    onSelectAnio: (Int?) -> Unit,
    onCreate: () -> Unit,
    onViewDetail: (Course) -> Unit,
    onEdit: (Course) -> Unit,
    onDelete: (Course) -> Unit,
    mensajeOperacion: String?,
    onDismissMensaje: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null,
    canManage: Boolean = true
) {
    var search by rememberSaveable { mutableStateOf("") }
    var pendienteEliminar by remember { mutableStateOf<Course?>(null) }

    val filteredCourses = cursos.filter { course ->
        val query = search.trim()

        query.isEmpty() ||
                course.nombre.contains(query, ignoreCase = true) ||
                course.anio.toString().contains(query)
    }

    val anioOptions = listOf("Todos") + aniosDisponibles.map { it.toString() }
    val selectedAnioLabel = anioSeleccionado?.toString() ?: "Todos"

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                isLoading && cursos.isEmpty() -> LoadingState(message = "Cargando cursos...")

                error != null -> ErrorState(error = error, onRetry = onRetry)

                else -> {
                    Text(
                        text = if (canManage) {
                            "Cursos del establecimiento"
                        } else {
                            "Mis cursos"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (canManage) {
                            "${cursos.size} cursos"
                        } else {
                            "${cursos.size} cursos asignados"
                        },
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (canManage) {
                        AppButton(
                            text = "Nuevo curso",
                            onClick = onCreate
                        )
                    }

                    if (mensajeOperacion != null) {
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

                    SearchField(
                        value = search,
                        onValueChange = { search = it },
                        placeholder = "Buscar por nombre o año"
                    )

                    if (anioOptions.size > 1) {
                        FilterChipRow(
                            options = anioOptions,
                            selected = selectedAnioLabel,
                            onSelect = { label ->
                                onSelectAnio(label.toIntOrNull())
                            }
                        )
                    }

                    SectionHeader(
                        title = "Cursos",
                        count = filteredCourses.size
                    )

                    if (filteredCourses.isEmpty()) {
                        EmptyState(
                            message = when {
                                cursos.isEmpty() && !canManage ->
                                    "No tienes cursos asignados."
                                cursos.isEmpty() ->
                                    "No hay cursos para este filtro."
                                else ->
                                    "No se encontraron cursos."
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(
                                items = filteredCourses,
                                key = { it.id }
                            ) { course ->
                                CourseCard(
                                    course = course,
                                    studentCount = estudiantesPorCurso[course.id] ?: 0,
                                    onViewDetail = { onViewDetail(course) },
                                    onEdit = { onEdit(course) },
                                    onDelete = { pendienteEliminar = course },
                                    showActions = canManage
                                )
                            }
                        }
                    }
                }
            }
        }

        pendienteEliminar?.let { course ->
            ConfirmDialog(
                title = "Eliminar curso",
                message = "¿Eliminar «${course.nombre}» (${course.anio})? " +
                        "Esta acción no se puede deshacer.",
                onConfirm = {
                    onDelete(course)
                    pendienteEliminar = null
                },
                onDismiss = { pendienteEliminar = null }
            )
        }
    }
}
