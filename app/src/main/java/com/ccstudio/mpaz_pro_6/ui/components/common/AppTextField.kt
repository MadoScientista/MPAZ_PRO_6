package com.ccstudio.mpaz_pro_6.ui.components.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Campo de texto con etiqueta.
 *
 * Envuelve `OutlinedTextField` con los estilos del tema y soporta el modo de
 * contraseña, incluida la opción de mostrarla y un icono final opcional.
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supportingText: String? = null,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label) },
        placeholder = placeholder?.let { { Text(text = it) } },
        supportingText = supportingText?.let { { Text(text = it) } },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        enabled = enabled,
        trailingIcon = trailingIcon,
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        textStyle = MaterialTheme.typography.bodyLarge
    )
}
