package com.ccstudio.mpaz_pro_6.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.ui.theme.LocalTopBarColors

/**
 * Cabecera de perfil compacta para la barra superior.
 *
 * Se muestra en todos los destinos de nivel superior (las pestañas): la
 * identidad del usuario reemplaza al título, que el contenido de cada listado
 * ya comunica por su cuenta. Toma los colores de [LocalTopBarColors]: sobre el
 * azul de marca en modo claro y sobre la superficie neutra en oscuro.
 */
@Composable
fun ProfileHeader(
    userName: String,
    role: UserRole,
    modifier: Modifier = Modifier
) {
    val barColors = LocalTopBarColors.current
    val nameColor = barColors.content.takeOrElse {
        MaterialTheme.colorScheme.onSurface
    }
    val roleColor = barColors.subtitleContent.takeOrElse {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val avatarColor = barColors.avatarContainer.takeOrElse {
        MaterialTheme.colorScheme.primaryContainer
    }
    val avatarContentColor = barColors.avatarContent.takeOrElse {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // El avatar es decorativo: el nombre y el rol ya se leen al lado.
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(avatarColor)
                .clearAndSetSemantics { },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = avatarContentColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = if (userName.isBlank()) "Hola" else "Hola, $userName",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = nameColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = role.label,
                style = MaterialTheme.typography.bodySmall,
                color = roleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
