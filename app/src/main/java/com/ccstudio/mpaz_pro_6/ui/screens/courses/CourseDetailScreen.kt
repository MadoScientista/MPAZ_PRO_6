package com.ccstudio.mpaz_pro_6.ui.screens.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState

/**
 * Detalle de un curso.
 *
 * Muestra el nombre, el año, la cantidad de estudiantes y un grid de dos
 * columnas con una tarjeta por estudiante. La barra superior la monta el
 * `Scaffold` de `AppNavGraph`.
 */
@Composable
fun CourseDetailScreen(
    course: Course?,
    estudiantes: List<User>,
    isLoading: Boolean,
    error: ApiError?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    when {
        isLoading && course == null -> LoadingState(
            message = "Cargando el curso...",
            modifier = Modifier.padding(16.dp)
        )

        error != null && course == null -> ErrorState(
            error = error,
            onRetry = onRetry,
            modifier = Modifier.padding(16.dp)
        )

        course == null -> EmptyState(
            message = "No se encontró el curso.",
            modifier = Modifier.padding(16.dp)
        )

        else -> LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = course.nombre,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${course.anio} · ${studentCountLabel(estudiantes.size)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            when {
                isLoading && estudiantes.isEmpty() -> item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    LoadingState(message = "Cargando estudiantes...")
                }

                error != null && estudiantes.isEmpty() -> item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    ErrorState(error = error, onRetry = onRetry)
                }

                estudiantes.isEmpty() -> item(
                    span = { GridItemSpan(maxLineSpan) }
                ) {
                    EmptyState(message = "Este curso no tiene estudiantes.")
                }

                else -> items(estudiantes, key = { it.id }) { student ->
                    StudentCourseCard(student = student)
                }
            }
        }
    }
}

/** Tarjeta compacta de un estudiante dentro del detalle del curso. */
@Composable
private fun StudentCourseCard(
    student: User,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(26.dp)
                )
            }

            Text(
                text = student.nombre,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "@${student.username}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Texto de la cantidad de estudiantes, con singular y plural. */
private fun studentCountLabel(count: Int): String = when (count) {
    0 -> "Sin estudiantes"
    1 -> "1 estudiante"
    else -> "$count estudiantes"
}
