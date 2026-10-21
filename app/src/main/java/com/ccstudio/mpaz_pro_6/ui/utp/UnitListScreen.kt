package com.ccstudio.mpaz_pro_6.ui.utp

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// Datos que necesita mostrar cada tarjeta.
data class UnidadPendiente(
    val id: Int,
    val asignatura: String,
    val titulo: String,
    val cantidadActividades: Int
)

@Composable
fun UnitListScreen(
    unidadesPendientes: List<UnidadPendiente>,
    onVerDetalle: (UnidadPendiente) -> Unit,
    onAutorizar: (UnidadPendiente) -> Unit,
    modifier: Modifier = Modifier
) {
    var busqueda by rememberSaveable { mutableStateOf("") }
    var filtroAsignatura by rememberSaveable { mutableStateOf("Todas") }

    val asignaturas = listOf("Todas") +
            unidadesPendientes.map { it.asignatura }.distinct().sorted()

    val unidadesFiltradas = unidadesPendientes.filter { unidad ->
        val coincideAsignatura =
            filtroAsignatura == "Todas" ||
                    unidad.asignatura == filtroAsignatura

        val texto = busqueda.trim()

        val coincideBusqueda =
            unidad.titulo.contains(texto, ignoreCase = true) ||
                    unidad.asignatura.contains(texto, ignoreCase = true)

        coincideAsignatura && coincideBusqueda
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
            text = "${unidadesPendientes.size} revisiones pendientes",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium
        )

        // Buscador.
        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
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
            items(asignaturas) { asignatura ->
                FilterChip(
                    selected = filtroAsignatura == asignatura,
                    onClick = { filtroAsignatura = asignatura },
                    label = { Text(asignatura) }
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
                text = " (${unidadesFiltradas.size})",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Listado de tarjetas.
        if (unidadesFiltradas.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (unidadesPendientes.isEmpty()) {
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
                    items = unidadesFiltradas,
                    key = { it.id }
                ) { unidad ->
                    UnidadCard(
                        unidad = unidad,
                        onVerDetalle = { onVerDetalle(unidad) },
                        onAutorizar = { onAutorizar(unidad) }
                    )
                }
            }
        }
    }
}

@Composable
private fun UnidadCard(
    unidad: UnidadPendiente,
    onVerDetalle: () -> Unit,
    onAutorizar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Asignatura.
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Text(
                    text = unidad.asignatura,
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Título de la unidad.
            Text(
                text = unidad.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Número de actividades.
            Text(
                text = if (unidad.cantidadActividades == 1) {
                    "1 actividad"
                } else {
                    "${unidad.cantidadActividades} actividades"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Acciones de la jefa de UTP.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onVerDetalle,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ver detalle")
                }

                Button(
                    onClick = onAutorizar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Autorizar")
                }
            }
        }
    }
}