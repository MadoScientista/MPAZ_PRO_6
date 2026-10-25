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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.CounterCard
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.common.ThemeToggleButton
import com.ccstudio.mpaz_pro_6.ui.components.course.CourseList
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitCard


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
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
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
                ThemeToggleButton(
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
                TextButton(onClick = onCerrarSesion) {
                    Text(text = "Salir")
                }
            }
        )

        when {
            isLoading && cursos.isEmpty() && unidades.isEmpty() &&
                asignaturas.isEmpty() && actividades == 0 && informesPendientes == 0 ->
                LoadingState(
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
                        CourseList(cursos = cursos)
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
                        UnitCard(
                            unit = unit,
                            subjectName = subjectNames[unit.asignaturaId] ?: "Sin asignatura",
                            onViewDetail = { onViewUnit(unit) },
                            onAuthorize = null
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





