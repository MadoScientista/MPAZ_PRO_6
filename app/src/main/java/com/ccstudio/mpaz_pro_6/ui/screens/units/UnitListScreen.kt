package com.ccstudio.mpaz_pro_6.ui.screens.units

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
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
 * docente las unidades de sus asignaciones y el estudiante solo las `PUBLICADO`.
 */
@Composable
fun UnitListScreen(
    pendingUnits: List<LearningUnit>,
    subjects: List<Subject>,
    onViewDetail: (LearningUnit) -> Unit,
    onAuthorize: (LearningUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    var search by rememberSaveable { mutableStateOf("") }
    var subjectFilter by rememberSaveable { mutableStateOf("Todas") }

    // Nombre de cada asignatura, para mostrar y filtrar por ella.
    val subjectNames = subjects.associate { it.id to it.nombre }

    val subjectFilterOptions = listOf("Todas") +
            pendingUnits.mapNotNull { subjectNames[it.asignaturaId] }.distinct().sorted()

    val filteredUnits = pendingUnits.filter { unit ->
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
        // Título y total de revisiones pendientes.
        Text(
            text = "Revisión de unidades",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "${pendingUnits.size} revisiones pendientes",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )

        // Buscador.
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Buscar unidad") },
            placeholder = { Text("Título o asignatura") },
            singleLine = true
        )

        // Filtros por asignatura.
        Text(
            text = "Filtrar por asignatura",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 4.dp)
        ) {
            items(subjectFilterOptions) { subject ->
                FilterChip(
                    selected = subjectFilter == subject,
                    onClick = { subjectFilter = subject },
                    label = { Text(subject) }
                )
            }
        }

        // Encabezado del listado.
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Unidades por revisar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = " (${filteredUnits.size})",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Listado de tarjetas.
        if (filteredUnits.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (pendingUnits.isEmpty()) {
                        "No hay unidades pendientes."
                    } else {
                        "No se encontraron unidades."
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
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
                        onAuthorize = { onAuthorize(unit) }
                    )
                }
            }
        }
    }
}
