package com.ccstudio.mpaz_pro_6.ui.components.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.ContentResourceType
import com.ccstudio.mpaz_pro_6.data.model.ContentResource

/**
 * Tarjeta de un recurso de contenido.
 *
 * Muestra el tipo, la URL y la descripción del recurso, con acceso a abrir el
 * enlace. La descripción se muestra solo cuando está definida.
 * Las acciones de edición y borrado son opcionales: en una pantalla de solo
 * consulta se omiten. Cuando la pantalla las habilita, el borrado siempre se
 * ofrece y la pantalla explica cualquier `409 Conflict`.
 */
@Composable
fun ContentResourceCard(
    resource: ContentResource,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tipo de recurso.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ResourceTypeChip(type = resource.tipo)
            }

            // Descripción, solo si está definida.
            if (!resource.descripcion.isNullOrBlank()) {
                Text(
                    text = resource.descripcion,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // URL del recurso.
            Text(
                text = resource.url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Acciones.
            OutlinedButton(
                onClick = onOpen,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Abrir enlace")
            }

            if (onEdit != null || onDelete != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onEdit != null) {
                        OutlinedButton(
                            onClick = onEdit,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Editar")
                        }
                    }

                    if (onDelete != null) {
                        Button(
                            onClick = onDelete,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Text("Eliminar")
                        }
                    }
                }
            }
        }
    }
}

/** Chip del tipo de recurso (`IMAGEN` o `AUDIO`). */
@Composable
private fun ResourceTypeChip(
    type: ContentResourceType,
    modifier: Modifier = Modifier
) {
    val color = when (type) {
        ContentResourceType.IMAGE -> MaterialTheme.colorScheme.secondaryContainer
        ContentResourceType.AUDIO -> MaterialTheme.colorScheme.tertiaryContainer
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color,
        contentColor = contentColorFor(color)
    ) {
        Text(
            text = type.wireValue,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
            style = MaterialTheme.typography.labelLarge
        )
    }
}
