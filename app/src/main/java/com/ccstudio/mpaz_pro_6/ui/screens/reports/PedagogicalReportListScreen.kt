package com.ccstudio.mpaz_pro_6.ui.screens.reports

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
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SearchField
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.common.ThemeToggleButton
import com.ccstudio.mpaz_pro_6.ui.components.report.ReportCard

/**
 * Listado de informes pedagógicos del docente.
 *
 * Consume `GET /api/informes-pedagogicos` sin filtro de docente (el backend no lo
 * admite) y el ViewModel descarta los de otros docentes. Los nombres de
 * estudiante y unidad se resuelven con los catálogos que recibe la pantalla.
 *
 * La búsqueda por texto es **client-side**.
 */
@Composable
fun PedagogicalReportListScreen(
    informes: List<PedagogicalReport>,
    usuarios: List<User>,
    unidades: List<LearningUnit>,
    onViewDetail: (PedagogicalReport) -> Unit,
    onRetry: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    var search by rememberSaveable { mutableStateOf("") }

    val studentNames = usuarios.associate { it.id to it.nombre }
    val unitTitles = unidades.associate { it.id to it.titulo }

    val filteredReports = informes.filter { report ->
        val query = search.trim()

        val studentName = studentNames[report.estudianteId].orEmpty()
        val unitTitle = unitTitles[report.unidadId].orEmpty()

        query.isEmpty() ||
                studentName.contains(query, ignoreCase = true) ||
                unitTitle.contains(query, ignoreCase = true)
    }

    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            title = "Informes pedagógicos",
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
                isLoading -> LoadingState(message = "Cargando informes...")

                error != null -> ErrorState(error = error, onRetry = onRetry)

                else -> {
                    Text(
                        text = "Informes de tus estudiantes",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${informes.size} informes",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    SearchField(
                        value = search,
                        onValueChange = { search = it },
                        placeholder = "Estudiante o unidad"
                    )

                    SectionHeader(
                        title = "Informes",
                        count = filteredReports.size
                    )

                    if (filteredReports.isEmpty()) {
                        EmptyState(
                            message = if (informes.isEmpty()) {
                                "No tienes informes."
                            } else {
                                "No se encontraron informes."
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(
                                items = filteredReports,
                                key = { it.id }
                            ) { report ->
                                ReportCard(
                                    report = report,
                                    studentName = studentNames[report.estudianteId]
                                        ?: "Estudiante #${report.estudianteId}",
                                    unitName = unitTitles[report.unidadId]
                                        ?: "Unidad #${report.unidadId}",
                                    onViewDetail = { onViewDetail(report) },
                                    // Sin detalle todavía: no se ofrece una acción muerta.
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
