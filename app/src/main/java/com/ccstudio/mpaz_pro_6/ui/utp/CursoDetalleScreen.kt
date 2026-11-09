package com.ccstudio.mpaz_pro_6.ui.utp

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AsignaturaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity
import com.ccstudio.mpaz_pro_6.ui.components.SubjectLabel
import com.ccstudio.mpaz_pro_6.ui.navigation.fadeThroughContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CursoDetalleScreen(
    cursoId: Long,
    viewModel: UtpViewModel,
    rol: String = "UTP",
    onAsignaturaClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.cursoDetailState.collectAsState()
    val isUtp = rol.equals("UTP", ignoreCase = true)
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    var showAddAsignaturaDialog by remember { mutableStateOf(false) }
    var showAddDocenteDialog by remember { mutableStateOf(false) }
    var showAddEstudianteDialog by remember { mutableStateOf(false) }
    var assigningDocenteForAsignaturaId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(cursoId) {
        viewModel.loadCursoDetail(cursoId)
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

    val curso = state.cursoConDetalles

    if (curso == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Curso no encontrado.")
        }
        return
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Encabezado del curso
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = curso.nombreCompletoConAnnio,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Pestañas
        SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Asignaturas (${state.asignaturasConDocente.size})") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Docentes (${state.docentes.size})") }
            )
            Tab(
                selected = selectedTabIndex == 2,
                onClick = { selectedTabIndex = 2 },
                text = { Text("Estudiantes (${state.estudiantes.size})") }
            )
        }

        // Contenido según pestaña activa
        Box(modifier = Modifier.weight(1f)) {
            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = { fadeThroughContent() },
                modifier = Modifier.fillMaxSize(),
                label = "cursoTabs"
            ) { tab ->
                when (tab) {
                    0 -> AsignaturasTabContent(
                        asignaturasConDocente = state.asignaturasConDocente,
                        isUtp = isUtp,
                        onAsignaturaClick = onAsignaturaClick,
                        onRemoveAsignatura = { asignaturaId ->
                            viewModel.removeAsignaturaFromCurso(cursoId, asignaturaId)
                        },
                        onAssignDocenteClick = { asignaturaId ->
                            assigningDocenteForAsignaturaId = asignaturaId
                        }
                    )
                    1 -> DocentesTabContent(
                        docentes = state.docentes,
                        isUtp = isUtp,
                        onRemoveDocente = { docenteId ->
                            viewModel.removeDocenteFromCurso(cursoId, docenteId)
                        }
                    )
                    2 -> EstudiantesTabContent(
                        estudiantes = state.estudiantes,
                        isUtp = isUtp,
                        onRemoveEstudiante = { estudianteId ->
                            viewModel.removeEstudianteFromCurso(cursoId, estudianteId)
                        }
                    )
                }
            }

            if (isUtp) {
                FloatingActionButton(
                    onClick = {
                        when (selectedTabIndex) {
                            0 -> showAddAsignaturaDialog = true
                            1 -> showAddDocenteDialog = true
                            2 -> showAddEstudianteDialog = true
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp,
                        focusedElevation = 0.dp,
                        hoveredElevation = 0.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = when (selectedTabIndex) {
                            0 -> "Agregar asignatura"
                            1 -> "Agregar docente"
                            else -> "Agregar estudiante"
                        }
                    )
                }
            }
        }
    }

    // Dialog para agregar Asignatura
    if (showAddAsignaturaDialog) {
        AddAsignaturaDialog(
            availableAsignaturas = state.availableAsignaturas,
            availableDocentes = state.allDocentes,
            onDismiss = { showAddAsignaturaDialog = false },
            onAddExisting = { asignaturaId, docenteId ->
                viewModel.addAsignaturaToCurso(cursoId, asignaturaId, docenteId)
                showAddAsignaturaDialog = false
            },
            onCreateNew = { nombre, docenteId ->
                viewModel.createAndAddAsignaturaToCurso(cursoId, nombre, curso.curso.nivelId, docenteId)
                showAddAsignaturaDialog = false
            }
        )
    }

    // Dialog para asignar/cambiar Docente a una Asignatura específica
    assigningDocenteForAsignaturaId?.let { asigId ->
        val targetItem = state.asignaturasConDocente.find { it.asignatura.id == asigId }
        if (targetItem != null) {
            AssignDocenteToAsignaturaDialog(
                asignaturaNombre = targetItem.asignatura.nombre,
                availableDocentes = state.allDocentes,
                currentDocenteId = targetItem.docente?.id,
                onDismiss = { assigningDocenteForAsignaturaId = null },
                onAssignDocente = { docenteId ->
                    viewModel.assignDocenteToAsignaturaCurso(cursoId, asigId, docenteId)
                    assigningDocenteForAsignaturaId = null
                }
            )
        }
    }

    // Dialog para agregar / cambiar Docente
    if (showAddDocenteDialog) {
        AddUserDialog(
            title = "Gestionar Docente del Curso",
            subtitle = "Selecciona un docente activo para asignar a este curso:",
            availableUsers = state.availableDocentes,
            onDismiss = { showAddDocenteDialog = false },
            onAddUser = { usuarioId ->
                viewModel.addDocenteToCurso(cursoId, usuarioId)
                showAddDocenteDialog = false
            }
        )
    }

    // Dialog para agregar Estudiante
    if (showAddEstudianteDialog) {
        AddUserDialog(
            title = "Agregar Estudiante al Curso",
            subtitle = "Selecciona un estudiante para matricular en este curso:",
            availableUsers = state.availableEstudiantes,
            onDismiss = { showAddEstudianteDialog = false },
            onAddUser = { usuarioId ->
                viewModel.addEstudianteToCurso(cursoId, usuarioId)
                showAddEstudianteDialog = false
            }
        )
    }
}

