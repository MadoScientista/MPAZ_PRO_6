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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import com.ccstudio.mpaz_pro_6.data.local.dao.AsignaturaCursoDetalle
import com.ccstudio.mpaz_pro_6.ui.components.StatusChip
import com.ccstudio.mpaz_pro_6.ui.components.SubjectLabel
import com.ccstudio.mpaz_pro_6.ui.theme.LocalAppColors

@Composable
fun DocenteDetalleScreen(
    docenteId: Long,
    viewModel: UtpViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.docenteDetailState.collectAsState()
    val appColors = LocalAppColors.current

    var showAssignDialog by remember { mutableStateOf(false) }
    var itemToUnassign by remember { mutableStateOf<AsignaturaCursoDetalle?>(null) }

    LaunchedEffect(docenteId) {
        viewModel.loadDocenteDetail(docenteId)
    }

    if (state.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (state.docente == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No se encontró la información del docente.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
        }
    } else {
        val docente = state.docente!!

        Box(modifier = modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Tarjeta de Información del Docente
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = docente.nombreCompleto,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                StatusChip(
                                    statusName = if (docente.activo) "ACTIVO" else "INACTIVO"
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Usuario: @${docente.nombreUsuario}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "Rol: Docente",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Sección Header de Asignaturas
            item {
                Text(
                    text = "Asignaturas asignadas (${state.asignaturasAsignadas.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Lista de asignaturas asignadas
            if (state.asignaturasAsignadas.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Este docente no tiene asignaturas asignadas.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(state.asignaturasAsignadas) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.padding(end = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Book,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                SubjectLabel(
                                    subjectName = item.asignatura.nombre,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    textColor = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier
                                            .height(14.dp)
                                            .width(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Curso: ${item.nombreCompletoCurso}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(
                                onClick = { itemToUnassign = item }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Desasignar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
            }
            FloatingActionButton(
                onClick = { showAssignDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = appColors.fabContainer,
                contentColor = appColors.fabContent,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp,
                    focusedElevation = 0.dp,
                    hoveredElevation = 0.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Asignar asignatura"
                )
            }
        }
    }

    // Diálogo para Confirmar Desasignación
    if (itemToUnassign != null) {
        val target = itemToUnassign!!
        AlertDialog(
            onDismissRequest = { itemToUnassign = null },
            title = { Text("Confirmar desasignación") },
            text = {
                Text(
                    "¿Deseas quitar la asignatura '${target.asignatura.nombre}' en '${target.nombreCompletoCurso}' del docente ${state.docente?.nombreCompleto}?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.unassignDocenteFromAsignaturaCurso(
                            docenteId = docenteId,
                            cursoId = target.asignaturaCurso.cursoId,
                            asignaturaId = target.asignaturaCurso.asignaturaId
                        )
                        itemToUnassign = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Desasignar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { itemToUnassign = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo para Asignar Asignatura
    if (showAssignDialog) {
        AsignarAsignaturaDialog(
            docenteNombre = state.docente?.nombreCompleto ?: "",
            availableList = state.availableAsignaturasCurso,
            onDismiss = { showAssignDialog = false },
            onConfirm = { cursoId, asignaturaId ->
                viewModel.assignDocenteToAsignaturaCursoFromDetail(
                    docenteId = docenteId,
                    cursoId = cursoId,
                    asignaturaId = asignaturaId
                )
                showAssignDialog = false
            }
        )
    }
}

@Composable
private fun AsignarAsignaturaDialog(
    docenteNombre: String,
    availableList: List<AsignaturaCursoDetalle>,
    onDismiss: () -> Unit,
    onConfirm: (cursoId: Long, asignaturaId: Long) -> Unit
) {
    var selectedItem by remember { mutableStateOf<AsignaturaCursoDetalle?>(null) }
    var filterText by remember { mutableStateOf("") }

    val filteredList = availableList.filter { item ->
        val query = filterText.trim().lowercase()
        if (query.isEmpty()) {
            true
        } else {
            item.asignatura.nombre.lowercase().contains(query) ||
                    item.nombreCompletoCurso.lowercase().contains(query) ||
                    (item.docente?.nombreCompleto?.lowercase()?.contains(query) == true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Asignar Asignatura a $docenteNombre") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Selecciona una asignatura de los cursos disponibles:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = filterText,
                    onValueChange = { filterText = it },
                    placeholder = { Text("Filtrar asignatura...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (availableList.isEmpty()) {
                    Text(
                        text = "No hay más asignaturas disponibles para asignar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else if (filteredList.isEmpty()) {
                    Text(
                        text = "No se encontraron resultados con '$filterText'.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredList) { item ->
                            val isSelected = selectedItem?.asignaturaCurso?.id == item.asignaturaCurso.id
                            Surface(
                                onClick = { selectedItem = item },
                                modifier = Modifier.fillMaxWidth(),
                                shape = MaterialTheme.shapes.small,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedItem = item }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.asignatura.nombre,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Curso: ${item.nombreCompletoCurso}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (item.docente != null) {
                                            Text(
                                                text = "Actualmente a cargo de: ${item.docente.nombreCompleto}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        } else {
                                            Text(
                                                text = "Sin docente asignado",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = selectedItem != null,
                onClick = {
                    val sel = selectedItem
                    if (sel != null) {
                        onConfirm(sel.asignaturaCurso.cursoId, sel.asignaturaCurso.asignaturaId)
                    }
                }
            ) {
                Text("Asignar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
