package com.ccstudio.mpaz_pro_6.ui.components.unit

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.ui.theme.subjectAccent

/**
 * Nombre de la asignatura con un punto del color que la representa.
 *
 * El texto va en gris (`onSurfaceVariant`) para que no compita con el título de
 * la unidad; el color de la materia queda solo en el punto. Si no hay materia
 * ("Sin asignatura") no se dibuja el punto.
 */
@Composable
fun SubjectLabel(
    subjectName: String,
    modifier: Modifier = Modifier
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
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(accent)
            )
        }

        Text(
            text = subjectName,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
