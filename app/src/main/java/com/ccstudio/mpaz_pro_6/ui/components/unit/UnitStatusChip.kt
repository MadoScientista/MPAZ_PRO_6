package com.ccstudio.mpaz_pro_6.ui.components.unit

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus

/**
 * Chip del estado de una unidad.
 *
 * Traduce el `ContentStatus` a su valor wire (`BORRADOR`, `EN_REVISION`,
 * `AUTORIZADO`, `PUBLICADO`) y lo muestra con el color del tema que le corresponde.
 */
@Composable
fun UnitStatusChip(
    status: ContentStatus,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        ContentStatus.DRAFT -> MaterialTheme.colorScheme.surfaceVariant
        ContentStatus.IN_REVIEW -> MaterialTheme.colorScheme.tertiaryContainer
        ContentStatus.AUTHORIZED -> MaterialTheme.colorScheme.secondaryContainer
        ContentStatus.PUBLISHED -> MaterialTheme.colorScheme.primaryContainer
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color,
        contentColor = contentColorFor(color)
    ) {
        Text(
            text = status.wireValue,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelLarge
        )
    }
}
