package com.ccstudio.mpaz_pro_6.ui.screens.docencias

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
import com.ccstudio.mpaz_pro_6.data.model.Docencia
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.ConfirmDialog
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SearchField
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.docencia.DocenciaCard

/**
 * Listado de docencias de la UTP.
 *
 * Permite filtrar por curso y buscar por docente, curso o asignatura. La
 * creación, edición y eliminación se delegan al ViewModel; el borrado siempre
 * se ofrece y el ViewModel explica el `409 Conflict` cuando hay asignaciones.
 */
@Composable
fun DocenciaListScreen(
    docencias: List<Docencia>,
    cursos: List<Course>,
    selectedCourseId: Int?,
    onSelectCourse: (Int?) -> Unit,
    onCreate: () -> Unit,
    onEdit: (Docencia) -> Unit,
    onDelete: (Docencia) -> Unit,
    mensajeOperacion: String?,
    onDismissMensaje: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    var search by rememberSaveable { mutableStateOf("") }
    var pendienteEliminar by remember { mutableStateOf<Docencia?>(null) }

    val filteredDocencias = docencias.filter { docencia ->
        val matchesCourse = selectedCourseId == null ||
            docencia.cursoId == selectedCourseId
        val query = search.trim()
        val matchesSearch = query.isEmpty() ||
            docencia.docenteNombre.contains(query, ignoreCase = true) ||
            docencia.cursoNombre.contains(query, ignoreCase = true) ||
            docencia.asignaturaNombre.contains(query, ignoreCase = true)

        matchesCourse && matchesSearch
    }

    val courseOptions = listOf("Todas") + cursos.map { it.nombre }
    val selectedCourseLabel = cursos
        .firstOrNull { it.id == selectedCourseId }
        ?.nombre
        ?: "Todas"

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                isLoading && docencias.isEmpty() -> LoadingState(
                    message = "Cargando docencias..."
                )

                error != null -> ErrorState(error = error, onRetry = onRetry)

                else -> {
                    Text(
                        text = "Docencias",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${docencias.size} docencias",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    AppButton(
                        text = "Nueva docencia",
                        onClick = onCreate
                    )

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
                        placeholder = "Docente, curso o asignatura"
                    )

                    if (courseOptions.size > 1) {
                        FilterChipRow(
                            options = courseOptions,
                            selected = selectedCourseLabel,
                            onSelect = { label ->
                                onSelectCourse(
                                    cursos.firstOrNull { it.nombre == label }?.id
                                )
                            }
                        )
                    }

                    SectionHeader(
                        title = "Docencias",
                        count = filteredDocencias.size
                    )

                    if (filteredDocencias.isEmpty()) {
                        EmptyState(
                            message = if (docencias.isEmpty()) {
                                "No hay docencias registradas."
                            } else {
                                "No se encontraron docencias."
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(
                                items = filteredDocencias,
                                key = { it.id }
                            ) { docencia ->
                                DocenciaCard(
                                    docencia = docencia,
                                    onEdit = { onEdit(docencia) },
                                    onDelete = { pendienteEliminar = docencia }
                                )
                            }
                        }
                    }
                }
            }
        }

        pendienteEliminar?.let { docencia ->
            ConfirmDialog(
                title = "Eliminar docencia",
                message = "¿Eliminar la docencia de «${docencia.docenteNombre}» en " +
                    "«${docencia.cursoNombre} · ${docencia.asignaturaNombre}»? " +
                    "Si tiene asignaciones asociadas, no se podrá eliminar.",
                onConfirm = {
                    onDelete(docencia)
                    pendienteEliminar = null
                },
                onDismiss = { pendienteEliminar = null }
            )
        }
    }
}
