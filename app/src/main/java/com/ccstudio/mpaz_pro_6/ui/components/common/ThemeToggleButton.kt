package com.ccstudio.mpaz_pro_6.ui.components.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Botón para alternar entre el tema claro y oscuro.
 *
 * Muestra la acción disponible: luna cuando el tema actual es claro y sol
 * cuando es oscuro. El estado lo resuelve `MainActivity` y lo entrega por
 * parámetro, igual que el resto de los componentes de la app.
 */
@Composable
fun ThemeToggleButton(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onToggleTheme,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (isDarkTheme) {
                Icons.Filled.LightMode
            } else {
                Icons.Filled.DarkMode
            },
            contentDescription = if (isDarkTheme) {
                "Activar modo claro"
            } else {
                "Activar modo oscuro"
            }
        )
    }
}
