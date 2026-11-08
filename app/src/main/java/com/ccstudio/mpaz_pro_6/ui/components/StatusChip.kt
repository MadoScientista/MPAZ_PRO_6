package com.ccstudio.mpaz_pro_6.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusChip(
    statusName: String,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, contentColor) = when (statusName.uppercase()) {
        "EN_REVISION", "EN REVISIÓN" -> Color(0xFFFFF3E0) to Color(0xFFE65100) // Soft Orange
        "AUTORIZADO" -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)                 // Soft Green
        "BORRADOR" -> Color(0xFFECEFF1) to Color(0xFF455A64)                   // Soft Gray
        "PUBLICADO" -> Color(0xFFE3F2FD) to Color(0xFF1565C0)                  // Soft Blue
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = modifier,
        color = backgroundColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = statusName.replace("_", " "),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
