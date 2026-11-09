package com.ccstudio.mpaz_pro_6.ui.estudiante

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ccstudio.mpaz_pro_6.ui.theme.LocalAppColors
import com.ccstudio.mpaz_pro_6.ui.util.formatFecha

@Composable
fun EstudianteActividadesScreen(
    onIniciarIntento: (actividadId: Long, cursoId: Long, intentoId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EstudianteViewModel = viewModel()
) {
    val state by viewModel.actividadesState.collectAsState()
    val appColors = LocalAppColors.current

    var selectedItemForConfirm by remember { mutableStateOf<ActividadEstudianteItem?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadActividadesPendientesEstudiante()
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Actividades Pendientes y Disponibles (${state.actividades.size})",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        if (state.actividades.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tienes actividades pendientes en este momento.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(state.actividades) { item ->
                    ActividadCardEstudiante(
                        item = item,
                        onIniciarClick = { selectedItemForConfirm = item }
                    )
                }
            }
        }
    }

    // Diálogo de Confirmación de Inicio de Intento
    val itemToConfirm = selectedItemForConfirm
    if (itemToConfirm != null) {
        val actividad = itemToConfirm.actividadCursoConDetalles.actividad
        val cursoId = itemToConfirm.actividadCursoConDetalles.actividadCurso.cursoId
        val intentoNum = itemToConfirm.siguienteIntentoNumero
        val maxIntentosStr = itemToConfirm.maxIntentos?.let { "de $it" } ?: "(Sin límite)"
        val ultimo = itemToConfirm.ultimoIntentoResumen

        AlertDialog(
            onDismissRequest = { selectedItemForConfirm = null },
            icon = {
                Icon(
                    imageVector = if (itemToConfirm.sinIntentosDisponibles) Icons.Default.Info else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = if (itemToConfirm.sinIntentosDisponibles) "Intentos Agotados" else "Confirmación de Inicio",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Actividad: ${actividad.instrucciones}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    // Mostrar los resultados del último intento en el mensaje de confirmación
                    if (ultimo != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (ultimo.porcentajeLogro >= 60) appColors.success.container else appColors.warning.container,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Resultado del Intento #${ultimo.numeroIntento}:",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (ultimo.porcentajeLogro >= 60) appColors.success.content else appColors.warning.content
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${ultimo.porcentajeLogro}% de logro (${ultimo.cantidadCorrectas} correctas y ${ultimo.cantidadIncorrectas} incorrectas de ${ultimo.totalPreguntas})",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (ultimo.porcentajeLogro >= 60) appColors.success.content else appColors.warning.content
                                    )
                                )
                            }
                        }
                    }

                    if (!itemToConfirm.sinIntentosDisponibles) {
                        Text(
                            text = "¿Estás seguro de que deseas iniciar el Intento $intentoNum $maxIntentosStr de esta actividad?",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = "Has completado todos los intentos permitidos para esta actividad.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error)
                        )
                    }
                }
            },
            confirmButton = {
                if (!itemToConfirm.sinIntentosDisponibles) {
                    Button(
                        onClick = {
                            val actId = actividad.id
                            selectedItemForConfirm = null
                            viewModel.iniciarIntento(actId, cursoId) { nuevoIntentoId ->
                                onIniciarIntento(actId, cursoId, nuevoIntentoId)
                            }
                        }
                    ) {
                        Text(if (ultimo != null) "Iniciar Nuevo Intento" else "Iniciar Intento")
                    }
                } else if (ultimo != null) {
                    Button(
                        onClick = {
                            val actId = actividad.id
                            val ultId = ultimo.intentoId
                            selectedItemForConfirm = null
                            onIniciarIntento(actId, cursoId, ultId)
                        }
                    ) {
                        Text("Ver Resultados Anteriores")
                    }
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (!itemToConfirm.sinIntentosDisponibles && ultimo != null) {
                        OutlinedButton(
                            onClick = {
                                val actId = actividad.id
                                val ultId = ultimo.intentoId
                                selectedItemForConfirm = null
                                onIniciarIntento(actId, cursoId, ultId)
                            }
                        ) {
                            Text("Ver Respuestas")
                        }
                    }
                    TextButton(onClick = { selectedItemForConfirm = null }) {
                        Text("Cancelar")
                    }
                }
            }
        )
    }
}

@Composable
fun ActividadCardEstudiante(
    item: ActividadEstudianteItem,
    onIniciarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ac = item.actividadCursoConDetalles.actividadCurso
    val actividad = item.actividadCursoConDetalles.actividad
    val curso = item.actividadCursoConDetalles.curso
    val asignatura = item.asignatura
    val unidad = item.unidad
    val appColors = LocalAppColors.current

    val maxIntentosStr = item.maxIntentos?.let { "$it" } ?: "Sin límite"
    val intentoTexto = if (item.sinIntentosDisponibles) {
        "Intentos realizados: ${item.intentosRealizados} de $maxIntentosStr"
    } else {
        "Intento ${item.siguienteIntentoNumero} de $maxIntentosStr"
    }

    val fechaInicioStr = if (!ac.fechaInicio.isNull_or_blank()) formatFecha(ac.fechaInicio) else "No definida"
    val fechaFinStr = if (!ac.fechaFin.isNull_or_blank()) formatFecha(ac.fechaFin) else "Sin fecha límite"

    Card(
        onClick = onIniciarClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Asignatura / Curso
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Assignment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${asignatura?.nombre ?: "Asignatura"} • ${curso.nombreCompletoConAnnio}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    if (unidad != null) {
                        Text(
                            text = unidad.unidad.nombre,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Instrucciones / Título de Actividad
            Text(
                text = actividad.instrucciones,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Info Grid (Intentos, Resultado Anterior, Fecha Inicio, Fecha Fin)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Number of attempts
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Repeat,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = intentoTexto,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                // Resumen de último intento si existe
                if (item.ultimoIntentoResumen != null) {
                    val ult = item.ultimoIntentoResumen
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (ult.porcentajeLogro >= 60) appColors.success.container else appColors.warning.container,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (ult.porcentajeLogro >= 60) appColors.success.content else appColors.warning.content,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                text = "Último intento (#${ult.numeroIntento}): ${ult.porcentajeLogro}% de logro (${ult.cantidadCorrectas}/${ult.totalPreguntas} correctas)",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (ult.porcentajeLogro >= 60) appColors.success.content else appColors.warning.content
                                )
                            )
                        }
                    }
                }

                // Dates
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = "Inicio: $fechaInicioStr",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            text = "Fin: $fechaFinStr",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Botón de Iniciar Intento
            if (item.sinIntentosDisponibles) {
                OutlinedButton(
                    onClick = onIniciarClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text("Ver Respuestas / Intentos Agotados")
                }
            } else if (item.intentosRealizados > 0) {
                Button(
                    onClick = onIniciarClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text("Iniciar Intento ${item.siguienteIntentoNumero}")
                }
            } else {
                Button(
                    onClick = onIniciarClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text("Iniciar Intento")
                }
            }
        }
    }
}

private fun String?.isNull_or_blank(): Boolean {
    return this == null || this.isBlank()
}
