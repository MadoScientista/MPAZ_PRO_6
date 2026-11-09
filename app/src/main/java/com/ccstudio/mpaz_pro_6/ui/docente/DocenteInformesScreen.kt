package com.ccstudio.mpaz_pro_6.ui.docente

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ccstudio.mpaz_pro_6.data.local.dao.AsignaturaCursoDetalle
import com.ccstudio.mpaz_pro_6.data.local.dao.InformePedagogicoDetalle
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.UnidadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity
import com.ccstudio.mpaz_pro_6.ui.components.AppDatePickerDialog
import com.ccstudio.mpaz_pro_6.ui.components.AppTimePickerDialog
import com.ccstudio.mpaz_pro_6.ui.components.StatusChip
import com.ccstudio.mpaz_pro_6.ui.navigation.fadeThroughContent
import com.ccstudio.mpaz_pro_6.ui.util.formatFecha
import com.ccstudio.mpaz_pro_6.ui.util.formatFechaHoraInputSegundos
import com.ccstudio.mpaz_pro_6.ui.util.formatFechaInput
import com.ccstudio.mpaz_pro_6.ui.util.parseFecha
import com.ccstudio.mpaz_pro_6.ui.util.parseFechaHora
import java.time.LocalDateTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocenteInformesScreen(
    modifier: Modifier = Modifier,
    viewModel: InformesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.errorMessage, uiState.successMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título de sección
        Text(
            text = "Informes Pedagógicos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Gestión y emisión de consolidados de rendimiento por unidad y objetivo de aprendizaje.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Pestañas
        PrimaryTabRow(selectedTabIndex = uiState.selectedTab) {
            Tab(
                selected = uiState.selectedTab == 0,
                onClick = { viewModel.setTab(0) },
                text = { Text("Informes Emitidos (${uiState.informesEmitidos.size})") },
                icon = { Icon(Icons.Default.Assessment, contentDescription = null) }
            )
            Tab(
                selected = uiState.selectedTab == 1,
                onClick = { viewModel.setTab(1) },
                text = { Text("Generar Nuevo Informe") },
                icon = { Icon(Icons.Default.AddCircle, contentDescription = null) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            AnimatedContent(
                targetState = uiState.selectedTab,
                transitionSpec = { fadeThroughContent() },
                modifier = Modifier.fillMaxSize(),
                label = "informesTabs"
            ) { tab ->
                when (tab) {
                    0 -> InformesEmitidosView(
                        informes = uiState.informesEmitidos,
                        onVerDetalle = { viewModel.verDetalleInforme(it) },
                        onEliminar = { viewModel.eliminarInforme(it.informe.id) }
                    )
                    1 -> GenerarInformeView(
                        uiState = uiState,
                        onCursoSelected = { viewModel.onCursoSelected(it) },
                        onEstudianteSelected = { viewModel.onEstudianteSelected(it) },
                        onUnidadSelected = { viewModel.onUnidadSelected(it) },
                        onFechaCorteChanged = { viewModel.setFechaCorte(it) },
                        onFechaEmisionChanged = { viewModel.setFechaEmision(it) },
                        onDescripcionChanged = { viewModel.setDescripcion(it) },
                        onGenerarPreview = { viewModel.generarVistaPrevia() },
                        onEmitirInforme = { viewModel.emitirInformeGenerado() }
                    )
                }
            }
        }
    }

    // Diálogo de Detalle del Informe Emitido
    if (uiState.showDetalleDialog && uiState.selectedInformeDetalle != null) {
        val detalle = uiState.selectedInformeDetalle!!
        AlertDialog(
            onDismissRequest = { viewModel.cerrarDetalleDialog() },
            title = {
                Text(
                    text = "Informe Pedagógico #${detalle.informe.id}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    item {
                        Column {
                            Text(
                                text = "Estudiante: ${detalle.estudiante.nombreCompleto}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Unidad: ${detalle.unidad.nombre} (${detalle.asignatura.nombre})",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                StatusChip(
                                    statusName = if (detalle.informe.origen == "AUTOMATICO") "AUTOMÁTICO" else "MANUAL"
                                )
                                Text(
                                    text = "Emisión: ${formatFecha(detalle.informe.fecha)}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (!detalle.informe.descripcion.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Nota: ${detalle.informe.descripcion}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (detalle.informe.fechaCorte.isNotBlank()) {
                                Text(
                                    text = "Corte Técnico: ${formatFecha(detalle.informe.fechaCorte)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Desglose por Objetivos de Aprendizaje",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    items(uiState.selectedInformeOAs) { oaDetalle ->
                        val corr = oaDetalle.detalle.correctas
                        val inc = oaDetalle.detalle.incorrectas
                        val total = corr + inc
                        val pct = if (total > 0) (corr.toDouble() / total) * 100.0 else 0.0

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = oaDetalle.objetivo.codigo,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = oaDetalle.objetivo.descripcion,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Correctas: $corr | Incorrectas: $inc",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "%.1f%%".format(pct),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (pct >= 60.0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { (pct / 100.0).toFloat() },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = if (pct >= 60.0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.cerrarDetalleDialog() }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
fun InformesEmitidosView(
    informes: List<InformePedagogicoDetalle>,
    onVerDetalle: (InformePedagogicoDetalle) -> Unit,
    onEliminar: (InformePedagogicoDetalle) -> Unit
) {
    if (informes.isEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No hay informes pedagógicos emitidos.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Vaya a la pestaña 'Generar Nuevo Informe' para calcular y guardar un nuevo reporte.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(informes) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.estudiante.nombreCompleto,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${item.unidad.nombre} • ${item.asignatura.nombre}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            StatusChip(
                                statusName = if (item.informe.origen == "AUTOMATICO") "AUTOMÁTICO" else "MANUAL"
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Fecha de Emisión: ${formatFecha(item.informe.fecha)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (item.informe.fechaCorte.isNotBlank()) {
                            Text(
                                text = "Corte Técnico: ${formatFecha(item.informe.fechaCorte)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (!item.informe.descripcion.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.informe.descripcion,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = { onVerDetalle(item) }
                            ) {
                                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Ver Desglose")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(onClick = { onEliminar(item) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GenerarInformeView(
    uiState: InformesUiState,
    onCursoSelected: (Long) -> Unit,
    onEstudianteSelected: (Long) -> Unit,
    onUnidadSelected: (Long) -> Unit,
    onFechaCorteChanged: (String) -> Unit,
    onFechaEmisionChanged: (String) -> Unit,
    onDescripcionChanged: (String) -> Unit,
    onGenerarPreview: () -> Unit,
    onEmitirInforme: () -> Unit
) {
    var showFechaCorteDatePicker by remember { mutableStateOf(false) }
    var showFechaCorteTimePicker by remember { mutableStateOf(false) }
    var showFechaEmisionDatePicker by remember { mutableStateOf(false) }

    if (showFechaCorteDatePicker) {
        AppDatePickerDialog(
            initialDate = parseFechaHora(uiState.fechaCorte)?.toLocalDate(),
            onDateSelected = { date ->
                val current = parseFechaHora(uiState.fechaCorte) ?: LocalDateTime.now()
                onFechaCorteChanged(
                    formatFechaHoraInputSegundos(LocalDateTime.of(date, current.toLocalTime()))
                )
            },
            onDismiss = { showFechaCorteDatePicker = false }
        )
    }

    if (showFechaCorteTimePicker) {
        AppTimePickerDialog(
            initialTime = parseFechaHora(uiState.fechaCorte)?.toLocalTime() ?: LocalTime.now(),
            onTimeSelected = { time ->
                val current = parseFechaHora(uiState.fechaCorte) ?: LocalDateTime.now()
                onFechaCorteChanged(
                    formatFechaHoraInputSegundos(LocalDateTime.of(current.toLocalDate(), time))
                )
            },
            onDismiss = { showFechaCorteTimePicker = false }
        )
    }

    if (showFechaEmisionDatePicker) {
        AppDatePickerDialog(
            initialDate = parseFecha(uiState.fechaEmision),
            onDateSelected = { date -> onFechaEmisionChanged(formatFechaInput(date)) },
            onDismiss = { showFechaEmisionDatePicker = false }
        )
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Selección de Parámetros",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Dropdown Curso / Asignatura
                    DropdownSelector(
                        label = "Curso y Asignatura",
                        items = uiState.cursosAsignados,
                        selectedId = uiState.selectedCursoId,
                        itemLabel = { "${it.nombreCompletoCurso} - ${it.asignatura.nombre}" },
                        itemId = { it.curso.id },
                        onSelected = { onCursoSelected(it) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dropdown Estudiante
                    DropdownSelector(
                        label = "Estudiante",
                        items = uiState.estudiantesCurso,
                        selectedId = uiState.selectedEstudianteId,
                        itemLabel = { "${it.pnombre} ${it.apaterno} (${it.nombreUsuario})" },
                        itemId = { it.id },
                        onSelected = { onEstudianteSelected(it) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dropdown Unidad
                    DropdownSelector(
                        label = "Unidad Académica",
                        items = uiState.unidadesCurso,
                        selectedId = uiState.selectedUnidadId,
                        itemLabel = { it.nombre },
                        itemId = { it.id },
                        onSelected = { onUnidadSelected(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = uiState.fechaCorte,
                        onValueChange = onFechaCorteChanged,
                        label = { Text("Fecha de corte") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            Row {
                                IconButton(onClick = { showFechaCorteDatePicker = true }) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Seleccionar fecha de corte"
                                    )
                                }
                                IconButton(onClick = { showFechaCorteTimePicker = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Seleccionar hora de corte"
                                    )
                                }
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onGenerarPreview,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isCalculatingPreview && uiState.selectedEstudianteId != null && uiState.selectedUnidadId != null
                    ) {
                        if (uiState.isCalculatingPreview) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Calculando Resultados...")
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generar Vista Previa del Informe")
                        }
                    }
                }
            }
        }

        val preliminar = uiState.preliminarResponse
        if (preliminar != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "2. Consolidado Preliminar Evaluado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Estudiante: ${preliminar.usuario.nombreCompleto}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Unidad: ${preliminar.unidad.nombre}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Actividades Evaluadas: ${preliminar.actividades.size}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            val totalG = preliminar.totalCorrectas + preliminar.totalIncorrectas
                            val pctG = if (totalG > 0) (preliminar.totalCorrectas.toDouble() / totalG) * 100.0 else 0.0

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Rendimiento Global",
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = "%.1f%%".format(pctG),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "3. Desglose por Objetivos de Aprendizaje",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            items(preliminar.detalles) { detalle ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = detalle.objetivoAprendizaje.codigo,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = detalle.objetivoAprendizaje.descripcion,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Correctas: ${detalle.correctas} | Incorrectas: ${detalle.incorrectas} (Total: ${detalle.total})",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "%.1f%%".format(detalle.porcentaje),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (detalle.porcentaje >= 60.0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { (detalle.porcentaje / 100.0).toFloat() },
                            modifier = Modifier.fillMaxWidth(),
                            color = if (detalle.porcentaje >= 60.0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "4. Emisión de Informe Snapshot",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.fechaEmision,
                            onValueChange = onFechaEmisionChanged,
                            label = { Text("Fecha de Emisión") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            trailingIcon = {
                                IconButton(onClick = { showFechaEmisionDatePicker = true }) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = "Seleccionar fecha de emisión"
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = uiState.descripcion,
                            onValueChange = onDescripcionChanged,
                            label = { Text("Observaciones / Descripción pedagógica") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onEmitirInforme,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Emitir y Guardar Informe Pedagógico")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownSelector(
    label: String,
    items: List<T>,
    selectedId: Long?,
    itemLabel: (T) -> String,
    itemId: (T) -> Long,
    onSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItem = items.find { itemId(it) == selectedId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedItem?.let { itemLabel(it) } ?: "Seleccionar...",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(itemLabel(item)) },
                    onClick = {
                        onSelected(itemId(item))
                        expanded = false
                    }
                )
            }
        }
    }
}
