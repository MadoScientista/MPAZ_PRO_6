package com.ccstudio.mpaz_pro_6.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.ui.components.StatusChip
import com.ccstudio.mpaz_pro_6.ui.components.SubjectLabel
import com.ccstudio.mpaz_pro_6.ui.utp.UtpViewModel

/**
 * Pantalla principal de inicio (Dashboard).
 *
 * Ofrece una interfaz personalizada según el rol del usuario autenticado:
 * - UTP: Centro de control completo con KPIs del sistema, accesos rápidos y lista de revisiones pendientes.
 * - DOCENTE: Centro de gestión pedagógica con resumen de asignaturas, accesos para creación de contenido, informes y vista previa de asignaturas a cargo.
 * - ESTUDIANTE: Portal del estudiante con acceso directo a sus asignaturas y actividades pendientes.
 *
 * @param rol Rol del usuario autenticado ("UTP", "DOCENTE", "ESTUDIANTE").
 * @param viewModel ViewModel principal para obtener estadísticas, asignaturas del docente y nombre del usuario.
 * @param onNavigateToRevisiones Callback para navegar a Revisiones Pendientes / Actividades.
 * @param onNavigateToCursos Callback para navegar a la lista de Cursos.
 * @param onNavigateToDocentes Callback para navegar a la lista de Docentes.
 * @param onNavigateToUsuarios Callback para navegar a la lista de Usuarios.
 * @param onNavigateToCrearUsuario Callback para navegar a la pantalla de Creación de Usuario.
 * @param onNavigateToDetalleUnidad Callback para navegar directamente al detalle de una unidad.
 * @param onNavigateToAsignaturas Callback para navegar a la sección de Asignaturas.
 * @param onNavigateToCrear Callback para navegar a la sección de Crear Contenido.
 * @param onNavigateToInformes Callback para navegar a la sección de Informes Pedagógicos.
 * @param onNavigateToAsignaturaCurso Callback para navegar al detalle de una Asignatura en un Curso específico.
 */
