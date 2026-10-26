package com.ccstudio.mpaz_pro_6.ui.components.assignment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButtonVariant
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTextField
import com.ccstudio.mpaz_pro_6.ui.theme.TextFieldShape
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Formulario de asignación.
 *
 * Reúne el curso, la unidad, el docente, la fecha, el máximo de intentos y el
 * interruptor de prueba final habilitada. El estado de los campos llega por
 * parámetro y cada uno expone su callback; la pantalla arma el
 * `CreateAssignmentRequest` o el `UpdateAssignmentRequest`.
 *
 * El curso, la unidad y el docente solo se eligen al crear: el `PUT` de asignaciones
 * no los admite y por eso quedan deshabilitados en edición. El selector de docente
 * solo ofrece usuarios con rol `DOCENTE`, como exige el contrato.
 *
 * La fecha se elige con el selector de Material 3. Como el contrato maneja un
 * instante ISO-8601, el componente conserva la hora del valor actual y solo cambia
 * la parte de la fecha.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignmentForm(
    courses: List<Course>,
    units: List<LearningUnit>,
    teachers: List<User>,
    cursoId: Int?,
    unidadId: Int?,
    docenteId: Int?,
    fecha: Instant?,
    maxIntentos: String,
    pruebaFinalHabilitada: Boolean,
    onCursoIdChange: (Int?) -> Unit,
    onUnidadIdChange: (Int?) -> Unit,
    onDocenteIdChange: (Int?) -> Unit,
    onFechaChange: (Instant?) -> Unit,
    onMaxIntentosChange: (String) -> Unit,
    onPruebaFinalHabilitadaChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isReadOnly: Boolean = false,
    isSaving: Boolean = false,
    error: String? = null
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    val maxIntentosValue = maxIntentos.trim()
    val isMaxIntentosValid = maxIntentosValue.isEmpty() ||
        (maxIntentosValue.toIntOrNull()?.let { it > 0 } == true)
    val canSubmit = cursoId != null &&
        unidadId != null &&
        docenteId != null &&
        fecha != null &&
        isMaxIntentosValid

    val courseOptions = courses.map { course ->
        course.id to "${course.nombre} (${course.anio})"
    }
    val unitOptions = units.map { unit ->
        unit.id to unit.titulo
    }
    val teacherOptions = teachers
        .filter { it.rol == UserRole.DOCENTE }
        .map { teacher ->
            teacher.id to "${teacher.nombre} (${teacher.username})"
        }

    val selectedCourseLabel = courseOptions
        .firstOrNull { it.first == cursoId }
        ?.second
        .orEmpty()
    val selectedUnitLabel = unitOptions
        .firstOrNull { it.first == unidadId }
        ?.second
        .orEmpty()
    val selectedTeacherLabel = teacherOptions
        .firstOrNull { it.first == docenteId }
        ?.second
        .orEmpty()

    val selectedFieldsEnabled = !isEditMode && !isReadOnly && !isSaving
    val selectedFieldsHint = if (isEditMode) {
        "No se puede cambiar en edición"
    } else {
        "Obligatorio"
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        AssignmentDropdownField(
            label = "Curso",
            selectedLabel = selectedCourseLabel,
            placeholder = "Seleccionar curso",
            supportingText = selectedFieldsHint,
            enabled = selectedFieldsEnabled,
            options = courseOptions,
            onSelect = onCursoIdChange
        )

        AssignmentDropdownField(
            label = "Unidad",
            selectedLabel = selectedUnitLabel,
            placeholder = "Seleccionar unidad",
            supportingText = selectedFieldsHint,
            enabled = selectedFieldsEnabled,
            options = unitOptions,
            onSelect = onUnidadIdChange
        )

        AssignmentDropdownField(
            label = "Docente",
            selectedLabel = selectedTeacherLabel,
            placeholder = "Seleccionar docente",
            supportingText = selectedFieldsHint,
            enabled = selectedFieldsEnabled,
            options = teacherOptions,
            onSelect = onDocenteIdChange
        )

        // Fecha de la asignación.
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Fecha",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isReadOnly && !isSaving
            ) {
                Text(
                    text = fecha?.let { formatInstant(it) } ?: "Seleccionar fecha"
                )
            }

            Text(
                text = if (isEditMode) {
                    "Solo se envía si se modifica"
                } else {
                    "Obligatoria"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AppTextField(
            value = maxIntentos,
            onValueChange = onMaxIntentosChange,
            label = "Máximo de intentos",
            placeholder = "Ej. 2",
            supportingText = if (isEditMode) {
                "Opcional; vacío deja el valor actual. Debe ser mayor que cero"
            } else {
                "Opcional; vacío deja que el backend aplique 1. Debe ser mayor que cero"
            },
            enabled = !isReadOnly && !isSaving
        )

        // Habilitación de la prueba final.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Prueba final habilitada",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Habilita la prueba final de la unidad para el curso. " +
                        "El máximo de intentos se aplica a esa prueba.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = pruebaFinalHabilitada,
                onCheckedChange = onPruebaFinalHabilitadaChange,
                enabled = !isReadOnly && !isSaving
            )
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppButton(
                text = if (isEditMode) "Guardar cambios" else "Crear asignación",
                onClick = onSubmit,
                modifier = Modifier.weight(1f),
                enabled = canSubmit && !isReadOnly && !isSaving,
                variant = AppButtonVariant.PRIMARY
            )

            AppButton(
                text = "Cancelar",
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                enabled = !isSaving,
                variant = AppButtonVariant.OUTLINED
            )
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = fecha?.let { toPickerMillis(it) }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedMillis ->
                            onFechaChange(mergeDate(selectedMillis, fecha))
                        }
                        showDatePicker = false
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

/**
 * Campo de selección de una entidad.
 *
 * Recibe las opciones ya resueltas como pares `id`-etiqueta para que el formulario
 * no dependa de los nombres de los modelos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AssignmentDropdownField(
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

/** Formato local de la fecha de la asignación. */
private val ASSIGNMENT_DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

private fun formatInstant(instant: Instant): String =
    ASSIGNMENT_DATE_FORMATTER.withZone(ZoneId.systemDefault()).format(instant)

/** Convierte un instante a los milisegundos que espera el selector de fecha. */
private fun toPickerMillis(instant: Instant): Long =
    instant
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

/**
 * Combina la fecha elegida con la hora del instante actual.
 *
 * El selector de Material 3 solo devuelve la fecha. Para no alterar la hora ya
 * definida en una asignación existente, se conserva su hora local; en una creación
 * nueva se usa la hora actual.
 */
private fun mergeDate(selectedDateMillis: Long, current: Instant?): Instant {
    val selectedDate = Instant.ofEpochMilli(selectedDateMillis)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
    val currentTime = current
        ?.atZone(ZoneId.systemDefault())
        ?.toLocalTime()
        ?: LocalTime.now()

    return selectedDate
        .atTime(currentTime)
        .atZone(ZoneId.systemDefault())
        .toInstant()
}
