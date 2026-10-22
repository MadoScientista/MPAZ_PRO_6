package com.ccstudio.mpaz_pro_6.ui.screens.units

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.ContentResource
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitStatusChip
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitSummaryHeader

/**
 * Pantalla de detalle de una unidad.
 *
 * Muestra la información principal de la unidad y la estructura pedagógica
 * (objetivos, contenidos, recursos y actividades) para revisión efectiva por
 * parte del UTP. Todo el contenido es solo lectura.
 */
@Composable
fun UnitDetailScreen(
    unidad: LearningUnit?,
    asignatura: Subject?,
    objetivos: List<LearningObjective>,
    contenidosPorObjetivo: Map<Int, List<Content>>,
    recursosPorContenido: Map<Int, List<ContentResource>>,
    actividades: List<Activity>,
    isLoading: Boolean,
    error: ApiError?,
    onAutorizar: () -> Unit,
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        AppTopBar(
            title = "Detalle de la unidad",
            onNavigateBack = onNavigateBack
        )

        when {
            isLoading && unidad == null -> LoadingState(message = "Cargando la unidad...")

            error != null && unidad == null -> ErrorState(error = error, onRetry = onRetry)

            unidad != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        UnitSummaryHeader(
                            unit = unidad,
                            subjectName = asignatura?.nombre ?: "Sin asignatura"
                        )
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Información general",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(text = "Nivel educativo: ${unidad.nivelEducativo}")
                                Text(text = "Orden: ${unidad.orden}")
                                Text(text = "Asignatura: ${asignatura?.nombre ?: "Sin asignatura"}")
                                Text(text = "Estado:")
                                UnitStatusChip(status = unidad.estado)
                            }
                        }
                    }

                    item {
                        SectionHeader(title = "Objetivos de Aprendizaje", count = objetivos.size)
                    }

                    if (objetivos.isEmpty()) {
                        item {
                            Text(
                                text = "Esta unidad no tiene objetivos registrados.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // Las claves de cada bloque se prefijan: los ids de objetivo y
                        // de actividad son secuencias independientes y se solapan.
                        items(objetivos, key = { "objetivo-${it.id}" }) { objetivo ->
                            ObjectiveCard(
                                objetivo = objetivo,
                                contenidos = contenidosPorObjetivo[objetivo.id].orEmpty(),
                                recursosPorContenido = recursosPorContenido
                            )
                        }
                    }

                    item {
                        SectionHeader(title = "Actividades", count = actividades.size)
                    }

                    if (actividades.isEmpty()) {
                        item {
                            Text(
                                text = "Esta unidad no tiene actividades registradas.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(actividades, key = { "actividad-${it.id}" }) { actividad ->
                            ActivityReviewCard(actividad = actividad)
                        }
                    }

                    item {
                        AppButton(
                            text = "Autorizar / Revisar",
                            onClick = onAutorizar,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ObjectiveCard(
    objetivo: LearningObjective,
    contenidos: List<Content>,
    recursosPorContenido: Map<Int, List<ContentResource>>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = objetivo.codigo?.let { "$it - ${objetivo.descripcion}" } ?: objetivo.descripcion,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = "Eje: ${objetivo.eje}")
            objetivo.textoReferencia?.let {
                Text(text = "Referencia: $it", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (contenidos.isEmpty()) {
                Text(
                    text = "Sin contenidos asociados.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Contenidos",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                contenidos.sortedBy { it.orden }.forEach { contenido ->
                    ContentReviewCard(
                        contenido = contenido,
                        recursos = recursosPorContenido[contenido.id].orEmpty()
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentReviewCard(
    contenido: Content,
    recursos: List<ContentResource>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "${contenido.orden}. ${contenido.titulo}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = contenido.explicacion)
            contenido.ejemplos?.let {
                Text(text = "Ejemplos: $it", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            contenido.instrucciones?.let {
                Text(text = "Instrucciones: $it", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // Origen omitido: no es relevante para la revisión del UTP.

            if (recursos.isNotEmpty()) {
                Text(
                    text = "Recursos",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                recursos.forEach { recurso ->
                    Text(
                        text = "- ${recurso.tipo.name}: ${recurso.url}${recurso.descripcion?.let { " ($it)" } ?: ""}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityReviewCard(actividad: Activity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "${actividad.orden}. ${actividad.titulo}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = when (actividad.tipo) {
                    ActivityType.ACTIVITY -> "Tipo: ACTIVIDAD"
                    ActivityType.CHALLENGE -> "Tipo: DESAFIO"
                    ActivityType.FINAL_TEST -> "Tipo: PRUEBA_FINAL"
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
