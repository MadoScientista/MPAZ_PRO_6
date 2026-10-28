package com.ccstudio.mpaz_pro_6.ui.screens.units

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.activity.ActivityCard
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButtonVariant
import com.ccstudio.mpaz_pro_6.ui.components.common.CounterCard
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.content.ContentCard
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitSummaryHeader

/**
 * Detalle de una unidad.
 *
 * Muestra el resumen de la unidad, sus contenidos y su prueba final. Las
 * actividades regulares viven dentro del detalle de cada contenido; la prueba
 * final se mantiene aquí porque es transversal a la unidad. La UTP y el docente
 * ven todo el material en modo lectura; el estudiante ve los contenidos
 * publicados y la prueba final habilitada, y puede resolverla.
 *
 * La autorización solo se ofrece a la UTP y solo en los estados que el backend
 * admite: `EN_REVISION` para autorizar y `AUTORIZADO` para publicar.
 */
@Composable
fun UnitDetailScreen(
    unidad: LearningUnit?,
    asignatura: Subject?,
    contenidos: List<Content>,
    objetivos: List<LearningObjective>,
    actividades: List<Activity>,
    recursosPorContenido: Map<Int, Int>,
    actividadesPorContenido: Map<Int, Int>,
    preguntasPorActividad: Map<Int, Int>,
    isLoading: Boolean,
    error: ApiError?,
    onViewContent: (Content) -> Unit,
    onOpenActivity: (Activity) -> Unit,
    onAutorizar: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    puedeAutorizar: Boolean = false,
    puedeResolverActividades: Boolean = false,
    puedeCrearContenido: Boolean = false,
    puedeEnviarRevision: Boolean = false,
    isSendingReview: Boolean = false,
    mensajeRevision: String? = null,
    onCreateContent: (() -> Unit)? = null,
    onEnviarRevision: (() -> Unit)? = null
) {
    // La prueba final se presenta por separado: es única por unidad y no forma
    // parte de la lista de actividades regulares.
    val pruebaFinal = actividades.firstOrNull { it.tipo == ActivityType.FINAL_TEST }
    val actividadesRegulares = actividades.filter { it.tipo != ActivityType.FINAL_TEST }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when {
                isLoading && unidad == null -> LoadingState(message = "Cargando la unidad...")

                error != null -> ErrorState(error = error, onRetry = onRetry)

                unidad != null -> {
                    UnitSummaryHeader(
                        unit = unidad,
                        subjectName = asignatura?.nombre ?: "Sin asignatura"
                    )

                    SectionHeader(
                        title = "Resumen",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CounterCard(
                            titulo = "Contenidos",
                            valor = contenidos.size,
                            modifier = Modifier.weight(1f)
                        )

                        CounterCard(
                            titulo = "Actividades",
                            valor = actividadesRegulares.size,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    SectionHeader(
                        title = "Contenidos",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        count = contenidos.size
                    )

                    if (puedeCrearContenido &&
                        unidad.estado == ContentStatus.DRAFT &&
                        onCreateContent != null
                    ) {
                        AppButton(
                            text = "Crear nuevo contenido",
                            onClick = onCreateContent,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            variant = AppButtonVariant.OUTLINED
                        )
                    }

                    if (contenidos.isEmpty()) {
                        EmptyState(
                            message = "Esta unidad todavía no tiene contenidos.",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        contenidos.forEach { content ->
                            ContentCard(
                                content = content,
                                resourceCount = recursosPorContenido[content.id] ?: 0,
                                activityCount = actividadesPorContenido[content.id] ?: 0,
                                onViewContent = { onViewContent(content) },
                                objectiveLabels = listOfNotNull(
                                    etiquetaObjetivo(content.oaId, objetivos)
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    SectionHeader(
                        title = "Prueba final",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        count = if (pruebaFinal != null) 1 else null
                    )

                    if (pruebaFinal == null) {
                        EmptyState(
                            message = "Esta unidad todavía no tiene una prueba final.",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        ActivityCard(
                            activity = pruebaFinal,
                            questionsCount = preguntasPorActividad[pruebaFinal.id] ?: 0,
                            unitTitle = null,
                            onViewDetail = if (puedeResolverActividades) {
                                { onOpenActivity(pruebaFinal) }
                            } else {
                                null
                            },
                            detailLabel = "Resolver",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    if (mensajeRevision != null) {
                        Text(
                            text = mensajeRevision,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (puedeEnviarRevision &&
                        unidad.estado == ContentStatus.DRAFT &&
                        onEnviarRevision != null
                    ) {
                        AppButton(
                            text = if (isSendingReview) {
                                "Enviando..."
                            } else {
                                "Enviar a revisión"
                            },
                            onClick = onEnviarRevision,
                            enabled = !isSendingReview,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    if (puedeAutorizar &&
                        (unidad.estado == ContentStatus.IN_REVIEW ||
                                unidad.estado == ContentStatus.AUTHORIZED)
                    ) {
                        AppButton(
                            text = if (unidad.estado == ContentStatus.AUTHORIZED) {
                                "Publicar unidad"
                            } else {
                                "Autorizar unidad"
                            },
                            onClick = onAutorizar,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Código del objetivo de aprendizaje de un contenido.
 *
 * Devuelve `null` cuando el objetivo no tiene código: la tarjeta solo muestra el
 * código, sin prefijos ni etiquetas adicionales.
 */
private fun etiquetaObjetivo(
    objectiveId: Int,
    objetivos: List<LearningObjective>
): String? {
    return objetivos
        .firstOrNull { it.id == objectiveId }
        ?.codigo
        ?.takeIf { it.isNotBlank() }
}
