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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.Content

/**
 * Tarjeta resumen de un contenido.
 *
 * Muestra el título, una descripción breve de la explicación, el número de
 * recursos, los códigos de los objetivos de aprendizaje asociados y el acceso
 * **Ver contenido**. El contenido solo trae `oaId`, así que los códigos de los
 * objetivos los resuelve la pantalla que lista y se entregan como
 * `objectiveLabels`.
 *
 * El orden del contenido no se muestra: solo indica su posición en la lista y no
 * aporta información para la revisión.
 *
 * Las acciones de edición y borrado son opcionales: en una pantalla de solo
 * consulta (por ejemplo, la revisión de la UTP) se omiten y la tarjeta queda como
 * resumen. El borrado siempre se ofrece cuando la pantalla lo habilita; al eliminar
 * un contenido el backend borra en cascada sus recursos.
 */
@Composable
fun ContentCard(
    content: Content,
    resourceCount: Int,
    onViewContent: () -> Unit,
    modifier: Modifier = Modifier,
    objectiveLabels: List<String> = emptyList(),
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
            // Título.
            Text(
                text = content.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // Códigos de los objetivos de aprendizaje, si la pantalla pudo resolverlos.
            if (objectiveLabels.isNotEmpty()) {
                Text(
                    text = objectiveLabels.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Resumen de la explicación.
            Text(
                text = content.explicacion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Recursos asociados.
            Text(
                text = if (resourceCount == 1) "1 recurso" else "$resourceCount recursos",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            // Acceso al contenido completo.
            OutlinedButton(
                onClick = onViewContent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver contenido")
            }

            // Acciones de gestión, solo si la pantalla las habilita.
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
