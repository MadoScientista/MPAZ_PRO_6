package com.ccstudio.mpaz_pro_6.ui.components.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Chip de estado genérico.
 *
 * Muestra una etiqueta de estado ya resuelta por la pantalla y usa los colores del
 * tema. Los chips específicos de dominio (estado de unidad, tipo de actividad,
 * estado de informe) viven en su propio componente porque conocen el enum y su
 * traducción al valor wire.
 *
 * Por defecto usa el color neutro (`surfaceVariant`). La pantalla elige el color
 * semántico cuando el estado lo requiere, siempre desde `MaterialTheme.colorScheme`.
 */
@Composable
fun StatusChip(
    label: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = contentColorFor(containerColor)
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelLarge
        )
    }
}
