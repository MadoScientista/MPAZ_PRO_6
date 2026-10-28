package com.ccstudio.mpaz_pro_6.ui.screens.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.subject.SubjectCard

/**
 * Detalle de un curso.
 *
 * Muestra el nombre, el año, la cantidad de estudiantes y dos secciones principales:
 * 1. Las asignaturas del curso (cada tarjeta lleva a la vista de sus unidades).
 * 2. Los estudiantes del curso en tarjetas horizontales (icono a la izquierda,
 *    nombre a la derecha y username comenzando con `@`).
 *
 * La barra superior la monta el `Scaffold` de `AppNavGraph`.
 */
@Composable
fun CourseDetailScreen(
    course: Course?,
    asignaturas: List<Subject>,
    unidadesPorAsignatura: Map<Int, Int>,
    estudiantes: List<User>,
    isLoading: Boolean,
    error: ApiError?,
    onRetry: () -> Unit,
    onViewSubject: (Subject) -> Unit,
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

        else -> Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado del curso
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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

            // Sección 1: Asignaturas del curso
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    title = "Asignaturas",
                    count = asignaturas.size
                )

                when {
                    isLoading && asignaturas.isEmpty() -> LoadingState(message = "Cargando asignaturas...")
                    asignaturas.isEmpty() -> EmptyState(message = "Este curso no tiene asignaturas asociadas.")
                    else -> Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        asignaturas.forEach { subject ->
                            val unitCount = unidadesPorAsignatura[subject.id] ?: 0
                            SubjectCard(
                                subject = subject,
                                unitCount = unitCount,
                                pendingCount = 0,
                                onViewDetail = { onViewSubject(subject) },
                                onEdit = {},
                                onDelete = {},
                                showActions = false,
                                showPendingChip = false
                            )
                        }
                    }
                }
            }

            // Sección 2: Estudiantes del curso
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SectionHeader(
                    title = "Estudiantes",
                    count = estudiantes.size,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                when {
                    isLoading && estudiantes.isEmpty() -> LoadingState(
                        message = "Cargando estudiantes...",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    estudiantes.isEmpty() -> EmptyState(
                        message = "Este curso no tiene estudiantes.",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    else -> LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(estudiantes, key = { it.id }) { student ->
                            StudentCourseCard(student = student)
                        }
                    }
                }
            }
        }
    }
}

/** Tarjeta horizontal de un estudiante dentro del detalle del curso. */
@Composable
private fun StudentCourseCard(
    student: User,
    modifier: Modifier = Modifier
) {
    val usernameFormatted = if (student.username.startsWith("@")) {
        student.username
    } else {
        "@${student.username}"
    }

    Card(
        modifier = modifier
            .width(220.dp)
            .height(76.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = student.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = usernameFormatted,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
