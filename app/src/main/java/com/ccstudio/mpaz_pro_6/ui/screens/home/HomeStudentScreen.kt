package com.ccstudio.mpaz_pro_6.ui.screens.home

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.activity.StudentActivityCarouselCard
import com.ccstudio.mpaz_pro_6.ui.components.activity.StudentActivityStatus
import com.ccstudio.mpaz_pro_6.ui.components.common.CardCarousel
import com.ccstudio.mpaz_pro_6.ui.components.common.CounterCard
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.subject.SubjectCarouselCard

/**
 * Panel de inicio del estudiante.
 *
 * Muestra el curso del estudiante, un resumen de sus asignaturas visibles y las
 * actividades que le quedan pendientes. El curso es solo contexto: no abre el
 * detalle administrativo ni la lista de compañeros. Las asignaturas y las
 * actividades usan carruseles; cada uno tiene un enlace «Ver todo» a su listado
 * correspondiente. No incluye informes pedagógicos.
 *
 * Las actividades en progreso se marcan con su estado y abren el intento para
 * continuarlo; las pendientes abren un intento nuevo. Una prueba final completada
 * sigue en el carrusel mientras queden intentos y se ofrece como “Repetir prueba”.
 */
@Composable
fun HomeStudentScreen(
    curso: Course?,
    unidades: List<LearningUnit>,
    asignaturas: List<Subject>,
    unidadesPorAsignatura: Map<Int, Int>,
    actividadesPendientes: List<Activity>,
    actividadesEnProgreso: Set<Int>,
    actividadesParaRepetir: Set<Int>,
    sinCurso: Boolean,
    onViewSubject: (Subject) -> Unit,
    onVerAsignaturas: () -> Unit,
    onVerActividades: () -> Unit,
    onResolveActivity: (Activity) -> Unit,
    onCargar: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    val unitTitles = unidades.associate { it.id to it.titulo }

    Column(modifier = modifier.fillMaxSize()) {
        when {
            isLoading && unidades.isEmpty() && actividadesPendientes.isEmpty() ->
                LoadingState(
                    message = "Cargando tu panel...",
                    modifier = Modifier.padding(16.dp)
                )

            error != null -> ErrorState(
                error = error,
                onRetry = onCargar,
                modifier = Modifier.padding(16.dp)
            )

            sinCurso -> EmptyState(
                message = "No tienes curso asignado.",
                description = "La UTP debe asignarte un curso para ver tus unidades y actividades.",
                modifier = Modifier.padding(16.dp)
            )

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp)
                    .testTag("home-student-list"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                curso?.let { course ->
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = "Curso ${course.nombre}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "${course.anio}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

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
                        titulo = "Asignaturas",
                        valor = asignaturas.size,
                        modifier = Modifier.weight(1f)
                    )

                    CounterCard(
                        titulo = "Pendientes",
                        valor = actividadesPendientes.size,
                        modifier = Modifier.weight(1f)
                    )
                }

                CardCarousel(
                    title = "Mis asignaturas",
                    itemCount = asignaturas.size,
                    onSeeAll = onVerAsignaturas,
                    emptyMessage = "No tienes asignaturas con unidades publicadas asignadas."
                ) { index ->
                    val subject = asignaturas[index]

                    SubjectCarouselCard(
                        subject = subject,
                        unitCount = unidadesPorAsignatura[subject.id] ?: 0,
                        onClick = { onViewSubject(subject) }
                    )
                }

                CardCarousel(
                    title = "Actividades pendientes",
                    itemCount = actividadesPendientes.size,
                    onSeeAll = onVerActividades,
                    emptyMessage = "No tienes actividades pendientes."
                ) { index ->
                    val activity = actividadesPendientes[index]
                    val status = when {
                        activity.id in actividadesEnProgreso ->
                            StudentActivityStatus.IN_PROGRESS

                        activity.id in actividadesParaRepetir ->
                            StudentActivityStatus.COMPLETED

                        else -> StudentActivityStatus.PENDING
                    }

                    StudentActivityCarouselCard(
                        activity = activity,
                        unitTitle = unitTitles[activity.unidadId] ?: "Sin unidad",
                        status = status,
                        canRetry = activity.id in actividadesParaRepetir,
                        onClick = { onResolveActivity(activity) }
                    )
                }
            }
        }
    }
}
