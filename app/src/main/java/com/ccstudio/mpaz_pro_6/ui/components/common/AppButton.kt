package com.ccstudio.mpaz_pro_6.ui.components.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

/**
 * Botón primario de la app.
 *
 * Unifica el uso de `Button`, `OutlinedButton` y `TextButton` en un solo
 * componente con variantes, para que el espaciado y los colores sean consistentes.
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: AppButtonVariant = AppButtonVariant.PRIMARY,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    when (variant) {

        AppButtonVariant.PRIMARY -> Button(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor
            )
        ) {
            Text(text = text, textAlign = TextAlign.Center)
        }

        AppButtonVariant.OUTLINED -> OutlinedButton(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            enabled = enabled
        ) {
            Text(text = text, textAlign = TextAlign.Center)
        }

        AppButtonVariant.TEXT -> TextButton(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            enabled = enabled
        ) {
            Text(text = text, textAlign = TextAlign.Center)
        }
    }
}

/** Variantes disponibles del [AppButton]. */
enum class AppButtonVariant {
    /** Acción principal, con fondo relleno. */
    PRIMARY,

    /** Acción secundaria, con borde. */
    OUTLINED,

    /** Acción terciaria, solo texto. */
    TEXT
}
