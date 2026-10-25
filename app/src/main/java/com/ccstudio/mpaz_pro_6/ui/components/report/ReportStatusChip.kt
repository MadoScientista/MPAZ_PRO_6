package com.ccstudio.mpaz_pro_6.ui.components.report

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.ReportStatus

/**
 * Chip del estado de validación de un informe pedagógico.
 *
 * Se comparte entre la tarjeta del listado y el detalle del informe.
 */
@Composable
fun ReportStatusChip(
    status: ReportStatus,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        ReportStatus.GENERATED -> MaterialTheme.colorScheme.surfaceVariant
        ReportStatus.PENDING_VALIDATION -> MaterialTheme.colorScheme.tertiaryContainer
        ReportStatus.VALIDATED -> MaterialTheme.colorScheme.primaryContainer
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
