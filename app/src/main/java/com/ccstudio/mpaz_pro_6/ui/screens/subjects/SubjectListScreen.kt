package com.ccstudio.mpaz_pro_6.ui.screens.subjects

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
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SearchField
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.common.ThemeToggleButton
import com.ccstudio.mpaz_pro_6.ui.components.subject.SubjectCard

/**
 * Listado de asignaturas.
 *
 * Consume `GET /api/asignaturas` (ordenado por nombre) y busca por texto libre
 * en cliente. Las acciones de edición y borrado llegan con sus formularios; por
 * ahora el listado es de solo lectura y no ofrece botones muertos.
 */
@Composable
fun SubjectListScreen(
    asignaturas: List<Subject>,
    onRetry: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    var search by rememberSaveable { mutableStateOf("") }

    val filteredSubjects = asignaturas.filter { subject ->
        val query = search.trim()

        query.isEmpty() ||
                subject.nombre.contains(query, ignoreCase = true)
    }

    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            title = "Asignaturas",
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
                isLoading -> LoadingState(message = "Cargando asignaturas...")

                error != null -> ErrorState(error = error, onRetry = onRetry)

                else -> {
                    Text(
                        text = "Asignaturas del currículo",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${asignaturas.size} asignaturas",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    SearchField(
                        value = search,
                        onValueChange = { search = it },
                        placeholder = "Buscar por nombre"
                    )

                    SectionHeader(
                        title = "Asignaturas",
                        count = filteredSubjects.size
                    )

                    if (filteredSubjects.isEmpty()) {
                        EmptyState(
                            message = if (asignaturas.isEmpty()) {
                                "No hay asignaturas."
                            } else {
                                "No se encontraron asignaturas."
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(
                                items = filteredSubjects,
                                key = { it.id }
                            ) { subject ->
                                SubjectCard(
                                    subject = subject,
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
