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
import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.CardCarousel
import com.ccstudio.mpaz_pro_6.ui.components.common.CounterCard
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.report.ReportCarouselCard
import com.ccstudio.mpaz_pro_6.ui.components.subject.SubjectCarouselCard

/**
 * Panel de inicio del docente.
 *
 * El flujo principal es por asignatura: muestra un resumen de sus asignaturas,
 * sus cursos y el carrusel de asignaturas que dicta. Las asignaturas y los
 * cursos se derivan de las docencias; no se consulta el listado completo de
 * usuarios. Los informes pendientes usan los nombres que entrega el backend.
 *
 * Los cursos no se repiten en un carrusel: conservan su pestaña propia en la
 * barra inferior. El Inicio prioriza las asignaturas, la creación de
 * asignaciones y los informes por validar.
 */
@Composable
fun HomeTeacherScreen(
    cursos: List<Course>,
    asignaturas: List<Subject>,
    cursosPorAsignatura: Map<Int, List<String>>,
    unidadesPorAsignatura: Map<Int, Int>,
    informesPendientes: List<PedagogicalReport>,
    actividades: Int,
    onViewSubject: (Subject) -> Unit,
    onViewReport: (PedagogicalReport) -> Unit,
    onCreateAssignment: () -> Unit,
    onVerAsignaturas: () -> Unit,
    onVerInformes: () -> Unit,
    onCargar: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        when {
            isLoading && cursos.isEmpty() && asignaturas.isEmpty() ->
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
                        titulo = "Asignaturas",
                        valor = asignaturas.size,
                        modifier = Modifier.weight(1f)
                    )

                    CounterCard(
                        titulo = "Cursos",
                        valor = cursos.size,
                        modifier = Modifier.weight(1f)
                    )

                    CounterCard(
                        titulo = "Actividades",
                        valor = actividades,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    text = "Las actividades corresponden a tus unidades asignadas.",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                AppButton(
                    text = "Nueva asignación",
                    onClick = onCreateAssignment,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                CardCarousel(
                    title = "Asignaturas",
                    itemCount = asignaturas.size,
                    onSeeAll = onVerAsignaturas,
                    emptyMessage = "No tienes asignaturas asignadas."
                ) { index ->
                    val subject = asignaturas[index]

                    SubjectCarouselCard(
                        subject = subject,
                        unitCount = unidadesPorAsignatura[subject.id] ?: 0,
                        coursesLabel = cursosPorAsignatura[subject.id]
                            ?.joinToString(" · ")
                            .orEmpty(),
                        onClick = { onViewSubject(subject) }
                    )
                }

                CardCarousel(
                    title = "Informes pendientes",
                    itemCount = informesPendientes.size,
                    onSeeAll = onVerInformes,
                    emptyMessage = "No tienes informes por validar."
                ) { index ->
                    val report = informesPendientes[index]

                    ReportCarouselCard(
                        report = report,
                        studentName = report.estudianteNombre
                            ?: "Estudiante #${report.estudianteId}",
                        unitName = report.unidadTitulo
                            ?: "Unidad #${report.unidadId}",
                        onClick = { onViewReport(report) }
                    )
                }
            }
        }
    }
}
