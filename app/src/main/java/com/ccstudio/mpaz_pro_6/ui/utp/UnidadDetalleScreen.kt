package com.ccstudio.mpaz_pro_6.ui.utp

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.ccstudio.mpaz_pro_6.data.local.dao.ActividadConTipo
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ContenidoEntity
import com.ccstudio.mpaz_pro_6.ui.components.AppPillButton
import com.ccstudio.mpaz_pro_6.ui.components.StatusChip
import com.ccstudio.mpaz_pro_6.ui.theme.LocalAppColors

@Composable
fun UnidadDetalleScreen(
    unidadId: Long,
    cursoId: Long = 0L,
    viewModel: UtpViewModel,
    rol: String = "UTP",
    onContentClick: (Long) -> Unit,
    onPruebaFinalClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.unitDetailState.collectAsState()
    val isUtp = rol.equals("UTP", ignoreCase = true)
    val appColors = LocalAppColors.current

    var showAutorizarConfirmDialog by remember { mutableStateOf(false) }
    var showBorradorConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(unidadId, cursoId) {
        viewModel.loadUnitDetail(unidadId, cursoId)
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

    val unidadConDetalles = state.unidadConDetalles
    if (unidadConDetalles == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Unidad no encontrada")
        }
        return
    }

    val unidad = unidadConDetalles.unidad
    val asignatura = unidadConDetalles.asignatura
    val estado = unidadConDetalles.estado
    val curso = state.cursoConDetalles
    val isAuthorized = estado.id == 3L || estado.nombre == "AUTORIZADO"
    val isEstudiante = rol.equals("ESTUDIANTE", ignoreCase = true)

    if (isEstudiante && !isAuthorized) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Esta unidad no se encuentra disponible actualmente.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Título de la Unidad y Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = curso?.nombreCompletoConAnnio ?: asignatura.nombre,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    if (!isEstudiante) {
                        StatusChip(statusName = estado.nombre)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = unidad.nombre,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        }

        // 2. Listado de Contenidos
        Text(
            text = "Listado de Contenidos",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        if (state.contenidos.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "No hay contenidos registrados en esta unidad.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.contenidos.forEachIndexed { index, contenido ->
                    ContenidoItemCard(
                        index = index + 1,
                        contenido = contenido,
                        onClick = { onContentClick(contenido.id) }
                    )
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // 3. Prueba Final
        Text(
            text = "Prueba Final",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        val pruebaFinal = state.pruebaFinal
        if (pruebaFinal != null) {
            PruebaFinalCard(
                pruebaFinal = pruebaFinal,
                onClick = { onPruebaFinalClick(pruebaFinal.actividad.id) }
            )
        } else {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Text(
                        text = "No hay prueba disponible",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Botones de Acción (UTP) / Estado de la Unidad
        if (isUtp) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (isAuthorized) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = appColors.success.container,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = appColors.success.content,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "Unidad Autorizada",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = appColors.success.content
                                )
                            )
                        }
                    }

                    AppPillButton(
                        text = "Cambiar a Borrador",
                        onClick = { showBorradorConfirmDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 52.dp),
                        containerColor = appColors.destructiveButton.container,
                        contentColor = appColors.destructiveButton.content
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AppPillButton(
                            text = "A Borrador",
                            onClick = { showBorradorConfirmDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 52.dp),
                            containerColor = appColors.destructiveButton.container,
                            contentColor = appColors.destructiveButton.content
                        )

                        AppPillButton(
                            text = "Autorizar",
                            onClick = { showAutorizarConfirmDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 52.dp),
                            containerColor = appColors.successButton.container,
                            contentColor = appColors.successButton.content
                        )
                    }
                }
            }
        } else if (!isEstudiante) {
            if (isAuthorized) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = appColors.success.container,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = appColors.success.content,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Unidad Autorizada",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = appColors.success.content
                            )
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Unidad En Revisión (Pendiente de Autorización UTP)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }

    if (showAutorizarConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showAutorizarConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = appColors.success.content
                )
            },
            title = {
                Text(
                    text = "¿Autorizar Unidad?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas autorizar la unidad \"${unidad.nombre}\"? Estará disponible para los docentes y cursos.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                AppPillButton(
                    text = "Autorizar",
                    onClick = {
                        showAutorizarConfirmDialog = false
                        viewModel.autorizarUnidad(unidadId, cursoId)
                    },
                    containerColor = appColors.successButton.container,
                    contentColor = appColors.successButton.content
                )
            },
            dismissButton = {
                TextButton(onClick = { showAutorizarConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (showBorradorConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showBorradorConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = "¿Cambiar a Borrador?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas cambiar el estado de la unidad \"${unidad.nombre}\" a Borrador?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                AppPillButton(
                    text = "Cambiar a Borrador",
                    onClick = {
                        showBorradorConfirmDialog = false
                        viewModel.cambiarEstadoUnidadBorrador(unidadId, cursoId)
                    },
                    containerColor = appColors.destructiveButton.container,
                    contentColor = appColors.destructiveButton.content
                )
            },
            dismissButton = {
                TextButton(onClick = { showBorradorConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ContenidoItemCard(
    index: Int,
    contenido: ContenidoEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val titleLine = contenido.descripcion.lines()
        .firstOrNull { it.isNotBlank() }
        ?.removePrefix("#")?.removePrefix("#")?.removePrefix("#")?.trim()
        ?: "Contenido $index"

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Article,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Contenido $index",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = titleLine,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Ver contenido",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun PruebaFinalCard(
    pruebaFinal: ActividadConTipo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Quiz,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Prueba Final Disponible",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = pruebaFinal.actividad.instrucciones,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                    ),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Ver prueba final",
                tint = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}