@Composable
fun InicioScreen(
    rol: String,
    modifier: Modifier = Modifier,
    viewModel: UtpViewModel? = null,
    onNavigateToRevisiones: () -> Unit = {},
    onNavigateToCursos: () -> Unit = {},
    onNavigateToDocentes: () -> Unit = {},
    onNavigateToUsuarios: () -> Unit = {},
    onNavigateToCrearUsuario: () -> Unit = {},
    onNavigateToDetalleUnidad: (Long) -> Unit = {},
    onNavigateToAsignaturas: () -> Unit = {},
    onNavigateToCrear: () -> Unit = {},
    onNavigateToInformes: () -> Unit = {},
    onNavigateToAsignaturaCurso: (Long, Long) -> Unit = { _, _ -> },
) {
    val scrollState = rememberScrollState()

    val currentName by viewModel?.currentUserName?.collectAsState() ?: remember { androidx.compose.runtime.mutableStateOf("") }
    val pendingUnits by viewModel?.pendingUnits?.collectAsState() ?: remember { androidx.compose.runtime.mutableStateOf(emptyList()) }
    val cursosState by viewModel?.cursosState?.collectAsState() ?: remember { androidx.compose.runtime.mutableStateOf(com.ccstudio.mpaz_pro_6.ui.utp.CursosUiState()) }
    val docentesState by viewModel?.docentesState?.collectAsState() ?: remember { androidx.compose.runtime.mutableStateOf(com.ccstudio.mpaz_pro_6.ui.utp.DocentesUiState()) }
    val usuariosState by viewModel?.usuariosState?.collectAsState() ?: remember { androidx.compose.runtime.mutableStateOf(com.ccstudio.mpaz_pro_6.ui.utp.UsuariosUiState()) }
    val docenteAsignaturas by viewModel?.docenteAsignaturas?.collectAsState() ?: remember { androidx.compose.runtime.mutableStateOf(emptyList()) }

    LaunchedEffect(Unit) {
        viewModel?.loadCurrentUserName()
        viewModel?.loadPendingUnits()
        viewModel?.loadCursos()
        viewModel?.loadDocentes()
        viewModel?.loadUsuarios()
        viewModel?.loadDocenteAsignaturas()
    }

    val displayName = currentName.ifBlank { rol }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- 1. HEADER CON SALUDO PERSONALIZADO ---
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "¡Hola, $displayName!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Text(
                text = if (rol.equals("UTP", ignoreCase = true)) {
                    "Coordinación Técnico Pedagógica"
                } else if (rol.equals("DOCENTE", ignoreCase = true)) {
                    "Portal Docente - Gestión Pedagógica"
                } else if (rol.equals("ESTUDIANTE", ignoreCase = true)) {
                    "Portal del Estudiante"
                } else {
                    "Rol actual: $rol"
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // --- 2. VISTA Y DASHBOARD PARA ESTUDIANTE ---
        if (rol.equals("ESTUDIANTE", ignoreCase = true)) {
            Text(
                text = "Accesos Rápidos",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KpiCard(
                    count = "Mis Asignaturas",
                    subtitle = "Acceder a asignaturas y unidades",
                    icon = Icons.Default.Book,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = onNavigateToAsignaturas,
                    modifier = Modifier.weight(1f)
                )

                KpiCard(
                    count = "Actividades",
                    subtitle = "Resolver tareas y pruebas",
                    icon = Icons.AutoMirrored.Filled.FactCheck,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    onClick = onNavigateToRevisiones,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- 3. VISTA Y DASHBOARD PARA DOCENTE ---
        if (rol.equals("DOCENTE", ignoreCase = true)) {
            val cursosAsignadosCount = remember(docenteAsignaturas) {
                docenteAsignaturas.map { it.curso.id }.distinct().size
            }

            Text(
                text = "Resumen Pedagógico",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        count = "${docenteAsignaturas.size}",
                        subtitle = "Mis asignaturas",
                        icon = Icons.Default.Book,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        onClick = onNavigateToAsignaturas,
                        modifier = Modifier.weight(1f)
                    )

                    KpiCard(
                        count = "Nuevo",
                        subtitle = "Crear contenido",
                        icon = Icons.Default.AddCircle,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        onClick = onNavigateToCrear,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        count = "Reportes",
                        subtitle = "Informes pedagógicos",
                        icon = Icons.Default.Assessment,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        onClick = onNavigateToInformes,
                        modifier = Modifier.weight(1f)
                    )

                    KpiCard(
                        count = "$cursosAsignadosCount",
                        subtitle = "Cursos a cargo",
                        icon = Icons.Default.School,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = onNavigateToCursos,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ACCIONES RÁPIDAS PARA DOCENTE
            Text(
                text = "Acciones Rápidas",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                QuickActionButton(
                    icon = Icons.Default.AddCircle,
                    label = "Crear contenido",
                    onClick = onNavigateToCrear
                )
                QuickActionButton(
                    icon = Icons.Default.Book,
                    label = "Mis asignaturas",
                    onClick = onNavigateToAsignaturas
                )
                QuickActionButton(
                    icon = Icons.Default.Assessment,
                    label = "Informes pedagógicos",
                    onClick = onNavigateToInformes
                )
                QuickActionButton(
                    icon = Icons.Default.School,
                    label = "Ver cursos",
                    onClick = onNavigateToCursos
                )
            }

            // MIS ASIGNATURAS A CARGO (VISTA PREVIA)
            Text(
                text = "Mis Asignaturas a Cargo",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            if (docenteAsignaturas.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                text = "Sin asignaturas asignadas",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "No tiene asignaturas o cursos vinculados actualmente.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    docenteAsignaturas.forEach { item ->
                        Card(
                            onClick = {
                                onNavigateToAsignaturaCurso(item.curso.id, item.asignatura.id)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.padding(end = 12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Book,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    SubjectLabel(
                                        subjectName = item.asignatura.nombre,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        textColor = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = item.nombreCompletoCurso,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = item.nombreCurso,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Ver Asignatura",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 4. TARJETAS DE MÉTRICAS (DASHBOARD KPIs PARA UTP) ---
        if (rol.equals("UTP", ignoreCase = true)) {
            Text(
                text = "Resumen del Sistema",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        count = "${pendingUnits.size}",
                        subtitle = "Por revisar",
                        icon = Icons.AutoMirrored.Filled.FactCheck,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        badgeText = if (pendingUnits.isNotEmpty()) "${pendingUnits.size} PENDIENTE${if (pendingUnits.size > 1) "S" else ""}" else null,
                        onClick = onNavigateToRevisiones,
                        modifier = Modifier.weight(1f)
                    )

                    KpiCard(
                        count = "${cursosState.cursos.size}",
                        subtitle = "Cursos activos",
                        icon = Icons.Default.School,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        onClick = onNavigateToCursos,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KpiCard(
                        count = "${docentesState.docentes.size}",
                        subtitle = "Profesores",
                        icon = Icons.Default.Badge,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = onNavigateToDocentes,
                        modifier = Modifier.weight(1f)
                    )

                    KpiCard(
                        count = "${usuariosState.usuarios.size}",
                        subtitle = "Usuarios totales",
                        icon = Icons.Default.People,
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = onNavigateToUsuarios,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ACCIONES RÁPIDAS PARA UTP
            Text(
                text = "Acciones Rápidas",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                QuickActionButton(
                    icon = Icons.AutoMirrored.Filled.FactCheck,
                    label = "Revisar material",
                    onClick = onNavigateToRevisiones
                )
                QuickActionButton(
                    icon = Icons.Default.School,
                    label = "Gestionar cursos",
                    onClick = onNavigateToCursos
                )
                QuickActionButton(
                    icon = Icons.Default.Badge,
                    label = "Ver docentes",
                    onClick = onNavigateToDocentes
                )
                QuickActionButton(
                    icon = Icons.Default.PersonAdd,
                    label = "Crear usuario",
                    onClick = onNavigateToCrearUsuario
                )
            }

            // REVISIONES PENDIENTES URGENTES (VISTA PREVIA)
            Text(
                text = "Revisiones Pendientes",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            if (pendingUnits.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 12.dp)
                        )
                        Column {
                            Text(
                                text = "¡Todo al día!",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "No hay materiales pendientes de revisión.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    pendingUnits.take(3).forEach { item ->
                        Card(
                            onClick = { onNavigateToDetalleUnidad(item.unidad.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Book,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = 12.dp)
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.unidad.nombre,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    SubjectLabel(
                                        subjectName = item.asignatura.nombre,
                                        style = MaterialTheme.typography.bodySmall,
                                        textColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                StatusChip(statusName = item.estado.nombre)

                                Spacer(modifier = Modifier.width(8.dp))

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Revisar unidad",
                                    tint = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun KpiCard(
    count: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeText: String? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor
                )
                if (badgeText != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = contentColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
    }
}
