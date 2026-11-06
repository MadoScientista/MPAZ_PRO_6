package com.ccstudio.mpaz_pro_6.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.ui.components.StatusChip
import com.ccstudio.mpaz_pro_6.ui.utp.UtpViewModel

/**
 * Pantalla principal de inicio (Dashboard).
 *
 * Para el rol UTP, despliega un centro de control completo que incluye:
 * - Saludo personalizado con el primer nombre del usuario en sesión (`¡Hola, [Nombre]!`).
 * - Tarjetas resumen de métricas clave (KPIs: Revisiones pendientes, Cursos activos, Docentes y Usuarios totales).
 * - Acciones rápidas con navegación directa a flujos principales de gestión.
 * - Vista previa de las unidades con revisión pendiente más recientes.
 * - Botón de cierre de sesión.
 *
 * @param rol Rol del usuario autenticado ("UTP", "DOCENTE", "ESTUDIANTE").
 * @param onLogoutClick Callback para cerrar la sesión activa.
 * @param viewModel ViewModel de UTP para obtener estadísticas y nombre del usuario.
 * @param onNavigateToRevisiones Callback para navegar a la sección de Revisiones Pendientes.
 * @param onNavigateToCursos Callback para navegar a la lista de Cursos.
 * @param onNavigateToDocentes Callback para navegar a la lista de Docentes.
 * @param onNavigateToUsuarios Callback para navegar a la lista de Usuarios.
 * @param onNavigateToCrearUsuario Callback para navegar a la pantalla de Creación de Usuario.
 * @param onNavigateToDetalleUnidad Callback para navegar directamente al detalle de una unidad dada su ID.
 */
@Composable
fun InicioScreen(
    rol: String,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UtpViewModel? = null,
    onNavigateToRevisiones: () -> Unit = {},
    onNavigateToCursos: () -> Unit = {},
    onNavigateToDocentes: () -> Unit = {},
    onNavigateToUsuarios: () -> Unit = {},
    onNavigateToCrearUsuario: () -> Unit = {},
    onNavigateToDetalleUnidad: (Long) -> Unit = {},
) {
    val scrollState = rememberScrollState()

    val currentName by viewModel?.currentUserName?.collectAsState() ?: androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    val pendingUnits by viewModel?.pendingUnits?.collectAsState() ?: androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(emptyList()) }
    val cursosState by viewModel?.cursosState?.collectAsState() ?: androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(com.ccstudio.mpaz_pro_6.ui.utp.CursosUiState()) }
    val docentesState by viewModel?.docentesState?.collectAsState() ?: androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(com.ccstudio.mpaz_pro_6.ui.utp.DocentesUiState()) }
    val usuariosState by viewModel?.usuariosState?.collectAsState() ?: androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(com.ccstudio.mpaz_pro_6.ui.utp.UsuariosUiState()) }

    LaunchedEffect(Unit) {
        viewModel?.loadCurrentUserName()
        viewModel?.loadPendingUnits()
        viewModel?.loadCursos()
        viewModel?.loadDocentes()
        viewModel?.loadUsuarios()
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
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "¡Hola, $displayName!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (rol.equals("UTP", ignoreCase = true)) {
                        "Coordinación Técnico Pedagógica"
                    } else if (rol.equals("ESTUDIANTE", ignoreCase = true)) {
                        "Portal del Estudiante"
                    } else {
                        "Rol actual: $rol"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                )
            }
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
                    onClick = onNavigateToCursos,
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

        // --- 2. TARJETAS DE MÉTRICAS (DASHBOARD KPIs PARA UTP) ---
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

            // --- 3. ACCIONES RÁPIDAS ---
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

            // --- 4. REVISIONES PENDIENTES URGENTES (VISTA PREVIA) ---
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToDetalleUnidad(item.unidad.id) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                                    Text(
                                        text = item.asignatura.nombre,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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

        // --- 5. BOTÓN CERRAR SESIÓN ---
        Button(
            onClick = onLogoutClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Cerrar sesión"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión")
        }
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
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                        shape = MaterialTheme.shapes.extraSmall
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
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = contentColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.8f)
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
