package com.ccstudio.mpaz_pro_6.ui.components.activity

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.ActivityType

/**
 * Chip del tipo de actividad.
 *
 * Traduce el `ActivityType` a su valor wire (`ACTIVIDAD`, `DESAFIO`,
 * `PRUEBA_FINAL`) y aplica un color del tema por tipo. La `PRUEBA_FINAL` usa el
 * color de error porque tiene reglas propias: solo se admite una por unidad y no
 * se puede asociar a una asignación.
 */
@Composable
fun ActivityTypeChip(
    activityType: ActivityType,
    modifier: Modifier = Modifier
) {
    val color = when (activityType) {
        ActivityType.ACTIVITY -> MaterialTheme.colorScheme.secondaryContainer
        ActivityType.CHALLENGE -> MaterialTheme.colorScheme.tertiaryContainer
        ActivityType.FINAL_TEST -> MaterialTheme.colorScheme.errorContainer
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color,
        contentColor = contentColorFor(color)
    ) {
        Text(
            text = activityType.wireValue,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelLarge
        )
    }
}
