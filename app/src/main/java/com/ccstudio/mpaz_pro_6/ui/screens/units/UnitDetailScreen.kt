package com.ccstudio.mpaz_pro_6.ui.screens.units

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.activity.ActivityTypeChip
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.common.StatusChip
import com.ccstudio.mpaz_pro_6.ui.components.common.ThemeToggleButton
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitSummaryHeader

/**
 * Detalle de una unidad.
 *
 * Consume `GET /api/unidades/{id}` y muestra los datos de la unidad con acceso a
 * la pantalla de autorización.
 *
 * Para el estudiante agrega el material de la unidad: objetivos de aprendizaje,
 * contenidos publicados y actividades asignadas. Tocar un contenido abre su
 * detalle y tocar una actividad inicia o continúa el intento.
 */
@Composable
fun UnitDetailScreen(
    unidad: LearningUnit?,
    asignatura: Subject?,
    isLoading: Boolean,
    error: ApiError?,
    onAutorizar: () -> Unit,
    onRetry: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    mostrarMaterial: Boolean = false,
    objetivos: List<LearningObjective> = emptyList(),
    contenidos: List<Content> = emptyList(),
    actividades: List<Activity> = emptyList(),
    onOpenContent: (Content) -> Unit = {},
    onResolveActivity: (Activity) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        AppTopBar(
            title = "Detalle de la unidad",
            onNavigateBack = onNavigateBack,
            actions = {
                ThemeToggleButton(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                isLoading -> LoadingState(message = "Cargando la unidad...")

                error != null -> ErrorState(error = error, onRetry = onRetry)

                unidad != null -> {

                    UnitSummaryHeader(
                        unit = unidad,
                        subjectName = asignatura?.nombre ?: "Sin asignatura"
                    )

                    SectionHeader(title = "Datos de la unidad")

                    DatoLinea(etiqueta = "Asignatura", valor = asignatura?.nombre ?: "-")
                    DatoLinea(etiqueta = "Nivel educativo", valor = unidad.nivelEducativo)
                    DatoLinea(etiqueta = "Título", valor = unidad.titulo)
                    DatoLinea(etiqueta = "Orden", valor = unidad.orden.toString())
                    DatoLinea(etiqueta = "Estado", valor = unidad.estado.wireValue)
                    DatoLinea(
                        etiqueta = "Autorizada por",
                        valor = unidad.autorizadaPorId?.let { "Usuario $it" } ?: "-"
                    )
                    DatoLinea(
                        etiqueta = "Fecha de autorización",
                        valor = unidad.fechaAutorizacion?.toString() ?: "-"
                    )

                    if (unidad.estado.esPendienteDeRevision() ||
                        unidad.estado == ContentStatus.AUTHORIZED
                    ) {
                        AppButton(
                            text = if (unidad.estado == ContentStatus.AUTHORIZED) {
                                "Publicar unidad"
                            } else {
                                "Autorizar unidad"
                            },
                            onClick = onAutorizar,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (mostrarMaterial) {
                        StudentMaterial(
                            objetivos = objetivos,
                            contenidos = contenidos,
                            actividades = actividades,
                            onOpenContent = onOpenContent,
                            onResolveActivity = onResolveActivity
                        )
                    }
                }
            }
        }
    }
}

/** Objetivos, contenidos y actividades del estudiante dentro de la unidad. */
@Composable
private fun StudentMaterial(
    objetivos: List<LearningObjective>,
    contenidos: List<Content>,
    actividades: List<Activity>,
    onOpenContent: (Content) -> Unit,
    onResolveActivity: (Activity) -> Unit
) {
    SectionHeader(
        title = "Objetivos de aprendizaje",
        count = objetivos.size
    )

    if (objetivos.isEmpty()) {
        EmptyState(
            message = "No hay objetivos de aprendizaje.",
            modifier = Modifier.padding(16.dp)
        )
    } else {
        objetivos.forEach { objective ->
            ObjectiveSummary(objective = objective)

            val contentsOfObjective = contenidos.filter { it.oaId == objective.id }

            if (contentsOfObjective.isEmpty()) {
                Text(
                    text = "Sin contenidos para este objetivo.",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                contentsOfObjective.forEach { content ->
                    ContentSummary(
                        content = content,
                        onClick = { onOpenContent(content) }
                    )
                }
            }
        }
    }

    SectionHeader(
        title = "Actividades",
        count = actividades.size
    )

    if (actividades.isEmpty()) {
        EmptyState(
            message = "No hay actividades asignadas.",
            description = "Cuando el docente asigne una actividad aparecerá aquí.",
            modifier = Modifier.padding(16.dp)
        )
    } else {
        actividades.forEach { activity ->
            ActivitySummary(
                activity = activity,
                onClick = { onResolveActivity(activity) }
            )
        }
    }
}

/** Resumen de un objetivo de aprendizaje. */
@Composable
private fun ObjectiveSummary(
    objective: LearningObjective,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Text(
                    text = objective.codigo ?: "Sin código",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Text(
                text = objective.descripcion,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = objective.eje,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Resumen de un contenido; al tocarlo se abre su detalle. */
@Composable
private fun ContentSummary(
    content: Content,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusChip(label = content.origen.wireValue)

            Text(
                text = content.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = content.explicacion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Toca para leer el contenido completo",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/** Resumen de una actividad; al tocarla se abre el intento. */
@Composable
private fun ActivitySummary(
    activity: Activity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActivityTypeChip(activityType = activity.tipo)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Toca para resolver",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Línea de dato con su etiqueta.
 */
@Composable
private fun DatoLinea(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
