package com.ccstudio.mpaz_pro_6.ui.screens.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.ContentResource
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.common.StatusChip
import com.ccstudio.mpaz_pro_6.ui.components.common.ThemeToggleButton

/**
 * Detalle de un contenido.
 *
 * Consume `GET /api/contenidos/{id}` y muestra la explicación, los ejemplos, las
 * instrucciones y los recursos publicados. Es una vista de solo lectura para el
 * estudiante.
 */
@Composable
fun ContentDetailScreen(
    content: Content?,
    resources: List<ContentResource>,
    isLoading: Boolean,
    error: ApiError?,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            title = "Contenido",
            onNavigateBack = onNavigateBack,
            actions = {
                ThemeToggleButton(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
        )

        when {
            isLoading -> LoadingState(message = "Cargando el contenido...")

            error != null -> ErrorState(error = error, onRetry = onRetry)

            content == null -> EmptyState(
                message = "No se encontró el contenido.",
                modifier = Modifier.padding(16.dp)
            )

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = content.titulo,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusChip(label = content.origen.wireValue)

                    Text(
                        text = "Orden ${content.orden}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                SectionHeader(title = "Explicación")
                Text(
                    text = content.explicacion,
                    style = MaterialTheme.typography.bodyLarge
                )

                if (!content.ejemplos.isNullOrBlank()) {
                    SectionHeader(title = "Ejemplos")
                    Text(
                        text = content.ejemplos,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (!content.instrucciones.isNullOrBlank()) {
                    SectionHeader(title = "Instrucciones")
                    Text(
                        text = content.instrucciones,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                SectionHeader(
                    title = "Recursos",
                    count = resources.size
                )

                if (resources.isEmpty()) {
                    EmptyState(
                        message = "Este contenido no tiene recursos.",
                        description = "Cuando se agregue una imagen o un audio aparecerá aquí."
                    )
                } else {
                    resources.forEach { resource ->
                        ResourceSummary(resource = resource)
                    }
                }
            }
        }
    }
}

/** Resumen de solo lectura de un recurso de contenido. */
@Composable
private fun ResourceSummary(
    resource: ContentResource,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusChip(label = resource.tipo.wireValue)

            if (!resource.descripcion.isNullOrBlank()) {
                Text(
                    text = resource.descripcion,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = resource.url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
