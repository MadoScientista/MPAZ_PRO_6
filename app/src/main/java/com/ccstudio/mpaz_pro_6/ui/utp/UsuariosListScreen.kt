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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.local.dao.UsuarioConRol
import com.ccstudio.mpaz_pro_6.ui.components.StatusChip

@Composable
fun UsuariosListScreen(
    viewModel: UtpViewModel,
    onCreateUserClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.usuariosState.collectAsState()

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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateUserClick,
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = "Crear usuario") },
                text = { Text("Nuevo Usuario") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                    FilterChip(
                        selected = state.selectedRolFilter == null,
                        onClick = { viewModel.updateUsuariosRolFilter(null) },
                        label = { Text("Todos") }
                    )
                }
                item {
                    FilterChip(
                        selected = state.selectedRolFilter == 1L,
                        onClick = {
                            viewModel.updateUsuariosRolFilter(if (state.selectedRolFilter == 1L) null else 1L)
                        },
                        label = { Text("UTP") },
                        leadingIcon = {
                            Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.height(16.dp))
                        }
                    )
                }
                item {
                    FilterChip(
                        selected = state.selectedRolFilter == 2L,
                        onClick = {
                            viewModel.updateUsuariosRolFilter(if (state.selectedRolFilter == 2L) null else 2L)
                        },
                        label = { Text("Docentes") },
                        leadingIcon = {
                            Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.height(16.dp))
                        }
                    )
                }
                item {
                    FilterChip(
                        selected = state.selectedRolFilter == 3L,
                        onClick = {
                            viewModel.updateUsuariosRolFilter(if (state.selectedRolFilter == 3L) null else 3L)
                        },
                        label = { Text("Estudiantes") },
                        leadingIcon = {
                            Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.height(16.dp))
                        }
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
    }
}

@Composable
private fun UsuarioCardItem(
    usuarioConRol: UsuarioConRol,
    onToggleActive: () -> Unit
) {
    val usuario = usuarioConRol.usuario
    val rol = usuarioConRol.rol

    val (roleBgColor, roleTextColor) = when (rol.nombre.uppercase()) {
        "UTP" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        "DOCENTE" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "ESTUDIANTE" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = roleBgColor,
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "@${usuario.nombreUsuario}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SuggestionChip(
                        onClick = { },
                        label = {
                            Text(
                                text = rol.nombre,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )

                    StatusChip(
                        statusName = if (usuario.activo) "ACTIVO" else "INACTIVO"
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (usuario.activo) "Activo" else "Inactivo",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (usuario.activo) Color(0xFF2E7D32) else Color.Gray
                )
                Switch(
                    checked = usuario.activo,
                    onCheckedChange = { onToggleActive() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    }
}
