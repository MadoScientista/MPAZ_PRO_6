package com.ccstudio.mpaz_pro_6.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTextField

/**
 * Pantalla de inicio de sesión.
 *
 * Solo es UI: el backend no expone un endpoint de autenticación (solo el CRUD de
 * `/api/usuarios` con `username` y `password`), así que la validación de la sesión
 * se resuelve localmente. Cuando exista el endpoint de login, esta pantalla deberá
 * delegar en el repositorio y propagar el rol (`UserRole`) para elegir el destino
 * de la navegación.
 *
 * La contraseña es un dato del formulario, no del modelo `User`: viaja en claro
 * únicamente hacia el backend y nunca forma parte de una respuesta.
 */
@Composable
fun LoginScreen(
    username: String,
    password: String,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onIniciarSesion: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: String? = null,
    hint: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Inicia sesión",
            style = MaterialTheme.typography.headlineMedium
        )

        AppTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = "Nombre de usuario",
            enabled = !isLoading
        )

        AppTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = "Contraseña",
            isPassword = true,
            enabled = !isLoading
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

        AppButton(
            text = if (isLoading) "Validando..." else "Iniciar sesión",
            onClick = onIniciarSesion,
            enabled = !isLoading
        )

        if (hint != null) {
            Text(
                text = hint,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}
