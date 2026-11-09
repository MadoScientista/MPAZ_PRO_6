package com.ccstudio.mpaz_pro_6.ui.estudiante

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ccstudio.mpaz_pro_6.ui.navigation.fadeThroughContent
import com.ccstudio.mpaz_pro_6.ui.theme.LocalAppColors

@Composable
fun EstudianteResolverActividadScreen(
    actividadId: Long,
    cursoId: Long = 0L,
    intentoId: Long = 0L,
    onVolverAUnidad: (unidadId: Long, cursoId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EstudianteViewModel = viewModel()
) {
    val state by viewModel.resolverState.collectAsState()
    val appColors = LocalAppColors.current

    var showTerminarConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(actividadId, cursoId, intentoId) {
        viewModel.loadResolverActividad(actividadId, cursoId, intentoId)
    }

    if (state.estado == EstadoResolverActividad.LOADING) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (state.estado == EstadoResolverActividad.ERROR) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = state.errorMessage ?: "Error al cargar la actividad")
        }
        return
    }

    val actividad = state.actividad
    val preguntas = state.preguntas

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cabecera Principal de la Actividad
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${state.asignaturaNombre} • ${state.unidadNombre}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = "Intento ${state.numeroIntento} de ${state.maxIntentos ?: "Sin límite"}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = state.tipoActividad?.nombre ?: "Actividad",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )

                if (actividad != null && actividad.instrucciones.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = actividad.instrucciones,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                        )
                    )
                }
            }
        }

        // --- MODOS DE LA ACTIVIDAD ---
        AnimatedContent(
            targetState = state.estado,
            transitionSpec = { fadeThroughContent() },
            label = "resolverMode"
        ) { estado ->
        // --- MODO: CONFIRMACION (Pasos 1, 2 y 7) ---
        if (estado == EstadoResolverActividad.CONFIRMACION) {
            val ultimo = state.ultimoIntentoResumen
            val siguienteNum = state.totalIntentosRealizados + 1
            val maxStr = state.maxIntentos?.let { "de $it" } ?: "(Sin límite)"

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Confirmación de Inicio",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Mostrar los resultados del último intento si existe (Paso 7)
                    if (ultimo != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (ultimo.porcentajeLogro >= 60) appColors.success.container else appColors.warning.container,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Resultado del Intento #${ultimo.numeroIntento}:",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (ultimo.porcentajeLogro >= 60) appColors.success.content else appColors.warning.content
                                    )
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${ultimo.porcentajeLogro}% de Logro",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (ultimo.porcentajeLogro >= 60) appColors.success.content else appColors.warning.content
                                    )
                                )
                                Text(
                                    text = "${ultimo.cantidadCorrectas} respuestas correctas y ${ultimo.cantidadIncorrectas} incorrectas de ${ultimo.totalPreguntas}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (ultimo.porcentajeLogro >= 60) appColors.success.content else appColors.warning.content
                                    )
                                )
                            }
                        }
                    }

                    if (state.puedeRealizarOtroIntento) {
                        Text(
                            text = "¿Deseas iniciar el Intento $siguienteNum $maxStr de esta actividad?",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                        )

                        Button(
                            onClick = {
                                viewModel.iniciarNuevoIntento(actividadId, cursoId)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "Iniciar Intento $siguienteNum",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        if (ultimo != null) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.loadResolverActividad(actividadId, cursoId, ultimo.intentoId)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 50.dp),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "Ver Respuestas Anteriores",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "Has alcanzado el número máximo de intentos permitidos para esta actividad.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        if (ultimo != null) {
                            Button(
                                onClick = {
                                    viewModel.loadResolverActividad(actividadId, cursoId, ultimo.intentoId)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "Ver Respuestas Anteriores",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = { onVolverAUnidad(state.unidadId, state.cursoId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Volver a la unidad",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // --- MODO: RESOLVIENDO (Pasos 3 y 4) ---
        if (estado == EstadoResolverActividad.RESOLVIENDO) {
            Text(
                text = "Preguntas de la Actividad (${preguntas.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            preguntas.forEachIndexed { index, preguntaConAlt ->
                val preguntaId = preguntaConAlt.pregunta.id
                val seleccionadaId = state.respuestasSeleccionadas[preguntaId]

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Pregunta ${index + 1}",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = preguntaConAlt.pregunta.enunciado,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        preguntaConAlt.alternativas.forEachIndexed { altIndex, alternativa ->
                            val optionLetter = ('A' + altIndex).toString()
                            val isSelected = (seleccionadaId == alternativa.id)

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .selectable(
                                        selected = isSelected,
                                        onClick = {
                                            viewModel.seleccionarAlternativa(preguntaId, alternativa.id)
                                        },
                                        role = Role.RadioButton
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = null
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = optionLetter,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = alternativa.enunciado,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Terminar Actividad
            val respondidas = state.respuestasSeleccionadas.size
            val totalPreguntas = preguntas.size

            Button(
                onClick = { showTerminarConfirmDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Terminar Actividad ($respondidas de $totalPreguntas respondidas)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // --- MODO: RESULTADO (Pasos 5 y 6) ---
        if (estado == EstadoResolverActividad.RESULTADO) {
            // Tarjeta Resumen de Resultados
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.porcentajeLogro >= 60) appColors.success.container else appColors.warning.container
                ),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Resultado del Intento ${state.numeroIntento}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (state.porcentajeLogro >= 60) appColors.success.content else appColors.warning.content
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${state.porcentajeLogro}% de Logro",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (state.porcentajeLogro >= 60) appColors.success.content else appColors.warning.content
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${state.cantidadCorrectas}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = appColors.success.content
                                )
                            )
                            Text(
                                text = "Correctas",
                                style = MaterialTheme.typography.bodySmall.copy(color = appColors.success.content)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${state.cantidadIncorrectas}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = appColors.danger.content
                                )
                            )
                            Text(
                                text = "Incorrectas",
                                style = MaterialTheme.typography.bodySmall.copy(color = appColors.danger.content)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${preguntas.size}",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Text(
                                text = "Total",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }

            Text(
                text = "Desglose de Respuestas",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            // Desglose de Preguntas con Alternativas Correctas e Incorrectas
            state.resultados.forEachIndexed { index, resDetalle ->
                val pConAlt = resDetalle.preguntaConAlternativas

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
                            Text(
                                text = "Pregunta ${index + 1}",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            if (resDetalle.esCorrecta) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = appColors.success.container
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = appColors.success.content,
                                            modifier = Modifier.padding(end = 4.dp)
                                        )
                                        Text(
                                            text = "Correcta",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = appColors.success.content,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            } else {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = appColors.danger.container
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = null,
                                            tint = appColors.danger.content,
                                            modifier = Modifier.padding(end = 4.dp)
                                        )
                                        Text(
                                            text = "Incorrecta",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = appColors.danger.content,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = pConAlt.pregunta.enunciado,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        pConAlt.alternativas.forEachIndexed { altIndex, alternativa ->
                            val optionLetter = ('A' + altIndex).toString()
                            val isCorrectAnswer = alternativa.correcta
                            val isStudentSelected = (resDetalle.alternativaSeleccionadaId == alternativa.id)
                            val isStudentWrongSelection = isStudentSelected && !isCorrectAnswer

                            val containerColor = when {
                                isCorrectAnswer -> appColors.success.container
                                isStudentWrongSelection -> appColors.danger.container
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            }

                            val borderColor = when {
                                isCorrectAnswer -> appColors.success.content
                                isStudentWrongSelection -> appColors.danger.content
                                else -> Color.Transparent
                            }

                            val textColor = when {
                                isCorrectAnswer -> appColors.success.content
                                isStudentWrongSelection -> appColors.danger.content
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .border(
                                        width = if (isCorrectAnswer || isStudentWrongSelection) 1.5.dp else 0.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(10.dp)
                                    ),
                                color = containerColor,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when {
                                            isCorrectAnswer -> appColors.success.content
                                            isStudentWrongSelection -> appColors.danger.content
                                            else -> MaterialTheme.colorScheme.outlineVariant
                                        }
                                    ) {
                                        Text(
                                            text = optionLetter,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = when {
                                                    isCorrectAnswer -> appColors.success.container
                                                    isStudentWrongSelection -> appColors.danger.container
                                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                                }
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = alternativa.enunciado,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = textColor,
                                            fontWeight = if (isCorrectAnswer || isStudentWrongSelection) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (isCorrectAnswer) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Correcta",
                                            tint = appColors.success.content
                                        )
                                    } else if (isStudentWrongSelection) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = "Incorrecta elegida",
                                            tint = appColors.danger.content
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón "Volver a la unidad" (Paso 6)
            Button(
                onClick = {
                    onVolverAUnidad(state.unidadId, state.cursoId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Volver a la unidad",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
        }
    }

    // Diálogo de Confirmación "Terminar Actividad"
    if (showTerminarConfirmDialog) {
        val respondidas = state.respuestasSeleccionadas.size
        val total = preguntas.size
        val faltantes = total - respondidas

        AlertDialog(
            onDismissRequest = { showTerminarConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Help,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = "¿Terminar Actividad?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "¿Estás seguro de que deseas finalizar este intento y enviar tus respuestas?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (faltantes > 0) {
                        Text(
                            text = "Atención: Tienes $faltantes pregunta(s) sin responder.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val intId = state.intento?.id ?: 0L
                        showTerminarConfirmDialog = false
                        viewModel.finalizarIntento(intId) {}
                    }
                ) {
                    Text("Finalizar Actividad")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTerminarConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
