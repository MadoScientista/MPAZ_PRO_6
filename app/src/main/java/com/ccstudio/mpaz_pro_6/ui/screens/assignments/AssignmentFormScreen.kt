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
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.ui.components.assignment.AssignmentForm
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import java.time.Instant

/**
 * Formulario de edición de una asignación.
 *
 * Solo edita las reglas que admite el `PUT`: fecha, máximo de intentos y
 * prueba final habilitada. Curso, unidad y docente se muestran bloqueados.
 * El ViewModel envía únicamente los campos modificados.
 */
@Composable
fun AssignmentFormScreen(
    courses: List<Course>,
    units: List<LearningUnit>,
    teachers: List<User>,
    cursoId: Int?,
    unidadId: Int?,
    docenteId: Int?,
    fecha: Instant?,
    maxIntentos: String,
    pruebaFinalHabilitada: Boolean,
    isEditMode: Boolean,
    isLoading: Boolean,
    isSaving: Boolean,
    error: String?,
    onCursoIdChange: (Int?) -> Unit,
    onUnidadIdChange: (Int?) -> Unit,
    onDocenteIdChange: (Int?) -> Unit,
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
                    teachers = teachers,
                    cursoId = cursoId,
                    unidadId = unidadId,
                    docenteId = docenteId,
                    fecha = fecha,
                    maxIntentos = maxIntentos,
                    pruebaFinalHabilitada = pruebaFinalHabilitada,
                    onCursoIdChange = onCursoIdChange,
                    onUnidadIdChange = onUnidadIdChange,
                    onDocenteIdChange = onDocenteIdChange,
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
