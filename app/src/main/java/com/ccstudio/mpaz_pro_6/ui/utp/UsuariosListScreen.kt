package com.ccstudio.mpaz_pro_6.ui.utp

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.local.dao.UsuarioConRol
import com.ccstudio.mpaz_pro_6.ui.components.AppFilterChip
import com.ccstudio.mpaz_pro_6.ui.components.AppSwitch
import com.ccstudio.mpaz_pro_6.ui.components.RoleBadge
import com.ccstudio.mpaz_pro_6.ui.theme.LocalAppColors

@Composable
fun UsuariosListScreen(
    viewModel: UtpViewModel,
    onCreateUserClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.usuariosState.collectAsState()
    val appColors = LocalAppColors.current

    LaunchedEffect(Unit) {
        viewModel.loadUsuarios()
    }

    val filteredUsuarios = state.usuarios.filter { item ->
        val query = state.searchQuery.trim().lowercase()
        val matchesQuery = if (query.isEmpty()) {
            true
        } else {
            val user = item.usuario
            val roleName = item.rol.nombre.lowercase()
            user.nombreCompleto.lowercase().contains(query) ||
                    user.nombreUsuario.lowercase().contains(query) ||
                    user.pnombre.lowercase().contains(query) ||
                    user.apaterno.lowercase().contains(query) ||
                    roleName.contains(query)
        }

        val matchesRole = if (state.selectedRolFilter != null) {
            item.rol.id == state.selectedRolFilter
        } else {
            true
        }

        matchesQuery && matchesRole
    }

    val availableRoles = remember(state.usuarios) {
        state.usuarios.map { it.rol }.distinctBy { it.id }.sortedBy { it.id }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Buscador por texto
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.updateUsuariosSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Buscar usuario por nombre, @usuario o rol...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateUsuariosSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar búsqueda"
                            )
                        }
                    }
                },
                singleLine = true
            )

            // Filtros de Rol por Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    AppFilterChip(
                        selected = state.selectedRolFilter == null,
                        onClick = { viewModel.updateUsuariosRolFilter(null) },
                        label = "Todos"
                    )
                }
                items(availableRoles, key = { it.id }) { rol ->
                    AppFilterChip(
                        selected = state.selectedRolFilter == rol.id,
                        onClick = {
                            viewModel.updateUsuariosRolFilter(
                                if (state.selectedRolFilter == rol.id) null else rol.id
                            )
                        },
                        label = roleFilterLabel(rol.nombre),
                        leadingIcon = roleIcon(rol.nombre)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (filteredUsuarios.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (state.searchQuery.isBlank() && state.selectedRolFilter == null) {
                            "No hay usuarios registrados en el sistema."
                        } else {
                            "No se encontraron usuarios con los criterios de búsqueda."
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredUsuarios, key = { it.usuario.id }) { item ->
                        UsuarioCardItem(
                            usuarioConRol = item,
                            onToggleActive = { viewModel.toggleUsuarioActivo(item.usuario) }
                        )
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = onCreateUserClick,
            icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Crear usuario") },
            text = { Text("Nuevo Usuario") },
            containerColor = appColors.fabContainer,
            contentColor = appColors.fabContent,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Composable
private fun UsuarioCardItem(
    usuarioConRol: UsuarioConRol,
    onToggleActive: () -> Unit
) {
    val usuario = usuarioConRol.usuario
    val rol = usuarioConRol.rol
    val appColors = LocalAppColors.current

    val roleTextColor = when (rol.nombre.uppercase()) {
        "UTP" -> MaterialTheme.colorScheme.onPrimaryContainer
        "DOCENTE" -> appColors.success.content
        "ESTUDIANTE" -> appColors.warning.content
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Icon(
                    imageVector = when (rol.nombre.uppercase()) {
                        "UTP" -> Icons.Default.Security
                        "DOCENTE" -> Icons.Default.Badge
                        else -> Icons.Default.Person
                    },
                    contentDescription = null,
                    tint = roleTextColor,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = usuario.nombreCompleto,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "@${usuario.nombreUsuario}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                RoleBadge(
                    text = rol.nombre,
                    contentColor = roleTextColor
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (usuario.activo) "Activo" else "Inactivo",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (usuario.activo) appColors.success.content else appColors.neutral.content
                )
                AppSwitch(
                    checked = usuario.activo,
                    onCheckedChange = { onToggleActive() }
                )
            }
        }
    }
}

private fun roleFilterLabel(roleName: String): String = when (roleName.uppercase()) {
    "DOCENTE" -> "Docentes"
    "ESTUDIANTE" -> "Estudiantes"
    else -> roleName
}

private fun roleIcon(roleName: String): ImageVector = when (roleName.uppercase()) {
    "UTP" -> Icons.Default.Security
    "DOCENTE" -> Icons.Default.Badge
    else -> Icons.Default.School
}
