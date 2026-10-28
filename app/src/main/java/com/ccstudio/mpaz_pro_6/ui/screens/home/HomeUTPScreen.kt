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
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.common.CardCarousel
import com.ccstudio.mpaz_pro_6.ui.components.common.CounterCard
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.course.CourseCarouselCard
import com.ccstudio.mpaz_pro_6.ui.components.subject.SubjectCarouselCard
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitCarouselCard

/**
 * Panel de inicio de la jefa de UTP.
 *
 * Muestra el resumen de la gestión (unidades pendientes, cursos y asignaturas) y
 * dos carruseles: las unidades `EN_REVISION` y las asignaturas. Cada carrusel
 * tiene un enlace «Ver todo» a su listado correspondiente.
 *
 * Las unidades pendientes se obtienen filtrando por `EN_REVISION` sobre
 * `GET /api/unidades`, porque el backend no expone un listado dedicado. Los
 * `BORRADOR` son material de trabajo del docente y no se muestran a la UTP.
 */
@Composable
fun HomeUTPScreen(
    unidadesPendientes: List<LearningUnit>,
    cursos: List<Course>,
    asignaturas: List<Subject>,
    unidadesPorAsignatura: Map<Int, Int>,
    estudiantesPorCurso: Map<Int, Int>,
    onViewUnit: (LearningUnit) -> Unit,
    onViewSubject: (Subject) -> Unit,
    onViewCourse: (Course) -> Unit,
    onVerUnidades: () -> Unit,
    onVerCursos: () -> Unit,
    onVerAsignaturas: () -> Unit,
    onCargar: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when {
            isLoading &&
                unidadesPendientes.isEmpty() &&
                cursos.isEmpty() &&
                asignaturas.isEmpty() -> LoadingState(
                message = "Cargando el resumen...",
                modifier = Modifier.padding(16.dp)
            )

            error != null -> ErrorState(
                error = error,
                onRetry = onCargar,
                modifier = Modifier.padding(16.dp)
            )

            else -> {
                val subjectNames = asignaturas.associate { it.id to it.nombre }

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
                        titulo = "Pendientes",
                        valor = unidadesPendientes.size,
                        modifier = Modifier.weight(1f)
                    )

                    CounterCard(
                        titulo = "Cursos",
                        valor = cursos.size,
                        modifier = Modifier.weight(1f)
                    )

                    CounterCard(
                        titulo = "Asignaturas",
                        valor = asignaturas.size,
                        modifier = Modifier.weight(1f)
                    )
                }

                CardCarousel(
                    title = "Unidades pendientes",
                    itemCount = unidadesPendientes.size,
                    onSeeAll = onVerUnidades,
                    emptyMessage = "Sin unidades pendientes"
                ) { index ->
                    val unit = unidadesPendientes[index]

                    UnitCarouselCard(
                        unit = unit,
                        subjectName = subjectNames[unit.asignaturaId] ?: "Sin asignatura",
                        onClick = { onViewUnit(unit) }
                    )
                }

                CardCarousel(
                    title = "Cursos",
                    itemCount = cursos.size,
                    onSeeAll = onVerCursos,
                    emptyMessage = "Sin cursos"
                ) { index ->
                    val course = cursos[index]

                    CourseCarouselCard(
                        course = course,
                        studentCount = estudiantesPorCurso[course.id] ?: 0,
                        onClick = { onViewCourse(course) }
                    )
                }

                CardCarousel(
                    title = "Asignaturas registradas",
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
            }
        }
    }
}
