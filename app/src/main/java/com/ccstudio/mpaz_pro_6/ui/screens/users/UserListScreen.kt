package com.ccstudio.mpaz_pro_6.ui.screens.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.ConfirmDialog
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SearchField
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.common.ThemeToggleButton
import com.ccstudio.mpaz_pro_6.ui.components.user.UserCard

/**
 * Listado de usuarios.
 *
 * Consume `GET /api/usuarios`. El filtro por rol y la búsqueda son client-side.
 * El curso solo se muestra para estudiantes y su nombre lo resuelve esta pantalla
 * con el catálogo de cursos. Permite crear, editar y borrar; el `409 Conflict` del
 * borrado se explica con el mensaje del ViewModel.
 */
@Composable
fun UserListScreen(
    usuarios: List<User>,
    cursos: List<Course>,
    onCreate: () -> Unit,
    onEdit: (User) -> Unit,
    onDelete: (User) -> Unit,
    mensajeOperacion: String?,
    onDismissMensaje: () -> Unit,
    onRetry: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    var search by rememberSaveable { mutableStateOf("") }
    var roleFilter by rememberSaveable { mutableStateOf("Todos") }
    var pendienteEliminar by remember { mutableStateOf<User?>(null) }

    val roleOptions = listOf("Todos") + UserRole.entries.map { it.wireValue }
    val courseNames = cursos.associate { it.id to it.nombre }

    val filteredUsers = usuarios.filter { user ->
        val query = search.trim()

        val matchesRole = roleFilter == "Todos" || user.rol.wireValue == roleFilter
        val matchesSearch = query.isEmpty() ||
                user.nombre.contains(query, ignoreCase = true) ||
                user.username.contains(query, ignoreCase = true)

        matchesRole && matchesSearch
    }

    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            title = "Usuarios",
            actions = {
                ThemeToggleButton(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                isLoading && usuarios.isEmpty() -> LoadingState(message = "Cargando usuarios...")

                error != null -> ErrorState(error = error, onRetry = onRetry)

                else -> {
                    Text(
                        text = "Usuarios del sistema",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${usuarios.size} usuarios",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    AppButton(
                        text = "Nuevo usuario",
                        onClick = onCreate
                    )

                    if (mensajeOperacion != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = mensajeOperacion,
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            TextButton(onClick = onDismissMensaje) {
                                Text("Cerrar")
                            }
                        }
                    }

                    SearchField(
                        value = search,
                        onValueChange = { search = it },
                        placeholder = "Nombre o usuario"
                    )

                    FilterChipRow(
                        options = roleOptions,
                        selected = roleFilter,
                        onSelect = { roleFilter = it }
                    )

                    SectionHeader(
                        title = "Usuarios",
                        count = filteredUsers.size
                    )

                    if (filteredUsers.isEmpty()) {
                        EmptyState(
                            message = if (usuarios.isEmpty()) {
                                "No hay usuarios."
                            } else {
                                "No se encontraron usuarios."
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 8.dp)
                        ) {
                            items(
                                items = filteredUsers,
                                key = { it.id }
                            ) { user ->
                                UserCard(
                                    user = user,
                                    onEdit = { onEdit(user) },
                                    onDelete = { pendienteEliminar = user },
                                    courseName = if (user.rol == UserRole.ESTUDIANTE) {
                                        user.cursoId?.let { courseNames[it] }
                                    } else {
                                        null
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        pendienteEliminar?.let { user ->
            ConfirmDialog(
                title = "Eliminar usuario",
                message = "¿Eliminar a «${user.nombre}» (${user.username})? " +
                        "Esta acción no se puede deshacer.",
                onConfirm = {
                    onDelete(user)
                    pendienteEliminar = null
                },
                onDismiss = { pendienteEliminar = null }
            )
        }
    }
}
