package com.ccstudio.mpaz_pro_6.ui.docente

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.TipoActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.UnidadEntity
import com.ccstudio.mpaz_pro_6.ui.components.MarkdownRenderer

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CrearContenidoScreen(
    modifier: Modifier = Modifier,
    viewModel: CrearContenidoViewModel = viewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    var showConfirmCrearDialog by remember { mutableStateOf(false) }
    var showConfirmLimpiarDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.initForm()
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(state.successMessage) {
        state.successMessage?.let { success ->
            Toast.makeText(context, success, Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher para importar archivos Markdown
    val markdownFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let { fileUri ->
            try {
                val inputStream = context.contentResolver.openInputStream(fileUri)
                val text = inputStream?.bufferedReader()?.use { it.readText() } ?: ""
                if (text.isNotBlank()) {
                    viewModel.updateDescripcionMarkdown(text)
                    Toast.makeText(context, "Markdown importado correctamente", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error al leer el archivo: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (state.isLoading && state.unidadesDisponibles.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Título principal
                Text(
                    text = "Crear Nuevo Contenido",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )

                // 1. Selección de Unidad
                Text(
                    text = "1. Seleccionar Unidad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                var unidadMenuExpanded by remember { mutableStateOf(false) }

                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = state.unidadSeleccionada?.nombre ?: "Seleccionar unidad...",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { unidadMenuExpanded = true },
                        trailingIcon = {
                            IconButton(onClick = { unidadMenuExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Desplegar")
                            }
                        },
                        label = { Text("Unidad Académica") },
                    )

                    DropdownMenu(
                        expanded = unidadMenuExpanded,
                        onDismissRequest = { unidadMenuExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f),
                    ) {
                        if (state.unidadesDisponibles.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No hay unidades disponibles") },
                                onClick = { unidadMenuExpanded = false },
                            )
                        } else {
                            state.unidadesDisponibles.forEach { unidad ->
                                DropdownMenuItem(
                                    text = { Text(unidad.nombre) },
                                    onClick = {
                                        viewModel.onUnidadSelected(unidad)
                                        unidadMenuExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }

                // 2. Selección de Objetivos de Aprendizaje (OAs)
                if (state.unidadSeleccionada != null) {
                    Text(
                        text = "2. Objetivos de Aprendizaje (OAs)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    if (state.objetivosUnidad.isEmpty()) {
                        Text(
                            text = "No hay OAs vinculados a esta unidad.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline,
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            state.objetivosUnidad.forEach { oa ->
                                val selected = state.objetivosSeleccionadosIds.contains(oa.id)
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.toggleObjetivoSeleccionado(oa.id) },
                                    label = { Text("${oa.codigo}: ${oa.descripcion}") },
                                    leadingIcon = if (selected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else null,
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()

                // 3. Editor de Descripción en Markdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "3. Descripción del Contenido (Markdown)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )

                    Row {
                        IconButton(onClick = { markdownFileLauncher.launch("*/*") }) {
                            Icon(Icons.Default.FileOpen, contentDescription = "Importar Markdown")
                        }
                        IconButton(onClick = { viewModel.togglePreviewMode() }) {
                            Icon(
                                imageVector = if (state.isPreviewMode) Icons.Default.Description else Icons.Default.Preview,
                                contentDescription = "Modo Vista Previa",
                                tint = if (state.isPreviewMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                if (state.isPreviewMode) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        Box(modifier = Modifier.padding(16.dp)) {
                            if (state.descripcionMarkdown.isBlank()) {
                                Text("Vista previa vacía", fontStyle = FontStyle.Italic)
                            } else {
                                MarkdownRenderer(markdownText = state.descripcionMarkdown)
                            }
                        }
                    }
                } else {
                    OutlinedTextField(
                        value = state.descripcionMarkdown,
                        onValueChange = { viewModel.updateDescripcionMarkdown(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 160.dp),
                        placeholder = { Text("Escribe la descripción del contenido aquí (Soporta sintaxis Markdown)...") },
                        label = { Text("Contenido Markdown") },
                    )
                }

                HorizontalDivider()

                // 4. Gestión de Actividades
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "4. Actividades (${state.actividadesAsociadas.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        onClick = { viewModel.openAsociarDialog() },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Asociar Creada")
                    }

                    Button(
                        onClick = { viewModel.openCrearActividadDialog() },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Crear Actividad")
                    }
                }

                // Lista de actividades agregadas al contenido
                if (state.actividadesAsociadas.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Aún no has asociado ni creado actividades para este contenido.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.actividadesAsociadas.forEach { act ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (act.idExistente != null) act.resumenVisual else "[NUEVA ACTIVIDAD] - ${act.tipoActividadNombre}",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = act.instrucciones.ifBlank { "Sin instrucciones especificadas" },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                        )
                                        if (act.preguntas.isNotEmpty()) {
                                            Text(
                                                text = "${act.preguntas.size} pregunta(s) agregada(s)",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                            )
                                        }
                                    }

                                    IconButton(onClick = { viewModel.eliminarActividadAsociada(act.idTemp) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón Crear Contenido
                Button(
                    onClick = { showConfirmCrearDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !state.isLoading && state.unidadSeleccionada != null && state.descripcionMarkdown.isNotBlank(),
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp),
                        )
                    } else {
                        Text(
                            text = "Crear Contenido",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                // Botón Limpiar Campos
                OutlinedButton(
                    onClick = { showConfirmLimpiarDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !state.isLoading && (state.unidadSeleccionada != null || state.descripcionMarkdown.isNotBlank() || state.actividadesAsociadas.isNotEmpty() || state.objetivosSeleccionadosIds.isNotEmpty()),
                ) {
                    Text(
                        text = "Limpiar Campos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Diálogo de Confirmación: Crear Contenido
    if (showConfirmCrearDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmCrearDialog = false },
            title = { Text("Confirmar Creación de Contenido", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas crear este contenido con las actividades y Objetivos de Aprendizaje seleccionados?") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmCrearDialog = false
                        viewModel.guardarContenido {
                            Toast.makeText(context, "¡Contenido creado exitosamente!", Toast.LENGTH_SHORT).show()
                        }
                    },
                ) {
                    Text("Sí, Crear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmCrearDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    // Diálogo de Confirmación: Limpiar Campos
    if (showConfirmLimpiarDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmLimpiarDialog = false },
            title = { Text("Confirmar Limpieza de Campos", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas borrar todos los campos del formulario? Se perderán los datos ingresados no guardados.") },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmLimpiarDialog = false
                        viewModel.limpiarFormulario()
                        Toast.makeText(context, "Formulario limpiado", Toast.LENGTH_SHORT).show()
                    },
                ) {
                    Text("Sí, Limpiar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmLimpiarDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    // Diálogo 1: Asociar Actividad Existente
    if (state.isAsociarDialogVisible) {
        AsociarActividadDialog(
            actividadesExistentes = state.actividadesExistentesDisponibles,
            onDismiss = { viewModel.closeAsociarDialog() },
            onSelectActividad = { item -> viewModel.asociarActividadExistente(item) },
        )
    }

    // Diálogo 2: Crear Nueva Actividad
    if (state.isCrearActividadDialogVisible) {
        CrearNuevaActividadDialog(
            tiposActividad = state.tiposActividad,
            onDismiss = { viewModel.closeCrearActividadDialog() },
            onConfirm = { nuevaAct -> viewModel.agregarNuevaActividad(nuevaAct) },
        )
    }
}

// Diálogo Selector de Actividades Existentes Enriquecidas
@Composable
private fun AsociarActividadDialog(
    actividadesExistentes: List<ActividadSeleccionableItem>,
    onDismiss: () -> Unit,
    onSelectActividad: (ActividadSeleccionableItem) -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Asociar Actividad Existente",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            if (actividadesExistentes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No se encontraron actividades válidas creadas previamente para asociar.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    items(actividadesExistentes) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectActividad(item) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            ),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Surface(
                                        shape = MaterialTheme.shapes.small,
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                    ) {
                                        Text(
                                            text = item.actividadConTipo.tipoActividad.nombre,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        )
                                    }

                                    Text(
                                        text = "${item.totalPreguntas} preguntas",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.outline,
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Origen: ${item.nombreUnidad}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                val extractoPregunta = if (item.primerEnunciado != null) {
                                    "P1: ${item.primerEnunciado}"
                                } else {
                                    "sin preguntas"
                                }

                                Text(
                                    text = extractoPregunta,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontStyle = if (item.primerEnunciado == null) FontStyle.Italic else FontStyle.Normal,
                                    color = if (item.primerEnunciado == null) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    )
}

// Diálogo Formulario para Crear una Nueva Actividad
@Composable
private fun CrearNuevaActividadDialog(
    tiposActividad: List<TipoActividadEntity>,
    onDismiss: () -> Unit,
    onConfirm: (ActividadCreacionTemp) -> Unit,
) {
    var tipoSeleccionado by remember { mutableStateOf(tiposActividad.firstOrNull() ?: TipoActividadEntity(1, "ACTIVIDAD")) }
    var tipoMenuExpanded by remember { mutableStateOf(false) }
    var instrucciones by remember { mutableStateOf("") }

    val preguntasList = remember { mutableStateListOf<PreguntaCreacion>() }

    var nuevoEnunciado by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear Nueva Actividad", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Selector de Tipo de Actividad
                Text("Tipo de Actividad", style = MaterialTheme.typography.labelLarge)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = tipoSeleccionado.nombre,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { tipoMenuExpanded = true },
                        trailingIcon = {
                            IconButton(onClick = { tipoMenuExpanded = true }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                    )

                    DropdownMenu(
                        expanded = tipoMenuExpanded,
                        onDismissRequest = { tipoMenuExpanded = false },
                    ) {
                        tiposActividad.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo.nombre) },
                                onClick = {
                                    tipoSeleccionado = tipo
                                    tipoMenuExpanded = false
                                },
                            )
                        }
                    }
                }

                // Instrucciones
                OutlinedTextField(
                    value = instrucciones,
                    onValueChange = { instrucciones = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Instrucciones") },
                    placeholder = { Text("Ej: Lee atentamente y responde...") },
                )

                HorizontalDivider()

                // Formulario de Preguntas
                Text("Preguntas (${preguntasList.size})", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    preguntasList.forEachIndexed { pIndex, p ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text("P${pIndex + 1}: ${p.enunciado}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    IconButton(onClick = { preguntasList.removeAt(pIndex) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar pregunta", tint = MaterialTheme.colorScheme.error)
                                    }
                                }

                                // Alternativas creadas para esta pregunta
                                p.alternativas.forEach { alt ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(selected = alt.correcta, onClick = null)
                                        Text("${alt.enunciado}${if (alt.correcta) " (Correcta)" else ""}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }

                // Añadir Pregunta Rápida
                OutlinedTextField(
                    value = nuevoEnunciado,
                    onValueChange = { nuevoEnunciado = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nueva Pregunta") },
                    placeholder = { Text("Enunciado de la pregunta...") },
                )

                Button(
                    onClick = {
                        if (nuevoEnunciado.isNotBlank()) {
                            preguntasList.add(
                                PreguntaCreacion(
                                    enunciado = nuevoEnunciado,
                                    alternativas = listOf(
                                        AlternativaCreacion(enunciado = "Opción A", correcta = true),
                                        AlternativaCreacion(enunciado = "Opción B", correcta = false),
                                    ),
                                ),
                            )
                            nuevoEnunciado = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = nuevoEnunciado.isNotBlank(),
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Agregar Pregunta (con opciones A y B)")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(
                        ActividadCreacionTemp(
                            tipoActividadId = tipoSeleccionado.id,
                            tipoActividadNombre = tipoSeleccionado.nombre,
                            instrucciones = instrucciones,
                            preguntas = preguntasList.toList(),
                            resumenVisual = "[NUEVA] ${tipoSeleccionado.nombre} (${preguntasList.size} preguntas)",
                        ),
                    )
                },
            ) {
                Text("Guardar Actividad")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    )
}
