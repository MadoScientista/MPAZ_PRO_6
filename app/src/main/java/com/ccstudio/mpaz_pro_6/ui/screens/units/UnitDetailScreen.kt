package com.ccstudio.mpaz_pro_6.ui.screens.units

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.activity.FinalTestCard
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButtonVariant
import com.ccstudio.mpaz_pro_6.ui.components.common.CounterCard
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.content.ContentCard
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitSummaryHeader
import java.time.Instant

/**
 * Detalle de una unidad.
 *
 * Muestra el resumen de la unidad, sus contenidos y sus actividades. La UTP ve el
 * material en modo lectura; el docente ve el material y dispone del botón para
 * crear nuevo contenido; el estudiante ve los contenidos publicados y las
 * actividades asignadas a su curso, y puede abrir una actividad para resolverla.
 *
 * El botón para crear nuevo contenido está disponible exclusivamente para el rol
 * docente (`UserRole.DOCENTE`).
 *
 * La autorización solo se ofrece a la UTP y solo en los estados que el backend
 * admite: `EN_REVISION` para autorizar y `AUTORIZADO` para publicar.
 *
 * La prueba final se muestra en una tarjeta propia con su fecha de aplicación y
 * máximo de intentos. El docente puede abrir su configuración desde esa tarjeta;
 * el estudiante ve un aviso cuando la prueba final está deshabilitada.
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
    course: Course? = null,
    rol: UserRole? = null,
    puedeAutorizar: Boolean = false,
    puedeResolverActividades: Boolean = false,
    puedeCrearContenido: Boolean = rol == UserRole.DOCENTE,
    onCreateContent: (() -> Unit)? = null,
    maxIntentosPruebaFinal: Int? = null,
    fechaPruebaFinal: Instant? = null,
    pruebaFinalHabilitada: Boolean? = null,
    pruebaFinalDeshabilitada: Boolean = false,
    intentosPruebaFinalCount: Int? = null,
    onEditFinalTest: (() -> Unit)? = null
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
                        subjectName = asignatura?.nombre ?: "Sin asignatura",
                        courseName = course?.nombre
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

                    val puedeCrear = (puedeCrearContenido || rol == UserRole.DOCENTE) && onCreateContent != null
                    if (puedeCrear) {
                        AppButton(
                            text = "Crear nuevo contenido",
                            onClick = onCreateContent,
                            variant = AppButtonVariant.OUTLINED,
                            modifier = Modifier.padding(horizontal = 16.dp)
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
                        if (rol == UserRole.ESTUDIANTE && pruebaFinalDeshabilitada) {
                            EmptyState(
                                message = "La prueba final todavía no está habilitada.",
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        } else {
                            EmptyState(
                                message = "Sin prueba final",
                                description = "Esta unidad todavía no tiene una prueba final.",
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    } else {
                        FinalTestCard(
                            activity = pruebaFinal,
                            questionsCount = preguntasPorActividad[pruebaFinal.id] ?: 0,
                            applicationDate = fechaPruebaFinal,
                            maxAttempts = maxIntentosPruebaFinal,
                            attemptsCount = intentosPruebaFinalCount,
                            enabled = pruebaFinalHabilitada,
                            onOpen = { onOpenActivity(pruebaFinal) },
                            openLabel = if (rol == UserRole.ESTUDIANTE) {
                                "Resolver"
                            } else {
                                "Vista previa"
                            },
                            onEdit = if (rol == UserRole.DOCENTE) onEditFinalTest else null,
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
