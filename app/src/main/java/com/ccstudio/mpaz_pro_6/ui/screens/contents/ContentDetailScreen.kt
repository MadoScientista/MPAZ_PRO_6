package com.ccstudio.mpaz_pro_6.ui.screens.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.ContentResource
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.ui.components.activity.ActivityCard
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButtonVariant
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.content.ContentResourceCard

/**
 * Detalle de un contenido.
 *
 * Pantalla de consulta para la revisión de la UTP: muestra el título (sin origen
 * ni orden), la explicación completa, los ejemplos, los recursos, las
 * instrucciones y las actividades asociadas. No ofrece edición ni borrado.
 *
 * El contenido, su objetivo y sus actividades llegan resueltos por el ViewModel.
 * Los recursos se abren por callback, para que la pantalla no dependa del
 * `Context` ni de un `Intent`. Las actividades también se abren por callback: el
 * estudiante entra a resolverlas y la UTP/docente las ven en modo lectura.
 */
@Composable
fun ContentDetailScreen(
    contenido: Content?,
    objetivo: LearningObjective?,
    recursos: List<ContentResource>,
    actividades: List<Activity>,
    preguntasPorActividad: Map<Int, Int>,
    isLoading: Boolean,
    error: ApiError?,
    onOpenResource: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    onOpenActivity: ((Activity) -> Unit)? = null,
    onCreateActivity: (() -> Unit)? = null,
    activityActionLabel: String = "Resolver"
) {
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
                isLoading && contenido == null -> LoadingState(message = "Cargando el contenido...")

                error != null -> ErrorState(error = error, onRetry = onRetry)

                contenido != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = contenido.titulo,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        val etiquetaObjetivo = etiquetaObjetivo(objetivo)

                        if (etiquetaObjetivo != null) {
                            Text(
                                text = "OA: $etiquetaObjetivo",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    SectionHeader(
                        title = "Explicación",
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Text(
                        text = contenido.explicacion,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    if (!contenido.ejemplos.isNullOrBlank()) {
                        SectionHeader(
                            title = "Ejemplos",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Text(
                            text = contenido.ejemplos,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    SectionHeader(
                        title = "Recursos",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        count = recursos.size
                    )

                    if (recursos.isEmpty()) {
                        EmptyState(
                            message = "Este contenido no tiene recursos asociados.",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        recursos.forEach { resource ->
                            ContentResourceCard(
                                resource = resource,
                                onOpen = { onOpenResource(resource.url) },
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    SectionHeader(
                        title = "Actividades",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        count = actividades.size
                    )

                    if (onCreateActivity != null) {
                        AppButton(
                            text = "Crear nueva actividad",
                            onClick = onCreateActivity,
                            variant = AppButtonVariant.OUTLINED,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    if (actividades.isEmpty()) {
                        EmptyState(
                            message = "Este contenido no tiene actividades asociadas.",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        actividades.forEach { activity ->
                            ActivityCard(
                                activity = activity,
                                questionsCount = preguntasPorActividad[activity.id] ?: 0,
                                contentTitle = contenido.titulo,
                                onViewDetail = onOpenActivity?.let { open ->
                                    { open(activity) }
                                },
                                detailLabel = activityActionLabel,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    if (!contenido.instrucciones.isNullOrBlank()) {
                        SectionHeader(
                            title = "Instrucciones",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Text(
                            text = contenido.instrucciones,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Etiqueta del objetivo de aprendizaje del contenido.
 *
 * Usa el código del objetivo (`OA01`) y, si no tiene, el número de su id.
 */
private fun etiquetaObjetivo(objetivo: LearningObjective?): String? {
    if (objetivo == null) return null

    val codigo = objetivo.codigo?.takeIf { it.isNotBlank() }

    return codigo ?: "OA ${objetivo.id}"
}
