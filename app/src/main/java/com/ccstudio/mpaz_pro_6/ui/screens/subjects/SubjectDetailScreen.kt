package com.ccstudio.mpaz_pro_6.ui.screens.subjects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitCard

/**
 * Detalle de una asignatura.
 *
 * Muestra el nombre, el curso (si se proporcionó), la cantidad de unidades y el listado de sus unidades. Cada
 * unidad abre su detalle. La acción de autorización (`onAuthorize`) es opcional y
 * solo se ofrece cuando el usuario tiene rol UTP para unidades en revisión o autorizadas.
 * La barra superior la monta el `Scaffold` de `AppNavGraph`.
 */
@Composable
fun SubjectDetailScreen(
    subject: Subject?,
    unidades: List<LearningUnit>,
    isLoading: Boolean,
    error: ApiError?,
    onViewUnit: (LearningUnit) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    course: Course? = null,
    onAuthorize: ((LearningUnit) -> Unit)? = null,
) {
    Column(modifier = modifier.fillMaxSize()) {
        when {
            isLoading && subject == null -> LoadingState(
                message = "Cargando la asignatura...",
                modifier = Modifier.padding(16.dp)
            )

            error != null && subject == null -> ErrorState(
                error = error,
                onRetry = onRetry,
                modifier = Modifier.padding(16.dp)
            )

            subject == null -> EmptyState(
                message = "No se encontró la asignatura.",
                modifier = Modifier.padding(16.dp)
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (course != null) "${subject.nombre} — ${course.nombre}" else subject.nombre,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        if (course != null) {
                            Text(
                                text = "Curso ${course.nombre} (${course.anio})",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = unitCountLabel(unidades.size),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (isLoading && unidades.isEmpty()) {
                    item {
                        LoadingState(message = "Cargando unidades...")
                    }
                } else if (error != null && unidades.isEmpty()) {
                    item {
                        ErrorState(error = error, onRetry = onRetry)
                    }
                } else if (unidades.isEmpty()) {
                    item {
                        EmptyState(message = "Esta asignatura no tiene unidades.")
                    }
                } else {
                    items(unidades, key = { it.id }) { unit ->
                        UnitCard(
                            unit = unit,
                            subjectName = subject.nombre,
                            onViewDetail = { onViewUnit(unit) },
                            onAuthorize = onAuthorize?.let { authorize ->
                                if (unit.estado == ContentStatus.IN_REVIEW ||
                                    unit.estado == ContentStatus.AUTHORIZED
                                ) {
                                    { authorize(unit) }
                                } else {
                                    null
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/** Texto de la cantidad de unidades, con singular y plural. */
private fun unitCountLabel(count: Int): String = when (count) {
    0 -> "Sin unidades"
    1 -> "1 unidad"
    else -> "$count unidades"
}
