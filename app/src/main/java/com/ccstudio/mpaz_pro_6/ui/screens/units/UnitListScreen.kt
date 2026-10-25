package com.ccstudio.mpaz_pro_6.ui.screens.units

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
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SearchField
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.common.ThemeToggleButton
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitCard

/**
 * Listado de unidades.
 *
 * Consume `GET /api/unidades`, que admite el filtro opcional `?asignaturaId={id}`
 * y ordena por asignatura y orden. El buscador por texto libre es **client-side**:
 * el backend no expone búsqueda por texto.
 *
 * La unidad solo trae el `asignaturaId`, así que la pantalla recibe además la lista
 * de asignaturas para resolver el nombre que se muestra y por el que se filtra.
 *
 * El mismo listado sirve a los tres roles; cambia el contenido según el `UserRole`:
 * el UTP ve las unidades `BORRADOR` y `EN_REVISION` pendientes de revisión, el
 * docente las unidades autorizadas y publicadas, y el estudiante solo las
 * `PUBLICADO`.
 */
@Composable
fun UnitListScreen(
    unidades: List<LearningUnit>,
    asignaturas: List<Subject>,
    onViewDetail: (LearningUnit) -> Unit,
    onAuthorize: ((LearningUnit) -> Unit)?,
    onRetry: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Unidades",
    headerTitle: String = "Revisión de unidades",
    emptyMessage: String? = null,
    contenidosPorUnidad: Map<Int, Int> = emptyMap(),
    actividadesPorUnidad: Map<Int, Int> = emptyMap(),
    pruebaFinalPorUnidad: Map<Int, Boolean> = emptyMap(),
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    var search by rememberSaveable { mutableStateOf("") }
    var subjectFilter by rememberSaveable { mutableStateOf("Todas") }

    // Nombre de cada asignatura, para mostrar y filtrar por ella.
    val subjectNames = asignaturas.associate { it.id to it.nombre }

    val subjectFilterOptions = listOf("Todas") +
            unidades.mapNotNull { subjectNames[it.asignaturaId] }.distinct().sorted()

    val filteredUnits = unidades.filter { unit ->
        val subjectName = subjectNames[unit.asignaturaId]

        val matchesSubject =
            subjectFilter == "Todas" ||
                    subjectName == subjectFilter

        val query = search.trim()

        val matchesSearch =
            unit.titulo.contains(query, ignoreCase = true) ||
                    (subjectName?.contains(query, ignoreCase = true) ?: false)

        matchesSubject && matchesSearch
    }

    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            title = title,
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
                isLoading -> LoadingState(message = "Cargando unidades...")

                error != null -> ErrorState(error = error, onRetry = onRetry)

                else -> {

                    Text(
                        text = headerTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${unidades.size} unidades visibles",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    SearchField(
                        value = search,
                        onValueChange = { search = it },
                        placeholder = "Título o asignatura"
                    )

                    if (subjectFilterOptions.size > 1) {
                        FilterChipRow(
                            options = subjectFilterOptions,
                            selected = subjectFilter,
                            onSelect = { subjectFilter = it }
                        )
                    }

                    SectionHeader(title = "Unidades", count = filteredUnits.size)

                    if (filteredUnits.isEmpty()) {
                        EmptyState(
                            message = emptyMessage ?: if (unidades.isEmpty()) {
                                "No hay unidades para mostrar."
                            } else {
                                "No se encontraron unidades."
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(
                                items = filteredUnits,
                                key = { it.id }
                            ) { unit ->
                                UnitCard(
                                    unit = unit,
                                    subjectName = subjectNames[unit.asignaturaId] ?: "Sin asignatura",
                                    onViewDetail = { onViewDetail(unit) },
                                    onAuthorize = onAuthorize?.let { authorize ->
                                        if (unit.estado == ContentStatus.IN_REVIEW ||
                                            unit.estado == ContentStatus.AUTHORIZED
                                        ) {
                                            { authorize(unit) }
                                        } else {
                                            null
                                        }
                                    },
                                    contentCount = contenidosPorUnidad[unit.id] ?: 0,
                                    activityCount = actividadesPorUnidad[unit.id] ?: 0,
                                    hasFinalTest = pruebaFinalPorUnidad[unit.id] ?: false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
