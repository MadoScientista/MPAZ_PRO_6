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
 * Las acciones dependen del `ContentStatus`: el botón de autorizar solo debe
 * aparecer cuando la unidad está en `BORRADOR` o `EN_REVISION` y el usuario tiene
 * rol `UTP`.
 */
@Composable
fun UnitCard(
    unit: LearningUnit,
    subjectName: String,
    onViewDetail: () -> Unit,
    onAuthorize: (() -> Unit)?,
    modifier: Modifier = Modifier
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subjectName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )

                UnitStatusChip(status = unit.estado)
            }

            Text(
                text = unit.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = unit.nivelEducativo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )


            // Acciones de la jefa de UTP.
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetail,
                    modifier = modifier.weight(1f)
                ) {
                    Text("Ver detalle")
                }

                if(onAuthorize != null){
                    Button(
                        onClick = onAuthorize as () -> Unit,
                        modifier = modifier.weight(1f)
                    ) {
                        Text("Autorizar")
                    }
                }

            }
        }
    }
}
