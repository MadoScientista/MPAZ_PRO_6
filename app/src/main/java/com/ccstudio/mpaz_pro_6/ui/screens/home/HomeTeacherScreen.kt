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
import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.ui.components.common.CardCarousel
import com.ccstudio.mpaz_pro_6.ui.components.common.CounterCard
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.course.CourseCarouselCard
import com.ccstudio.mpaz_pro_6.ui.components.report.ReportCarouselCard
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitCarouselCard


/**
 * Panel de inicio del docente.
 *
 * Muestra un resumen de sus cursos, las unidades que tiene asignadas y los
 * informes que esperan su validación. El carrusel de cursos replica el del panel
 * de UTP y enlaza con la pestaña Cursos. La cantidad de actividades y los
 * informes pendientes se resuelven en el ViewModel.
 * Los cursos que maneja el docente los resuelve el ViewModel con
 * `GET /api/usuarios/{id}/cursos`.
 *
 * La pantalla no habla con repositorios: recibe los datos ya resueltos y delega
 * las acciones por callback.
 */
@Composable
fun HomeTeacherScreen(
    cursos: List<Course>,
    unidades: List<LearningUnit>,
    asignaturas: List<Subject>,
    informesPendientes: List<PedagogicalReport>,
    estudiantes: List<User>,
    actividades: Int,
    onViewUnit: (LearningUnit) -> Unit,
    onViewCourse: (Course) -> Unit,
    onViewReport: (PedagogicalReport) -> Unit,
    onVerUnidades: () -> Unit,
    onVerCursos: () -> Unit,
    onVerInformes: () -> Unit,
    onCargar: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    val subjectNames = asignaturas.associate { it.id to it.nombre }
    val unitTitles = unidades.associate { it.id to it.titulo }
    val studentNames = estudiantes.associate { it.id to it.nombre }
    val estudiantesPorCurso = estudiantes
        .mapNotNull { it.cursoId }
        .groupingBy { it }
        .eachCount()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            isLoading && cursos.isEmpty() && unidades.isEmpty() &&
                asignaturas.isEmpty() && actividades == 0 &&
                informesPendientes.isEmpty() ->
                LoadingState(
                    message = "Cargando tu panel...",
                    modifier = Modifier.padding(16.dp)
                )

            error != null -> ErrorState(
                error = error,
                onRetry = onCargar,
                modifier = Modifier.padding(16.dp)
            )

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
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
                        titulo = "Unidades",
                        valor = unidades.size,
                        modifier = Modifier.weight(1f)
                    )

                    CounterCard(
                        titulo = "Actividades",
                        valor = actividades,
                        modifier = Modifier.weight(1f)
                    )

                    CounterCard(
                        titulo = "Informes",
                        valor = informesPendientes.size,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "Las actividades corresponden a tus unidades asignadas.",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

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
                    title = "Unidades asignadas",
                    itemCount = unidades.size,
                    onSeeAll = onVerUnidades,
                    emptyMessage = "No tienes unidades asignadas."
                ) { index ->
                    val unit = unidades[index]

                    UnitCarouselCard(
                        unit = unit,
                        subjectName = subjectNames[unit.asignaturaId] ?: "Sin asignatura",
                        onClick = { onViewUnit(unit) }
                    )
                }

                CardCarousel(
                    title = "Informes de rendimiento",
                    itemCount = informesPendientes.size,
                    onSeeAll = onVerInformes,
                    emptyMessage = "No tienes informes por validar."
                ) { index ->
                    val report = informesPendientes[index]

                    ReportCarouselCard(
                        report = report,
                        studentName = studentNames[report.estudianteId]
                            ?: "Sin estudiante",
                        unitName = unitTitles[report.unidadId] ?: "Sin unidad",
                        onClick = { onViewReport(report) }
                    )
                }
            }
        }
    }
}





