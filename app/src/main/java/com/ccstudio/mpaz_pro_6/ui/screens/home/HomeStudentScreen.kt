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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.activity.ActivityTypeChip
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.CounterCard
import com.ccstudio.mpaz_pro_6.ui.components.common.EmptyState
import com.ccstudio.mpaz_pro_6.ui.components.common.ErrorState
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.common.SectionHeader
import com.ccstudio.mpaz_pro_6.ui.components.common.StatusChip
import com.ccstudio.mpaz_pro_6.ui.components.common.ThemeToggleButton
import com.ccstudio.mpaz_pro_6.ui.components.unit.UnitCard

/**
 * Panel de inicio del estudiante.
 *
 * Muestra un resumen de sus unidades publicadas y asignadas, las actividades que
 * le quedan pendientes y la cantidad de informes. Al tocar una actividad pendiente
 * se abre su intento; el listado completo vive en la pestaña Actividades y los
 * informes en la pestaña Informes.
 */
@Composable
fun HomeStudentScreen(
    userName: String,
    unidades: List<LearningUnit>,
    asignaturas: List<Subject>,
    actividadesPendientes: List<Activity>,
    totalInformes: Int,
    sinCurso: Boolean,
    onViewUnit: (LearningUnit) -> Unit,
    onVerUnidades: () -> Unit,
    onResolveActivity: (Activity) -> Unit,
    onCerrarSesion: () -> Unit,
    onCargar: () -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    error: ApiError? = null
) {
    val subjectNames = asignaturas.associate { it.id to it.nombre }
    val unitTitles = unidades.associate { it.id to it.titulo }

    Column(modifier = modifier.fillMaxSize()) {
        AppTopBar(
            title = "Mi panel",
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

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("home-student-list"),
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
                            titulo = "Pendientes",
                            valor = actividadesPendientes.size,
                            modifier = Modifier.weight(1f)
                        )

                        CounterCard(
                            titulo = "Informes",
                            valor = totalInformes,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    SectionHeader(
                        title = "Mis unidades",
                        count = unidades.size
                    )
                }

                if (unidades.isEmpty()) {
                    item {
                        EmptyState(
                            message = "No tienes unidades publicadas asignadas.",
                            description = "Cuando la UTP publique y asigne una unidad aparecerá aquí."
                        )
                    }
                } else {
                    items(
                        items = unidades,
                        key = { "unit-${it.id}" }
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
                        text = "Ver todas mis unidades",
                        onClick = onVerUnidades
                    )
                }

                item {
                    SectionHeader(
                        title = "Actividades pendientes",
                        count = actividadesPendientes.size
                    )
                }

                if (actividadesPendientes.isEmpty()) {
                    item {
                        EmptyState(
                            message = "No tienes actividades pendientes.",
                            description = "Cuando tengas una actividad asignada aparecerá aquí."
                        )
                    }
                } else {
                    items(
                        items = actividadesPendientes.take(3),
                        key = { "activity-${it.id}" }
                    ) { activity ->
                        PendingActivityPreview(
                            activity = activity,
                            unitTitle = unitTitles[activity.unidadId] ?: "Sin unidad",
                            onClick = { onResolveActivity(activity) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Vista previa de una actividad pendiente en el panel del estudiante.
 *
 * Es de solo lectura: la resolución de la actividad llega con su pantalla.
 */
@Composable
private fun PendingActivityPreview(
    activity: Activity,
    unitTitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActivityTypeChip(activityType = activity.tipo)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = unitTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            StatusChip(label = "Por resolver")
        }
    }
}
