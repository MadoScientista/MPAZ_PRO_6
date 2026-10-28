package com.ccstudio.mpaz_pro_6.ui.components.course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.Course

/**
 * Tarjeta de un curso en el listado.
 *
 * Muestra el badge, el nombre, el año y, cuando se conoce, la cantidad de
 * estudiantes, más un botón «Ver detalle». La edición y el borrado quedan como
 * iconos, igual que en `SubjectCard`. El borrado siempre se ofrece; si el curso
 * tiene estudiantes o asignaciones, el backend responde `409 Conflict` y la
 * pantalla lo explica.
 */
@Composable
fun CourseCard(
    course: Course,
    onViewDetail: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    studentCount: Int? = null,
    showActions: Boolean = true
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.School,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = course.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = studentCount?.let {
                            "${course.anio} · ${studentCountLabel(it)}"
                        } ?: course.anio.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (showActions) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar curso"
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Eliminar curso",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = onViewDetail,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver detalle")
            }
        }
    }
}

/** Texto de la cantidad de estudiantes, con singular y plural. */
private fun studentCountLabel(count: Int): String = when (count) {
    0 -> "Sin estudiantes"
    1 -> "1 estudiante"
    else -> "$count estudiantes"
}
