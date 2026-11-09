package com.ccstudio.mpaz_pro_6.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.ui.theme.subjectAccent

/**
 * Nombre de la asignatura con un punto del color que la representa.
 *
 * El backend no guarda colores: el tono se deriva del nombre. Si no hay
 * asignatura ("Sin asignatura") no se dibuja el punto.
 */
@Composable
fun SubjectLabel(
    subjectName: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.labelLarge,
    textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    maxLines: Int = 1,
    dotSize: Dp = 8.dp
) {
    val accent = subjectAccent(subjectName)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (accent != null) {
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .background(accent)
            )
        }

        Text(
            text = subjectName,
            style = style,
            color = textColor,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}
