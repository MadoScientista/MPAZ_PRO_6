package com.ccstudio.mpaz_pro_6.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitStatusChip

/**
 * Panel de inicio del docente.
 *
 * Muestra las unidades en las que el docente tiene asignaciones, la cantidad de
 * actividades de esas unidades y los informes pedagógicos pendientes de validación.
 * Los cursos que maneja el docente los resuelve el ViewModel con
 * `GET /api/usuarios/{id}/cursos`.
 *
 * La pantalla no habla con repositorios: recibe los datos ya resueltos y delega
 * las acciones por callback.
 */
@Composable
fun HomeTeacherScreen(
    userName: String,
    cursos: List<Course>,
    unidades: List<LearningUnit>,
    asignaturas: List<Subject>,
    actividades: Int,
    informesPendientes: Int,
    onViewUnit: (LearningUnit) -> Unit,
    onVerUnidades: () -> Unit,
    onCerrarSesion: () -> Unit,
    onCargar: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    val subjectNames = asignaturas.associate { it.id to it.nombre }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        AppTopBar(
            title = "Panel del docente",
            actions = {
                TextButton(onClick = onCerrarSesion) {
                    Text(text = "Salir")
                }
            }
        )

        when {
            isLoading -> LoadingState(
                message = "Cargando tu panel...",
                modifier = Modifier.padding(16.dp)
            )

            error != null -> ErrorState(
                error = error,
                onRetry = onCargar,
                modifier = Modifier.padding(16.dp)
            )

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = if (userName.isBlank()) "Hola" else "Hola, $userName",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    SectionHeader(title = "Resumen")
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TeacherCounterCard(
                            titulo = "Unidades",
                            valor = unidades.size,
                            modifier = Modifier.weight(1f)
                        )

                        TeacherCounterCard(
                            titulo = "Actividades",
                            valor = actividades,
                            modifier = Modifier.weight(1f)
                        )

                        TeacherCounterCard(
                            titulo = "Informes pendientes",
                            valor = informesPendientes,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Text(
                        text = "Las actividades corresponden a tus unidades asignadas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    SectionHeader(title = "Mis cursos", count = cursos.size)
                }

                item {
                    if (cursos.isEmpty()) {
                        EmptyState(
                            message = "No tienes cursos asignados.",
                            description = "Cuando la UTP te asigne un curso aparecerá aquí."
                        )
                    } else {
                        TeacherCourseList(cursos = cursos)
                    }
                }

                item {
                    SectionHeader(title = "Unidades asignadas", count = unidades.size)
                }

                if (unidades.isEmpty()) {
                    item {
                        EmptyState(
                            message = "No tienes unidades asignadas.",
                            description = "Cuando la UTP te asigne una unidad aparecerá aquí."
                        )
                    }
                } else {
                    items(
                        items = unidades,
                        key = { it.id }
                    ) { unit ->
                        TeacherUnitCard(
                            unit = unit,
                            subjectName = subjectNames[unit.asignaturaId] ?: "Sin asignatura",
                            onViewDetail = { onViewUnit(unit) }
                        )
                    }
                }

                item {
                    AppButton(
                        text = "Ver todas las unidades",
                        onClick = onVerUnidades
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta con un contador del resumen del docente.
 */
@Composable
private fun TeacherCounterCard(
    titulo: String,
    valor: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = valor.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Lista los cursos que maneja el docente.
 */
@Composable
private fun TeacherCourseList(
    cursos: List<Course>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            cursos.forEach { course ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = course.nombre,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = course.anio.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Tarjeta de una unidad asignada al docente.
 *
 * Muestra la asignatura, el estado, el título y el nivel educativo, y permite
 * abrir el detalle de la unidad.
 */
@Composable
private fun TeacherUnitCard(
    unit: LearningUnit,
    subjectName: String,
    onViewDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subjectName,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )

                UnitStatusChip(status = unit.estado)
            }

            Text(
                text = unit.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = unit.nivelEducativo,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = onViewDetail,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver detalle")
            }
        }
    }
}
