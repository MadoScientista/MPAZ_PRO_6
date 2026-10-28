package com.ccstudio.mpaz_pro_6.ui.screens.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.ui.components.common.LoadingState
import com.ccstudio.mpaz_pro_6.ui.components.report.ReportGenerateForm

/**
 * Pantalla de generación de un informe pedagógico.
 *
 * Recibe el estado del [ReportGenerateViewModel] y delega el formulario en
 * [ReportGenerateForm]. El informe queda calculado por el backend y se abre en
 * su detalle después de crearlo.
 */
@Composable
fun ReportGenerateScreen(
    courses: List<Course>,
    students: List<User>,
    units: List<LearningUnit>,
    cursoId: Int?,
    estudianteId: Int?,
    unidadId: Int?,
    isLoading: Boolean,
    isSaving: Boolean,
    error: String?,
    onCursoIdChange: (Int?) -> Unit,
    onEstudianteIdChange: (Int?) -> Unit,
    onUnidadIdChange: (Int?) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        if (isLoading && courses.isEmpty()) {
            LoadingState(message = "Cargando cursos y estudiantes...")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Nuevo informe pedagógico",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                ReportGenerateForm(
                    courses = courses,
                    students = students,
                    units = units,
                    cursoId = cursoId,
                    estudianteId = estudianteId,
                    unidadId = unidadId,
                    isSaving = isSaving,
                    error = error,
                    onCursoIdChange = onCursoIdChange,
                    onEstudianteIdChange = onEstudianteIdChange,
                    onUnidadIdChange = onUnidadIdChange,
                    onSubmit = onSubmit,
                    onCancel = onCancel
                )
            }
        }
    }
}
