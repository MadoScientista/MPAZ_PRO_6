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
import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SearchField
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.report.ReportCard

/**
 * Listado de informes del estudiante.
 *
 * Muestra solo sus informes, con la unidad como título. La búsqueda por unidad es
 * client-side. El detalle es de solo lectura.
 */
@Composable
fun StudentReportListScreen(
    informes: List<PedagogicalReport>,
    unitTitles: Map<Int, String>,
    onViewDetail: (PedagogicalReport) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    var search by rememberSaveable { mutableStateOf("") }

    val filteredReports = informes.filter { report ->
        val query = search.trim()
        val unitTitle = unitTitles[report.unidadId].orEmpty()

        query.isEmpty() ||
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
                isLoading && informes.isEmpty() -> LoadingState(message = "Cargando informes...")

                error != null -> ErrorState(error = error, onRetry = onRetry)

                else -> {
                    Text(
                        text = "Mis informes pedagógicos",
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
                        placeholder = "Buscar por unidad"
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
                            },
                            description = if (informes.isEmpty()) {
                                "Cuando se genere un informe de tus actividades aparecerá aquí."
                            } else {
                                null
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
                                    studentName = "",
                                    unitName = unitTitles[report.unidadId]
                                        ?: "Unidad #${report.unidadId}",
                                    onViewDetail = { onViewDetail(report) },
                                    showStudentName = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
