package com.ccstudio.mpaz_pro_6.ui.screens.courses

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
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SearchField
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.common.ThemeToggleButton
import com.ccstudio.mpaz_pro_6.ui.components.course.CourseCard

/**
 * Listado de cursos.
 *
 * Consume `GET /api/cursos`. El filtro por año se envía al backend (`?anio`);
 * la búsqueda por nombre es client-side. Las acciones de edición y borrado
 * llegan con sus formularios, así que por ahora el listado es de solo lectura.
 */
@Composable
fun CourseListScreen(
    cursos: List<Course>,
    aniosDisponibles: List<Int>,
    anioSeleccionado: Int?,
    onSelectAnio: (Int?) -> Unit,
    onRetry: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    var search by rememberSaveable { mutableStateOf("") }

    val filteredCourses = cursos.filter { course ->
        val query = search.trim()

        query.isEmpty() ||
                course.nombre.contains(query, ignoreCase = true) ||
                course.anio.toString().contains(query)
    }

    val anioOptions = listOf("Todos") + aniosDisponibles.map { it.toString() }
    val selectedAnioLabel = anioSeleccionado?.toString() ?: "Todos"

    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            title = "Cursos",
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
                isLoading -> LoadingState(message = "Cargando cursos...")

                error != null -> ErrorState(error = error, onRetry = onRetry)

                else -> {
                    Text(
                        text = "Cursos del establecimiento",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${cursos.size} cursos",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )

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
                            message = if (cursos.isEmpty()) {
                                "No hay cursos para este filtro."
                            } else {
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
                                    onEdit = {},
                                    onDelete = {},
                                    // La edición y el borrado llegan con el formulario.
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
