package com.ccstudio.mpaz_pro_6.ui.screens.home

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
import androidx.compose.ui.platform.testTag
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
 * Muestra un resumen de sus unidades publicadas y asignadas, las actividades que
 * le quedan pendientes y la cantidad de informes. Las unidades y las actividades
 * usan el mismo carrusel que los paneles de UTP y docente; cada uno tiene un
 * enlace «Ver todo» a su listado correspondiente.
 *
 * Las actividades en progreso se marcan con su estado y abren el intento para
 * continuarlo; las pendientes abren un intento nuevo.
 */
@Composable
fun HomeStudentScreen(
    unidades: List<LearningUnit>,
    asignaturas: List<Subject>,
    actividadesPendientes: List<Activity>,
    actividadesEnProgreso: Set<Int>,
    totalInformes: Int,
    sinCurso: Boolean,
    courseId: Int? = null,
    onViewSubject: (Subject) -> Unit = {},
    onVerAsignaturas: () -> Unit = {},
    onVerActividades: () -> Unit,
    onResolveActivity: (Activity) -> Unit,
    onCargar: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {

    val unitTitles = unidades.associate { it.id to it.titulo }

    val unidadesPorAsignatura = unidades
        .groupingBy { it.asignaturaId }
        .eachCount()

    Column(modifier = modifier.fillMaxSize()) {
        when {
            isLoading && unidades.isEmpty() && actividadesPendientes.isEmpty() &&
                totalInformes == 0 ->
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
                    title = "Mis Asignaturas",
                    itemCount = asignaturas.size,
                    onSeeAll = onVerAsignaturas,
                    emptyMessage = "Sin asignaturas"
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
                    val status = if (activity.id in actividadesEnProgreso) {
                        StudentActivityStatus.IN_PROGRESS
                    } else {
                        StudentActivityStatus.PENDING
                    }

                    StudentActivityCarouselCard(
                        activity = activity,
                        unitTitle = unitTitles[activity.unidadId] ?: "Sin unidad",
                        status = status,
                        onClick = { onResolveActivity(activity) }
                    )
                }
            }
        }
    }
}
