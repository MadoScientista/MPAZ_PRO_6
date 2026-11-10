package com.ccstudio.mpaz_pro_6.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Switch de la app con el diseño unificado del drawer.
 *
 * Track azul en encendido, gris en apagado, thumb blanco y sin borde.
 */
@Composable
fun AppSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors = SwitchDefaults.colors(
            // Encendido: azul del tema + círculo blanco.
            checkedTrackColor = MaterialTheme.colorScheme.primary,
            checkedThumbColor = Color.White,
            checkedBorderColor = Color.Transparent,
            // Apagado: gris con contraste suficiente + círculo blanco.
            uncheckedTrackColor = MaterialTheme.colorScheme.outline,
            uncheckedThumbColor = Color.White,
            uncheckedBorderColor = Color.Transparent
        )
    )
}
