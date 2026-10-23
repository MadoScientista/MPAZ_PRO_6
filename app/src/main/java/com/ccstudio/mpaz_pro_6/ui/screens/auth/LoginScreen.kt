package com.ccstudio.mpaz_pro_6.ui.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.BuildConfig
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTextField

/**
 * Pantalla de inicio de sesión.
 *
 * Solo es UI: las credenciales viajan a `/api/auth/login` a través de
 * `AuthRepository`. La contraseña es un dato del formulario, no del modelo `User`,
 * y nunca se persiste en el dispositivo.
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
    hint: String? = null,
    onResetLocalData: (() -> Unit)? = null,
    isResettingData: Boolean = false
) {
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Inicia sesión",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Sistema educacional MPAZ",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
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
            passwordVisible = isPasswordVisible,
            enabled = !isLoading,
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        },
                        contentDescription = if (isPasswordVisible) {
                            "Ocultar contraseña"
                        } else {
                            "Mostrar contraseña"
                        }
                    )
                }
            }
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
        } else if (BuildConfig.MPAZ_LOCAL_ONLY) {
            Text(
                text = "Modo local: usa un usuario de la semilla. Contraseña: mpaz123",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }

        if (onResetLocalData != null) {
            TextButton(
                onClick = onResetLocalData,
                enabled = !isResettingData
            ) {
                Text(
                    text = if (isResettingData) {
                        "Restableciendo datos..."
                    } else {
                        "Restablecer datos locales"
                    }
                )
            }
        }
    }
}
