package com.ccstudio.mpaz_pro_6.ui.screens.assignments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.ui.components.assignment.AssignmentForm
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import java.time.Instant

/**
 * Formulario de creación o edición de una asignación.
 *
 * Al crear se eligen curso y unidad entre las docencias del docente. Al editar,
 * solo se modifican las reglas que admite el `PUT`: fecha, máximo de intentos y
 * prueba final habilitada; curso y unidad se muestran bloqueados.
 */
@Composable
fun AssignmentFormScreen(
    courses: List<Course>,
    units: List<LearningUnit>,
    cursoId: Int?,
    unidadId: Int?,
    fecha: Instant?,
    maxIntentos: String,
    pruebaFinalHabilitada: Boolean,
    isEditMode: Boolean,
    isLoading: Boolean,
    isSaving: Boolean,
    error: String?,
    onCursoIdChange: (Int?) -> Unit,
    onUnidadIdChange: (Int?) -> Unit,
    onFechaChange: (Instant?) -> Unit,
    onMaxIntentosChange: (String) -> Unit,
    onPruebaFinalHabilitadaChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (isLoading) {
            LoadingState(message = "Cargando la asignación...")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                AssignmentForm(
                    courses = courses,
                    units = units,
                    cursoId = cursoId,
                    unidadId = unidadId,
                    fecha = fecha,
                    maxIntentos = maxIntentos,
                    pruebaFinalHabilitada = pruebaFinalHabilitada,
                    onCursoIdChange = onCursoIdChange,
                    onUnidadIdChange = onUnidadIdChange,
                    onFechaChange = onFechaChange,
                    onMaxIntentosChange = onMaxIntentosChange,
                    onPruebaFinalHabilitadaChange = onPruebaFinalHabilitadaChange,
                    onSubmit = onSubmit,
                    onCancel = onCancel,
                    isEditMode = isEditMode,
                    isSaving = isSaving,
                    error = error
                )
            }
        }
    }
}
