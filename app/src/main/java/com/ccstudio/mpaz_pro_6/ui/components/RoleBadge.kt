package com.ccstudio.mpaz_pro_6.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Estilo único para las etiquetas de rol de la app.
 *
 * Lo comparten los chips de rol y el badge del listado de usuarios para que
 * ambos usen la misma familia, tamaño y peso (Geist, labelLarge, 14sp Medium).
 */
@Composable
internal fun roleLabelStyle(): TextStyle = MaterialTheme.typography.labelMedium.copy(
    fontWeight = FontWeight.SemiBold
)

/**
 * Etiqueta estática con el nombre de un rol.
 *
 * Comparte el estilo tipográfico con [AppFilterChip] para que el badge del card
 * y los chips de selección se vean iguales.
 */
@Composable
fun RoleBadge(
    text: String,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = contentColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = roleLabelStyle()
        )
    }
}
