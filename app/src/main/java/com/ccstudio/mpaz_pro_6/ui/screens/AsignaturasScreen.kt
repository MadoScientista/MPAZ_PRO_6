package com.ccstudio.mpaz_pro_6.ui.screens

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
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ccstudio.mpaz_pro_6.data.local.dao.AsignaturaCursoDetalle
import com.ccstudio.mpaz_pro_6.ui.components.SubjectLabel

@Composable
fun AsignaturasScreen(
    onAsignaturaClick: (cursoId: Long, asignaturaId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AsignaturasViewModel = viewModel(),
) {
    val state by viewModel.asignaturasState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAsignaturas()
    }

    val filteredList = remember(state.asignaturas, state.searchQuery) {
        val query = state.searchQuery.trim().lowercase()
        if (query.isEmpty()) {
            state.asignaturas
        } else {
            state.asignaturas.filter { item ->
                item.asignatura.nombre.lowercase().contains(query) ||
                        item.nombreCompletoCurso.lowercase().contains(query) ||
                        item.nombreCurso.lowercase().contains(query) ||
                        (item.docente != null && "${item.docente.pnombre} ${item.docente.apaterno}".lowercase().contains(query))
            }
        }
    }

    val rol = state.userRol.uppercase()
    val subtitleText = when (rol) {
        "UTP" -> "Todas las asignaturas del colegio (${filteredList.size})"
        "DOCENTE" -> "Asignaturas a su cargo (${filteredList.size})"
        "ESTUDIANTE" -> "Asignaturas de tu curso (${filteredList.size})"
        else -> "Asignaturas (${filteredList.size})"
    }

    val emptyText = when (rol) {
        "UTP" -> "No hay asignaturas registradas en el sistema."
        "DOCENTE" -> "No tiene asignaturas asignadas a su cargo actualmente."
        "ESTUDIANTE" -> "No tienes asignaturas registradas en tu curso actualmente."
        else -> "No hay asignaturas disponibles."
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Buscador
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar asignatura...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                )
            },
            trailingIcon = {
                if (state.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar búsqueda",
                        )
                    }
                }
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium,
        )

        // Subtítulo según Rol
        Text(
            text = subtitleText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        // Contenido según estado
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else if (state.asignaturas.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = emptyText,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else if (filteredList.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No se encontraron asignaturas con '${state.searchQuery}'.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                items(filteredList) { item ->
                    AsignaturaItemCard(
                        item = item,
                        showDocenteInfo = rol == "UTP" || rol == "ESTUDIANTE",
                        onClick = {
                            onAsignaturaClick(
                                item.asignaturaCurso.cursoId,
                                item.asignaturaCurso.asignaturaId,
                            )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AsignaturaItemCard(
    item: AsignaturaCursoDetalle,
    showDocenteInfo: Boolean,
    onClick: () -> Unit,
) {
    val docenteNombre = item.docente?.let { "${it.pnombre} ${it.apaterno}" } ?: "Sin docente asignado"

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(end = 16.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Book,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                SubjectLabel(
                    subjectName = item.asignatura.nombre,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    textColor = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .height(16.dp)
                            .width(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Curso: ${item.nombreCompletoCurso}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                if (showDocenteInfo) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .height(16.dp)
                                .width(16.dp),
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Docente: $docenteNombre",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver Asignatura",
                tint = MaterialTheme.colorScheme.outline,
            )
        }
    }
}
