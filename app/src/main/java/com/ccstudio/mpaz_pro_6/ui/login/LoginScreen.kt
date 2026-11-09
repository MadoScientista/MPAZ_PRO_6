package com.ccstudio.mpaz_pro_6.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.ui.components.ThemeToggleButton

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onResetLocalDataClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.resetMessage) {
        uiState.resetMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Formulario centrado en la ventana. No se aplican insets del IME:
            // el teclado se superpone y el formulario no cambia de posición.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título
                Text(
                    text = "MPAZ PRO",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Campos agrupados: usuario y contraseña comparten un
                // espaciado menor para leerse como un mismo bloque.
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Campo Nombre de Usuario
                    OutlinedTextField(
                        value = uiState.username,
                        onValueChange = onUsernameChange,
                        label = { Text("Nombre de usuario") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Campo Contraseña (caracteres ocultos / no guarda historial)
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        label = { Text("Contraseña") },
                        isError = uiState.errorMessage != null,
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    contentDescription = if (isPasswordVisible) {
                                        "Ocultar contraseña"
                                    } else {
                                        "Mostrar contraseña"
                                    }
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (isPasswordVisible) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                            autoCorrectEnabled = false
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { onLoginClick() }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Área de error con altura fija: evita que el formulario
                // centrado se desplace cuando aparece el mensaje.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón de Inicio de Sesión. Se mantiene visible y con la
                // misma altura mientras carga para no re-centrar el formulario.
                Button(
                    onClick = onLoginClick,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (uiState.isLoading) "Validando..." else "Iniciar Sesión")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Link / Botón para resetear datos locales
                TextButton(
                    onClick = onResetLocalDataClick,
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        text = "Resetear datos locales",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp)
            )

            ThemeToggleButton(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                tint = if (isDarkTheme) Color.White else Color.Black,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(8.dp)
            )
        }
    }
}
