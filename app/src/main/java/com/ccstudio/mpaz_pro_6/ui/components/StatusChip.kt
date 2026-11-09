package com.ccstudio.mpaz_pro_6.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.ui.theme.LocalAppColors

@Composable
fun StatusChip(
    statusName: String,
    modifier: Modifier = Modifier
) {
    val appColors = LocalAppColors.current
    val colors = when (statusName.uppercase()) {
        "EN_REVISION", "EN REVISIÓN" -> appColors.warning
        "AUTORIZADO", "ACTIVO" -> appColors.success
        "BORRADOR", "INACTIVO", "MANUAL" -> appColors.neutral
        "PUBLICADO", "AUTOMÁTICO" -> appColors.info
        else -> appColors.neutral
    }

    Surface(
        modifier = modifier,
        color = colors.container,
        contentColor = colors.content,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = statusName.replace("_", " "),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}
