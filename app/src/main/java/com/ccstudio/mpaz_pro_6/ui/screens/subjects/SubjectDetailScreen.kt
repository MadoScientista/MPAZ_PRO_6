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
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitCard

/**
 * Detalle de una asignatura.
 *
 * Muestra el nombre, la cantidad de unidades y el listado de sus unidades. Cada
 * unidad abre su detalle y, si está pendiente o autorizada y el rol lo permite,
 * ofrece la autorización. El estudiante recibe `onAuthorize = null` y solo ve
 * sus unidades publicadas. La barra superior la monta el `Scaffold` de
 * `AppNavGraph`.
 */
@Composable
fun SubjectDetailScreen(
    subject: Subject?,
    unidades: List<LearningUnit>,
    isLoading: Boolean,
    error: ApiError?,
    onViewUnit: (LearningUnit) -> Unit,
    onAuthorize: ((LearningUnit) -> Unit)?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
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
                            text = subject.nombre,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

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
                            onAuthorize = if (
                                onAuthorize != null &&
                                (unit.estado == ContentStatus.IN_REVIEW ||
                                    unit.estado == ContentStatus.AUTHORIZED)
                            ) {
                                { onAuthorize(unit) }
                            } else {
                                null
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
