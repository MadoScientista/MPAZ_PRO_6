package com.ccstudio.mpaz_pro_6.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * FilterChip unificado de la app.
 *
 * Fija el estilo del label con [roleLabelStyle] (Geist, 14sp, Medium) y el ícono
 * en 18dp, para que todos los chips de la app se vean iguales y compartan
 * tipografía con [RoleBadge].
 */
@Composable
fun AppFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        label = {
            Text(
                text = label,
                style = roleLabelStyle()
            )
        },
        leadingIcon = leadingIcon?.let { icon ->
            {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        }
    )
}
