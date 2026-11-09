package com.ccstudio.mpaz_pro_6.ui.utp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.ui.components.AppSwitch

@Composable
fun CrearUsuarioScreen(
    viewModel: UtpViewModel,
    onUserCreated: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.crearUsuarioState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.initCrearUsuarioForm()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Crear Nuevo Usuario",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Complete la información requerida para registrar un nuevo usuario en la plataforma.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        state.errorMessage?.let { error ->
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Datos de Cuenta",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Nombre de usuario
                OutlinedTextField(
                    value = state.nombreUsuario,
                    onValueChange = { viewModel.updateCrearUsuarioNombreUsuario(it) },
                    label = { Text("Nombre de Usuario (Login) *") },
                    placeholder = { Text("ej. jgonzalez") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Contraseña
                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.updateCrearUsuarioPassword(it) },
                    label = { Text("Contraseña *") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                    },
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = if (passwordVisible) "Ocultar" else "Mostrar")
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Información Personal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Primer nombre
                OutlinedTextField(
                    value = state.pnombre,
                    onValueChange = { viewModel.updateCrearUsuarioPnombre(it) },
                    label = { Text("Primer Nombre *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Segundo nombre
                OutlinedTextField(
                    value = state.snombre,
                    onValueChange = { viewModel.updateCrearUsuarioSnombre(it) },
                    label = { Text("Segundo Nombre (Opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Apellido paterno
                OutlinedTextField(
                    value = state.apaterno,
                    onValueChange = { viewModel.updateCrearUsuarioApaterno(it) },
                    label = { Text("Apellido Paterno *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Apellido materno
                OutlinedTextField(
                    value = state.amaterno,
                    onValueChange = { viewModel.updateCrearUsuarioAmaterno(it) },
                    label = { Text("Apellido Materno (Opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Rol y Estado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Seleccione el rol del usuario:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.roles.forEach { rolEntity ->
                        val isSelected = state.rolId == rolEntity.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updateCrearUsuarioRolId(rolEntity.id) },
                            label = { Text(rolEntity.nombre) },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (rolEntity.nombre.uppercase()) {
                                        "UTP" -> Icons.Default.Security
                                        "DOCENTE" -> Icons.Default.Badge
                                        else -> Icons.Default.School
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.height(16.dp)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Usuario Activo",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    AppSwitch(
                        checked = state.activo,
                        onCheckedChange = { viewModel.updateCrearUsuarioActivo(it) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                enabled = !state.isLoading
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {
                    viewModel.crearUsuario {
                        onUserCreated()
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Crear Usuario")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