@Composable
private fun AsignaturasTabContent(
    asignaturasConDocente: List<com.ccstudio.mpaz_pro_6.data.local.dao.AsignaturaConDocente>,
    isUtp: Boolean = true,
    onAsignaturaClick: (Long) -> Unit,
    onRemoveAsignatura: (Long) -> Unit,
    onAssignDocenteClick: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Asignaturas impartidas",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        if (asignaturasConDocente.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay asignaturas asociadas a este curso.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(asignaturasConDocente) { item ->
                    val asignatura = item.asignatura
                    val docente = item.docente
                    Card(
                        onClick = { onAsignaturaClick(asignatura.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                SubjectLabel(
                                    subjectName = asignatura.nombre,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                    textColor = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2
                                )
                                Text(
                                    text = if (docente != null) "Docente a cargo: ${docente.nombreCompleto}" else "Sin docente asignado",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (docente != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            }
                            if (isUtp) {
                                IconButton(onClick = { onAssignDocenteClick(asignatura.id) }) {
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = "Asignar / Cambiar Docente",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                IconButton(onClick = { onRemoveAsignatura(asignatura.id) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Quitar Asignatura",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DocentesTabContent(
    docentes: List<UsuarioEntity>,
    isUtp: Boolean = true,
    onRemoveDocente: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Docente(s) del Curso",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        if (docentes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay docentes asignados a este curso.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(docentes) { docente ->
                    UserCardItem(
                        usuario = docente,
                        icon = Icons.Default.Badge,
                        showRemove = isUtp,
                        onRemove = { onRemoveDocente(docente.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EstudiantesTabContent(
    estudiantes: List<UsuarioEntity>,
    isUtp: Boolean = true,
    onRemoveEstudiante: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Estudiantes Matriculados",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        if (estudiantes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay estudiantes registrados en este curso.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estudiantes) { estudiante ->
                    UserCardItem(
                        usuario = estudiante,
                        icon = Icons.Default.Person,
                        showRemove = isUtp,
                        onRemove = { onRemoveEstudiante(estudiante.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun UserCardItem(
    usuario: UsuarioEntity,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    showRemove: Boolean = true,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 12.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = usuario.nombreCompleto,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Usuario: @${usuario.nombreUsuario}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (showRemove) {
                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remover",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAsignaturaDialog(
    availableAsignaturas: List<AsignaturaEntity>,
    availableDocentes: List<UsuarioEntity>,
    onDismiss: () -> Unit,
    onAddExisting: (Long, Long?) -> Unit,
    onCreateNew: (String, Long?) -> Unit
) {
    var mode by remember { mutableIntStateOf(0) } // 0 = Seleccionar Existente, 1 = Crear Nueva
    var selectedAsignaturaId by remember { mutableStateOf<Long?>(availableAsignaturas.firstOrNull()?.id) }
    var selectedDocenteId by remember { mutableStateOf<Long?>(null) }
    var newAsignaturaName by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var docenteDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Asignatura al Curso") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.selectable(
                        selected = mode == 0,
                        onClick = { mode = 0 },
                        role = Role.RadioButton
                    )
                ) {
                    RadioButton(
                        selected = mode == 0,
                        onClick = null
                    )
                    Text(text = "Seleccionar de catálogo")
                }

                if (mode == 0) {
                    if (availableAsignaturas.isEmpty()) {
                        Text(
                            text = "No hay asignaturas disponibles sin asignar para este nivel.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 32.dp, bottom = 8.dp)
                        )
                    } else {
                        ExposedDropdownMenuBox(
                            expanded = dropdownExpanded,
                            onExpandedChange = { dropdownExpanded = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 32.dp, top = 4.dp, bottom = 8.dp)
                        ) {
                            val selectedName = availableAsignaturas.find { it.id == selectedAsignaturaId }?.nombre ?: "Seleccionar..."
                            OutlinedTextField(
                                value = selectedName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Asignatura") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false }
                            ) {
                                availableAsignaturas.forEach { asig ->
                                    DropdownMenuItem(
                                        text = { Text(asig.nombre) },
                                        onClick = {
                                            selectedAsignaturaId = asig.id
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.selectable(
                        selected = mode == 1,
                        onClick = { mode = 1 },
                        role = Role.RadioButton
                    )
                ) {
                    RadioButton(
                        selected = mode == 1,
                        onClick = null
                    )
                    Text(text = "Crear nueva asignatura")
                }

                if (mode == 1) {
                    OutlinedTextField(
                        value = newAsignaturaName,
                        onValueChange = { newAsignaturaName = it },
                        label = { Text("Nombre de la Asignatura") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 32.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selector opcional de Docente
                Text(
                    text = "Docente a cargo (Opcional):",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                ExposedDropdownMenuBox(
                    expanded = docenteDropdownExpanded,
                    onExpandedChange = { docenteDropdownExpanded = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                ) {
                    val selectedDocente = availableDocentes.find { it.id == selectedDocenteId }
                    val selectedText = selectedDocente?.let { "${it.nombreCompleto} (@${it.nombreUsuario})" } ?: "Sin docente asignado"
                    OutlinedTextField(
                        value = selectedText,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Docente responsable") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = docenteDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = docenteDropdownExpanded,
                        onDismissRequest = { docenteDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sin docente asignado") },
                            onClick = {
                                selectedDocenteId = null
                                docenteDropdownExpanded = false
                            }
                        )
                        availableDocentes.forEach { docente ->
                            DropdownMenuItem(
                                text = { Text("${docente.nombreCompleto} (@${docente.nombreUsuario})") },
                                onClick = {
                                    selectedDocenteId = docente.id
                                    docenteDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (mode == 0 && selectedAsignaturaId != null) {
                        onAddExisting(selectedAsignaturaId!!, selectedDocenteId)
                    } else if (mode == 1 && newAsignaturaName.isNotBlank()) {
                        onCreateNew(newAsignaturaName.trim(), selectedDocenteId)
                    }
                },
                enabled = (mode == 0 && selectedAsignaturaId != null && availableAsignaturas.isNotEmpty()) ||
                          (mode == 1 && newAsignaturaName.isNotBlank())
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignDocenteToAsignaturaDialog(
    asignaturaNombre: String,
    availableDocentes: List<UsuarioEntity>,
    currentDocenteId: Long?,
    onDismiss: () -> Unit,
    onAssignDocente: (Long?) -> Unit
) {
    var selectedDocenteId by remember { mutableStateOf<Long?>(currentDocenteId) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Asignar Docente") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Selecciona el docente responsable de la asignatura \"$asignaturaNombre\":",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val selectedDocente = availableDocentes.find { it.id == selectedDocenteId }
                    val selectedText = selectedDocente?.let { "${it.nombreCompleto} (@${it.nombreUsuario})" } ?: "Sin docente asignado"
                    OutlinedTextField(
                        value = selectedText,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Docente a cargo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Sin docente asignado") },
                            onClick = {
                                selectedDocenteId = null
                                dropdownExpanded = false
                            }
                        )
                        availableDocentes.forEach { docente ->
                            DropdownMenuItem(
                                text = { Text("${docente.nombreCompleto} (@${docente.nombreUsuario})") },
                                onClick = {
                                    selectedDocenteId = docente.id
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAssignDocente(selectedDocenteId)
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddUserDialog(
    title: String,
    subtitle: String,
    availableUsers: List<UsuarioEntity>,
    onDismiss: () -> Unit,
    onAddUser: (Long) -> Unit
) {
    var selectedUserId by remember { mutableStateOf<Long?>(availableUsers.firstOrNull()?.id) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (availableUsers.isEmpty()) {
                    Text(
                        text = "No hay usuarios disponibles para asignar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val selectedUser = availableUsers.find { it.id == selectedUserId }
                        val selectedText = selectedUser?.let { "${it.nombreCompleto} (@${it.nombreUsuario})" } ?: "Seleccionar..."
                        OutlinedTextField(
                            value = selectedText,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Usuario") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            availableUsers.forEach { user ->
                                DropdownMenuItem(
                                    text = { Text("${user.nombreCompleto} (@${user.nombreUsuario})") },
                                    onClick = {
                                        selectedUserId = user.id
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedUserId?.let { onAddUser(it) }
                },
                enabled = selectedUserId != null && availableUsers.isNotEmpty()
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
