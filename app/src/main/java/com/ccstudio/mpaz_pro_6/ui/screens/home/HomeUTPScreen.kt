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
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.CardCarousel
import com.ccstudio.mpaz_pro_6.ui.components.common.CounterCard
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.subject.SubjectCarouselCard
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitCarouselCard

/**
 * Panel de inicio de la jefa de UTP.
 *
 * Muestra el resumen de la gestión (unidades en revisión, cursos y asignaturas) y
 * dos carruseles: las unidades `EN_REVISION` y las asignaturas. La pestaña
 * **Revisión** concentra el listado completo de unidades en revisión; el Inicio
 * muestra solo la cola de trabajo inmediata.
 *
 * Las unidades en revisión se obtienen filtrando por `EN_REVISION` sobre
 * `GET /api/unidades`, porque el backend no expone un listado dedicado. Los
 * `BORRADOR` pertenecen al docente. Los cursos y las asignaturas tienen sus
 * propias pestañas, así que el panel no repite el catálogo completo. Desde el
 * panel se accede a la gestión de docencias sin agregar una sexta pestaña a la
 * barra inferior.
 */
@Composable
fun HomeUTPScreen(
    unidadesPendientes: List<LearningUnit>,
    cursos: List<Course>,
    asignaturas: List<Subject>,
    unidadesPorAsignatura: Map<Int, Int>,
    onViewUnit: (LearningUnit) -> Unit,
    onViewSubject: (Subject) -> Unit,
    onVerUnidades: () -> Unit,
    onVerAsignaturas: () -> Unit,
    onGestionarDocencias: () -> Unit,
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
                        titulo = "En revisión",
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

                AppButton(
                    text = "Gestionar docencias",
                    onClick = onGestionarDocencias,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                CardCarousel(
                    title = "Unidades en revisión",
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
