package com.ccstudio.mpaz_pro_6.ui.components.unit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit

/**
 * Tarjeta de una unidad.
 *
 * Muestra el resumen de una `LearningUnit`: asignatura, título, nivel educativo y
 * estado. La unidad solo trae el `asignaturaId`, así que el nombre de la asignatura
 * lo resuelve la pantalla que la lista y se lo entrega como `subjectName`.
 *
 * La acción de autorizar solo se muestra al rol `UTP`: el resto de los roles
 * consulta la unidad pero no la autoriza.
 */
@Composable
fun UnitCard(
    unit: LearningUnit,
    subjectName: String,
    onViewDetail: () -> Unit,
    onAuthorize: () -> Unit,
    puedeAutorizar: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Asignatura.
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Text(
                    text = subjectName,
                    modifier = Modifier.padding(
                        horizontal = 10.dp,
                        vertical = 5.dp
                    ),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Título de la unidad.
            Text(
                text = unit.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Nivel educativo y estado de la unidad.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = unit.nivelEducativo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                UnitStatusChip(status = unit.estado)
            }

            // Acciones: solo la UTP autoriza y publica.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetail,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ver detalle")
                }

                if (puedeAutorizar) {
                    Button(
                        onClick = onAuthorize,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Autorizar")
                    }
                }
            }
        }
    }
}
