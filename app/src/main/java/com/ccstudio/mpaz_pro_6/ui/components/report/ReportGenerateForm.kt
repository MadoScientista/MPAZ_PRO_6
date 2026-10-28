package com.ccstudio.mpaz_pro_6.ui.components.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButtonVariant
import com.ccstudio.mpaz_pro_6.ui.theme.TextFieldShape

/**
 * Formulario de generación de un informe pedagógico.
 *
 * El curso sale de las docencias del docente, el estudiante del endpoint
 * acotado del curso y la unidad de las asignaciones del curso. El componente no
 * conoce repositorios: recibe listas y devuelve las selecciones por callback.
 *
 * El botón queda deshabilitado hasta tener curso, estudiante y unidad, y
 * mientras el ViewModel está guardando.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportGenerateForm(
    courses: List<Course>,
    students: List<User>,
    units: List<LearningUnit>,
    cursoId: Int?,
    estudianteId: Int?,
    unidadId: Int?,
    isSaving: Boolean,
    error: String?,
    onCursoIdChange: (Int?) -> Unit,
    onEstudianteIdChange: (Int?) -> Unit,
    onUnidadIdChange: (Int?) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val courseOptions = courses.map { course ->
        course.id to "${course.nombre} (${course.anio})"
    }
    val studentOptions = students.map { student ->
        student.id to "${student.nombre} (@${student.username})"
    }
    val unitOptions = units.map { unit ->
        unit.id to unit.titulo
    }

    val canSubmit = cursoId != null &&
        estudianteId != null &&
        unidadId != null &&
        !isSaving

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "El informe se calcula con las respuestas registradas en las " +
                "actividades asignadas del estudiante.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        ReportDropdownField(
            label = "Curso",
            selectedLabel = courseOptions
                .firstOrNull { it.first == cursoId }
                ?.second
                .orEmpty(),
            placeholder = "Seleccionar curso",
            supportingText = "Cursos donde tienes docencia",
            enabled = !isSaving,
            options = courseOptions,
            onSelect = onCursoIdChange
        )

        ReportDropdownField(
            label = "Estudiante",
            selectedLabel = studentOptions
                .firstOrNull { it.first == estudianteId }
                ?.second
                .orEmpty(),
            placeholder = if (cursoId == null) {
                "Selecciona un curso primero"
            } else {
                "Seleccionar estudiante"
            },
            supportingText = when {
                cursoId == null -> "Selecciona un curso para cargar estudiantes"
                students.isEmpty() -> "El curso no tiene estudiantes"
                else -> "Estudiantes del curso"
            },
            enabled = !isSaving && cursoId != null && students.isNotEmpty(),
            options = studentOptions,
            onSelect = { onEstudianteIdChange(it) }
        )

        ReportDropdownField(
            label = "Unidad",
            selectedLabel = unitOptions
                .firstOrNull { it.first == unidadId }
                ?.second
                .orEmpty(),
            placeholder = if (cursoId == null) {
                "Selecciona un curso primero"
            } else {
                "Seleccionar unidad"
            },
            supportingText = when {
                cursoId == null -> "Selecciona un curso para cargar unidades"
                units.isEmpty() -> "El curso no tiene unidades asignadas"
                else -> "Solo unidades con asignación en el curso"
            },
            enabled = !isSaving && cursoId != null && units.isNotEmpty(),
            options = unitOptions,
            onSelect = { onUnidadIdChange(it) }
        )

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        AppButton(
            text = if (isSaving) "Generando informe..." else "Generar informe",
            onClick = onSubmit,
            enabled = canSubmit
        )

        AppButton(
            text = "Cancelar",
            onClick = onCancel,
            enabled = !isSaving,
            variant = AppButtonVariant.OUTLINED
        )
    }
}

/** Selector simple de una entidad con opciones `id`-etiqueta. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportDropdownField(
    label: String,
    selectedLabel: String,
    placeholder: String,
    supportingText: String,
    enabled: Boolean,
    options: List<Pair<Int, String>>,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled) {
                expanded = !expanded
            }
        },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedLabel,
            onValueChange = {},
            readOnly = true,
            shape = TextFieldShape,
            label = { Text(text = label) },
            placeholder = { Text(text = placeholder) },
            supportingText = { Text(text = supportingText) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            enabled = enabled,
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = TextFieldShape,
            shadowElevation = 0.dp
        ) {
            if (options.isEmpty()) {
                DropdownMenuItem(
                    text = { Text(text = "No hay opciones disponibles") },
                    onClick = {},
                    enabled = false
                )
            } else {
                options.forEach { (id, optionLabel) ->
                    DropdownMenuItem(
                        text = { Text(text = optionLabel) },
                        onClick = {
                            onSelect(id)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
