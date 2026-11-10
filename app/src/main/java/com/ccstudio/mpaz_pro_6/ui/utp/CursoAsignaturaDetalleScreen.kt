package com.ccstudio.mpaz_pro_6.ui.utp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.local.dao.UnidadConDetalles
import com.ccstudio.mpaz_pro_6.ui.components.StatusChip
import com.ccstudio.mpaz_pro_6.ui.components.SubjectLabel

@Composable
fun CursoAsignaturaDetalleScreen(
    cursoId: Long,
    asignaturaId: Long,
    viewModel: UtpViewModel,
    rol: String = "UTP",
    onUnidadClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.asignaturaDetailState.collectAsState()
    var showAddUnidadDialog by remember { mutableStateOf(false) }
    val isEstudiante = rol.equals("ESTUDIANTE", ignoreCase = true)

    LaunchedEffect(cursoId, asignaturaId, rol) {
        viewModel.loadAsignaturaDetail(cursoId, asignaturaId, isEstudiante = isEstudiante)
    }

    if (state.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val asignatura = state.asignatura
    val curso = state.cursoConDetalles

    if (asignatura == null || curso == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No se pudo cargar el detalle de la asignatura.")
        }
        return
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Encabezado
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    SubjectLabel(
                        subjectName = asignatura.nombre,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = curso.nombreCompletoConAnnio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Fila de acciones y título
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Unidades Didácticas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (!isEstudiante) {
                Button(onClick = { showAddUnidadDialog = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Agregar Unidad")
                }
            }
        }

        // Lista de Unidades
        if (state.unidades.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEstudiante) "No hay unidades autorizadas para esta asignatura." else "No hay unidades creadas para esta asignatura.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.unidades) { unidadConDetalles ->
                    UnidadItemCard(
                        unidadConDetalles = unidadConDetalles,
                        isEstudiante = isEstudiante,
                        onClick = { onUnidadClick(unidadConDetalles.unidad.id) }
                    )
                }
            }
        }
    }

    // Dialog para agregar Nueva Unidad
    if (showAddUnidadDialog) {
        AddUnidadDialog(
            onDismiss = { showAddUnidadDialog = false },
            onConfirm = { nombre ->
                viewModel.addUnidadToAsignatura(cursoId, asignaturaId, nombre)
                showAddUnidadDialog = false
            }
        )
    }
}

@Composable
private fun UnidadItemCard(
    unidadConDetalles: UnidadConDetalles,
    isEstudiante: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = unidadConDetalles.unidad.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (!isEstudiante) {
                    Spacer(modifier = Modifier.height(4.dp))
                    StatusChip(statusName = unidadConDetalles.estado.nombre)
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver Detalle de Unidad",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AddUnidadDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var nombreUnidad by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Nueva Unidad") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Ingresa el nombre de la nueva unidad didáctica:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = nombreUnidad,
                    onValueChange = { nombreUnidad = it },
                    label = { Text("Nombre de la Unidad") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nombreUnidad.isNotBlank()) {
                        onConfirm(nombreUnidad.trim())
                    }
                },
                enabled = nombreUnidad.isNotBlank()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
