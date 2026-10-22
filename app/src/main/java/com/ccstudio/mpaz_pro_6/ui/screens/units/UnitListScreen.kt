package com.ccstudio.mpaz_pro_6.ui.screens.units

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SearchField
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
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
 * el UTP ve las unidades `EN_REVISION` pendientes de autorización, el docente las
 * `AUTORIZADO` y `PUBLICADO`, y el estudiante solo las `PUBLICADO`. La acción de
 * autorizar solo se ofrece al rol `UTP`.
 */
@Composable
fun UnitListScreen(
    unidades: List<LearningUnit>,
    asignaturas: List<Subject>,
    rol: UserRole,
    onViewDetail: (LearningUnit) -> Unit,
    onAuthorize: (LearningUnit) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null,
    onCerrarSesion: (() -> Unit)? = null
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when {
            isLoading -> LoadingState(message = "Cargando unidades...")

            error != null -> ErrorState(error = error, onRetry = onRetry)

            else -> {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = tituloListado(rol),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${unidades.size} ${subtituloListado(rol)}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    // El docente y el estudiante aterrizan en este listado, así que
                    // necesitan aquí su forma de cerrar sesión.
                    if (onCerrarSesion != null) {
                        TextButton(onClick = onCerrarSesion) {
                            Text("Salir")
                        }
                    }
                }

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
                        message = if (unidades.isEmpty()) {
                            mensajeListadoVacio(rol)
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
                                onAuthorize = { onAuthorize(unit) },
                                puedeAutorizar = rol == UserRole.UTP
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Título del listado según el rol: cada uno ve un conjunto distinto de unidades.
 */
private fun tituloListado(rol: UserRole): String = when (rol) {
    UserRole.UTP -> "Unidades en revisión"
    UserRole.DOCENTE -> "Mis unidades"
    UserRole.ESTUDIANTE -> "Unidades disponibles"
}

/**
 * Texto que acompaña al contador de unidades del listado.
 */
private fun subtituloListado(rol: UserRole): String = when (rol) {
    UserRole.UTP -> "unidades por autorizar"
    UserRole.DOCENTE -> "unidades asignadas"
    UserRole.ESTUDIANTE -> "unidades publicadas"
}

/**
 * Aviso que se muestra cuando el listado no tiene unidades para el rol.
 */
private fun mensajeListadoVacio(rol: UserRole): String = when (rol) {
    UserRole.UTP -> "No hay unidades en revisión para autorizar."
    UserRole.DOCENTE -> "No hay unidades autorizadas ni publicadas."
    UserRole.ESTUDIANTE -> "No hay unidades publicadas."
}
