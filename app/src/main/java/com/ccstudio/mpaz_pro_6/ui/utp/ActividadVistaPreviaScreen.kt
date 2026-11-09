package com.ccstudio.mpaz_pro_6.ui.utp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.local.dao.ActividadCursoConDetalles
import com.ccstudio.mpaz_pro_6.data.local.dao.PreguntaConAlternativas
import com.ccstudio.mpaz_pro_6.ui.components.AppDatePickerDialog
import com.ccstudio.mpaz_pro_6.ui.components.AppPillButton
import com.ccstudio.mpaz_pro_6.ui.components.AppSwitch
import com.ccstudio.mpaz_pro_6.ui.components.AppTimePickerDialog
import com.ccstudio.mpaz_pro_6.ui.components.QuestionCard
import com.ccstudio.mpaz_pro_6.ui.components.StatusChip
import com.ccstudio.mpaz_pro_6.ui.util.formatFecha
import com.ccstudio.mpaz_pro_6.ui.util.formatFechaHoraInput
import com.ccstudio.mpaz_pro_6.ui.util.parseFechaHora
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun ActividadVistaPreviaScreen(
    actividadId: Long,
    cursoId: Long = 0L,
    viewModel: UtpViewModel,
    rol: String = "UTP",
    modifier: Modifier = Modifier
) {
    val state by viewModel.activityPreviewState.collectAsState()
    val isDocente = rol.equals("DOCENTE", ignoreCase = true)

    var showConfigDialog by remember { mutableStateOf(false) }
    var showAddQuestionDialog by remember { mutableStateOf(false) }
    var selectedPreguntaForEdit by remember { mutableStateOf<PreguntaConAlternativas?>(null) }

    LaunchedEffect(actividadId, cursoId) {
        viewModel.loadActivityPreview(actividadId, cursoId)
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

    val actividadConTipo = state.actividadConTipo
    if (actividadConTipo == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Actividad no encontrada")
        }
        return
    }

    val actividad = actividadConTipo.actividad
    val tipo = actividadConTipo.tipoActividad
    val estado = actividadConTipo.estado
    val unidadConDetalles = state.unidadConDetalles
    val cursoConDetalles = state.cursoConDetalles

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner de Vista Previa
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 10.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Vista Previa de la Actividad (Modo $rol)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Text(
                        text = "Se muestran las alternativas correctas destacadas para revisión pedagógica.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        // Header de la Actividad con Opción de Edición
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 1. Estado y botón de editar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusChip(statusName = estado.nombre)
                    if (isDocente) {
                        Button(
                            onClick = { showConfigDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text("Editar Actividad")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Curso
                if (cursoConDetalles != null) {
                    Text(
                        text = cursoConDetalles.nombreCompletoConAnnio,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // 3. Unidad / Contenido
                if (unidadConDetalles != null) {
                    Text(
                        text = unidadConDetalles.unidad.nombre,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // 4. Tipo de Actividad
                Text(
                    text = tipo.nombre,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 5. Instrucciones
                Text(
                    text = "Instrucciones",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = actividad.instrucciones,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // Encabezado de Preguntas + Botón Agregar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Preguntas (${state.preguntas.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            if (isDocente) {
                OutlinedButton(
                    onClick = { showAddQuestionDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("Agregar Pregunta")
                }
            }
        }

        if (state.preguntas.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "No hay preguntas asociadas a esta actividad.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                state.preguntas.forEachIndexed { index, preguntaConAlternativas ->
                    QuestionCard(
                        index = index + 1,
                        preguntaConAlternativas = preguntaConAlternativas,
                        isPreviewMode = true,
                        onEditClick = if (isDocente) {
                            { selectedPreguntaForEdit = preguntaConAlternativas }
                        } else null
                    )
                }
            }
        }
    }

    // --- DIÁLOGOS DE EDICIÓN ---

    // 1. Diálogo de Configuración de la Actividad (Visibilidad, Habilitada, Intentos, Fechas e Instrucciones)
    if (showConfigDialog) {
        EditarConfiguracionActividadDialog(
            instruccionesActuales = state.actividadConTipo?.actividad?.instrucciones ?: "",
            actividadesCurso = state.actividadesCurso,
            onDismiss = { showConfigDialog = false },
            onSaveInstrucciones = { nuevasInstrucciones ->
                viewModel.updateActividadInstrucciones(actividadId, nuevasInstrucciones)
            },
            onSaveCursoConfig = { actividadCursoId, visible, habilitada, maxIntentos, fechaInicio, fechaFin ->
                viewModel.updateActividadCursoConfig(
                    actividadCursoId = actividadCursoId,
                    visible = visible,
                    habilitada = habilitada,
                    maxIntentos = maxIntentos,
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin
                )
            }
        )
    }

    // 2. Diálogo para Agregar Nueva Pregunta
    if (showAddQuestionDialog) {
        AgregarPreguntaDialog(
            onDismiss = { showAddQuestionDialog = false },
            onConfirm = { enunciado, alternativas, correctaIndex ->
                viewModel.addPreguntaToActividad(
                    actividadId = actividadId,
                    enunciado = enunciado,
                    alternativasTexto = alternativas,
                    correctaIndex = correctaIndex
                )
                showAddQuestionDialog = false
            }
        )
    }

    // 3. Diálogo para Editar Pregunta y Alternativas
    val preguntaAEditar = selectedPreguntaForEdit
    if (preguntaAEditar != null) {
        EditarPreguntaDialog(
            preguntaConAlternativas = preguntaAEditar,
            onDismiss = { selectedPreguntaForEdit = null },
            onUpdateEnunciado = { nuevoEnunciado ->
                viewModel.updatePreguntaEnunciado(actividadId, preguntaAEditar.pregunta.id, nuevoEnunciado)
            },
            onUpdateAlternativaEnunciado = { altId, nuevoText ->
                viewModel.updateAlternativaEnunciado(actividadId, altId, nuevoText)
            },
            onSetCorrecta = { altId ->
                viewModel.setAlternativaCorrecta(actividadId, preguntaAEditar.pregunta.id, altId)
            },
            onAddAlternativa = { text, correcta ->
                viewModel.addAlternativaToPregunta(actividadId, preguntaAEditar.pregunta.id, text, correcta)
            },
            onDeleteAlternativa = { altId ->
                viewModel.deleteAlternativaFromPregunta(actividadId, preguntaAEditar.pregunta.id, altId)
            },
            onDeletePregunta = {
                viewModel.deletePreguntaFromActividad(actividadId, preguntaAEditar.pregunta.id)
                selectedPreguntaForEdit = null
            }
        )
    }
}

// ==========================================
// COMPONENTES AUXILIARES DE DIÁLOGOS
// ==========================================

@Composable
private fun EditarConfiguracionActividadDialog(
    instruccionesActuales: String,
    actividadesCurso: List<ActividadCursoConDetalles>,
    onDismiss: () -> Unit,
    onSaveInstrucciones: (String) -> Unit,
    onSaveCursoConfig: (Long, Boolean, Boolean, Int?, String?, String?) -> Unit
) {
    var instrucciones by remember { mutableStateOf(instruccionesActuales) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edición de Configuración de Actividad") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sección Instrucciones
                Text(
                    text = "Instrucciones de la Actividad",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                OutlinedTextField(
                    value = instrucciones,
                    onValueChange = { instrucciones = it },
                    label = { Text("Instrucciones") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                HorizontalDivider()

                // Configuración por Curso (Visibilidad, Habilitada, Intentos, Fechas)
                Text(
                    text = "Configuración por Curso (${actividadesCurso.size})",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                if (actividadesCurso.isEmpty()) {
                    Text(
                        text = "No se encontraron asignaciones de curso para esta actividad.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    actividadesCurso.forEach { item ->
                        CursoConfigCardItem(
                            item = item,
                            onSave = { visible, habilitada, maxIntentos, fechaInicio, fechaFin ->
                                onSaveCursoConfig(
                                    item.actividadCurso.id,
                                    visible,
                                    habilitada,
                                    maxIntentos,
                                    fechaInicio,
                                    fechaFin
                                )
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSaveInstrucciones(instrucciones)
                    onDismiss()
                }
            ) {
                Text("Guardar y Cerrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun CursoConfigCardItem(
    item: ActividadCursoConDetalles,
    onSave: (Boolean, Boolean, Int?, String?, String?) -> Unit
) {
    val ac = item.actividadCurso
    val curso = item.curso

    var visible by remember(ac) { mutableStateOf(ac.visible) }
    var habilitada by remember(ac) { mutableStateOf(ac.habilitada) }
    var maxIntentosStr by remember(ac) { mutableStateOf(ac.maxIntentos?.toString() ?: "") }
    var fechaInicio by remember(ac) { mutableStateOf(ac.fechaInicio ?: "") }
    var fechaFin by remember(ac) { mutableStateOf(ac.fechaFin ?: "") }
    var showFechaInicioDatePicker by remember { mutableStateOf(false) }
    var showFechaInicioTimePicker by remember { mutableStateOf(false) }
    var showFechaFinDatePicker by remember { mutableStateOf(false) }
    var showFechaFinTimePicker by remember { mutableStateOf(false) }

    fun updateFechas(nuevoInicio: String = fechaInicio, nuevoFin: String = fechaFin) {
        fechaInicio = nuevoInicio
        fechaFin = nuevoFin
        onSave(visible, habilitada, maxIntentosStr.toIntOrNull(), nuevoInicio, nuevoFin)
    }

    if (showFechaInicioDatePicker) {
        AppDatePickerDialog(
            initialDate = parseFechaHora(fechaInicio)?.toLocalDate(),
            onDateSelected = { date ->
                val current = parseFechaHora(fechaInicio) ?: LocalDateTime.now()
                updateFechas(
                    nuevoInicio = formatFechaHoraInput(LocalDateTime.of(date, current.toLocalTime()))
                )
            },
            onDismiss = { showFechaInicioDatePicker = false }
        )
    }

    if (showFechaInicioTimePicker) {
        AppTimePickerDialog(
            initialTime = parseFechaHora(fechaInicio)?.toLocalTime() ?: LocalTime.now(),
            onTimeSelected = { time ->
                val current = parseFechaHora(fechaInicio) ?: LocalDateTime.now()
                updateFechas(
                    nuevoInicio = formatFechaHoraInput(LocalDateTime.of(current.toLocalDate(), time))
                )
            },
            onDismiss = { showFechaInicioTimePicker = false }
        )
    }

    if (showFechaFinDatePicker) {
        AppDatePickerDialog(
            initialDate = parseFechaHora(fechaFin)?.toLocalDate(),
            onDateSelected = { date ->
                val current = parseFechaHora(fechaFin) ?: LocalDateTime.now()
                updateFechas(
                    nuevoFin = formatFechaHoraInput(LocalDateTime.of(date, current.toLocalTime()))
                )
            },
            onDismiss = { showFechaFinDatePicker = false }
        )
    }

    if (showFechaFinTimePicker) {
        AppTimePickerDialog(
            initialTime = parseFechaHora(fechaFin)?.toLocalTime() ?: LocalTime.now(),
            onTimeSelected = { time ->
                val current = parseFechaHora(fechaFin) ?: LocalDateTime.now()
                updateFechas(
                    nuevoFin = formatFechaHoraInput(LocalDateTime.of(current.toLocalDate(), time))
                )
            },
            onDismiss = { showFechaFinTimePicker = false }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = curso.nombreCompletoConAnnio,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // Switch Visibilidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Visibilidad", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                    Text("Permite a los estudiantes ver la actividad", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                AppSwitch(
                    checked = visible,
                    onCheckedChange = {
                        visible = it
                        onSave(visible, habilitada, maxIntentosStr.toIntOrNull(), fechaInicio, fechaFin)
                    }
                )
            }

            // Switch Habilitada
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Estado Habilitado", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
                    Text("Permite responder los intentos", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                AppSwitch(
                    checked = habilitada,
                    onCheckedChange = {
                        habilitada = it
                        onSave(visible, habilitada, maxIntentosStr.toIntOrNull(), fechaInicio, fechaFin)
                    }
                )
            }

            // Intentos Máximos
            OutlinedTextField(
                value = maxIntentosStr,
                onValueChange = {
                    maxIntentosStr = it
                    onSave(visible, habilitada, it.toIntOrNull(), fechaInicio, fechaFin)
                },
                label = { Text("Intentos Máximos") },
                placeholder = { Text("Ej: 3 (vacío para sin límite)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Fecha Inicio
            OutlinedTextField(
                value = fechaInicio,
                onValueChange = { updateFechas(nuevoInicio = it) },
                label = { Text("Fecha de Inicio") },
                placeholder = { Text("YYYY-MM-DD HH:mm") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    Row {
                        IconButton(onClick = { showFechaInicioDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Seleccionar fecha de inicio"
                            )
                        }
                        IconButton(onClick = { showFechaInicioTimePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Seleccionar hora de inicio"
                            )
                        }
                    }
                }
            )

            // Fecha Fin
            OutlinedTextField(
                value = fechaFin,
                onValueChange = { updateFechas(nuevoFin = it) },
                label = { Text("Fecha de Fin") },
                placeholder = { Text("YYYY-MM-DD HH:mm") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    Row {
                        IconButton(onClick = { showFechaFinDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Seleccionar fecha de fin"
                            )
                        }
                        IconButton(onClick = { showFechaFinTimePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Seleccionar hora de fin"
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun AgregarPreguntaDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, List<String>, Int) -> Unit
) {
    var enunciado by remember { mutableStateOf("") }
    var altA by remember { mutableStateOf("") }
    var altB by remember { mutableStateOf("") }
    var altC by remember { mutableStateOf("") }
    var altD by remember { mutableStateOf("") }
    var correctaIndex by remember { mutableIntStateOf(0) } // 0=A, 1=B, 2=C, 3=D
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Nueva Pregunta") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                OutlinedTextField(
                    value = enunciado,
                    onValueChange = { enunciado = it; errorMessage = null },
                    label = { Text("Enunciado de la Pregunta") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Text(
                    text = "Alternativas y Selección de Correcta:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                val altList = listOf("A" to altA, "B" to altB, "C" to altC, "D" to altD)
                val setters = listOf(
                    { v: String -> altA = v },
                    { v: String -> altB = v },
                    { v: String -> altC = v },
                    { v: String -> altD = v }
                )

                altList.forEachIndexed { index, (label, valText) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = (correctaIndex == index),
                            onClick = { correctaIndex = index }
                        )
                        OutlinedTextField(
                            value = valText,
                            onValueChange = { setters[index](it); errorMessage = null },
                            label = { Text("Alternativa $label") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (enunciado.isBlank()) {
                        errorMessage = "El enunciado es obligatorio."
                        return@Button
                    }
                    if (altA.isBlank() || altB.isBlank()) {
                        errorMessage = "Debe haber al menos 2 alternativas completas (A y B)."
                        return@Button
                    }
                    val alternativas = listOf(altA.trim(), altB.trim(), altC.trim(), altD.trim())
                        .filter { it.isNotBlank() }
                    onConfirm(enunciado.trim(), alternativas, correctaIndex)
                }
            ) {
                Text("Guardar Pregunta")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun EditarPreguntaDialog(
    preguntaConAlternativas: PreguntaConAlternativas,
    onDismiss: () -> Unit,
    onUpdateEnunciado: (String) -> Unit,
    onUpdateAlternativaEnunciado: (Long, String) -> Unit,
    onSetCorrecta: (Long) -> Unit,
    onAddAlternativa: (String, Boolean) -> Unit,
    onDeleteAlternativa: (Long) -> Unit,
    onDeletePregunta: () -> Unit
) {
    var enunciado by remember(preguntaConAlternativas) {
        mutableStateOf(preguntaConAlternativas.pregunta.enunciado)
    }
    var showAddAltDialog by remember { mutableStateOf(false) }
    var showConfirmDeletePregunta by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Pregunta") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Enunciado
                OutlinedTextField(
                    value = enunciado,
                    onValueChange = {
                        enunciado = it
                        onUpdateEnunciado(it)
                    },
                    label = { Text("Enunciado de la Pregunta") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                HorizontalDivider()

                // Alternativas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Alternativas (Selecciona la correcta)",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    TextButton(onClick = { showAddAltDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                        Text("Agregar")
                    }
                }

                preguntaConAlternativas.alternativas.forEachIndexed { index, alt ->
                    var altText by remember(alt) { mutableStateOf(alt.enunciado) }
                    val optionLetter = ('A' + index).toString()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = alt.correcta,
                            onClick = { onSetCorrecta(alt.id) }
                        )
                        OutlinedTextField(
                            value = altText,
                            onValueChange = {
                                altText = it
                                onUpdateAlternativaEnunciado(alt.id, it)
                            },
                            label = { Text("Opción $optionLetter") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        if (preguntaConAlternativas.alternativas.size > 2) {
                            IconButton(onClick = { onDeleteAlternativa(alt.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar alternativa",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()

                // Eliminar Pregunta
                AppPillButton(
                    text = "Eliminar Pregunta",
                    onClick = { showConfirmDeletePregunta = true },
                    modifier = Modifier.align(Alignment.End),
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Listo")
            }
        }
    )

    // Sub-diálogo para agregar alternativa
    if (showAddAltDialog) {
        var nuevaAltText by remember { mutableStateOf("") }
        var esCorrecta by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddAltDialog = false },
            title = { Text("Agregar Alternativa") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nuevaAltText,
                        onValueChange = { nuevaAltText = it },
                        label = { Text("Texto de la Alternativa") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .toggleable(
                                value = esCorrecta,
                                onValueChange = { esCorrecta = it },
                                role = Role.Checkbox
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = esCorrecta,
                            onCheckedChange = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Marcar como alternativa correcta")
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nuevaAltText.isNotBlank()) {
                            onAddAlternativa(nuevaAltText.trim(), esCorrecta)
                            showAddAltDialog = false
                        }
                    }
                ) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAltDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Sub-diálogo para confirmar eliminación de pregunta
    if (showConfirmDeletePregunta) {
        AlertDialog(
            onDismissRequest = { showConfirmDeletePregunta = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar esta pregunta y sus alternativas?") },
            confirmButton = {
                AppPillButton(
                    text = "Eliminar",
                    onClick = {
                        showConfirmDeletePregunta = false
                        onDeletePregunta()
                    },
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDeletePregunta = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
