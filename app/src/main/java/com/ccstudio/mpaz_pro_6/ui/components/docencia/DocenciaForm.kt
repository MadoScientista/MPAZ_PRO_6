package com.ccstudio.mpaz_pro_6.ui.components.docencia

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButtonVariant
import com.ccstudio.mpaz_pro_6.ui.theme.TextFieldShape

/**
 * Formulario de docencia.
 *
 * Al crear pide docente, curso y asignatura. Al editar, el curso y la asignatura
 * quedan bloqueados porque el `PUT` parcial solo admite cambiar al docente.
 * El selector de docente solo ofrece usuarios con rol `DOCENTE`.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocenciaForm(
    docentes: List<User>,
    cursos: List<Course>,
    asignaturas: List<Subject>,
    docenteId: Int?,
    cursoId: Int?,
    asignaturaId: Int?,
    onDocenteIdChange: (Int?) -> Unit,
    onCursoIdChange: (Int?) -> Unit,
    onAsignaturaIdChange: (Int?) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isSaving: Boolean = false,
    error: String? = null
) {
    val docenteOptions = docentes
        .filter { it.rol == UserRole.DOCENTE }
        .map { it.id to "${it.nombre} (${it.username})" }
    val cursoOptions = cursos.map { it.id to "${it.nombre} (${it.anio})" }
    val asignaturaOptions = asignaturas.map { it.id to it.nombre }

    val selectedDocente = docenteOptions
        .firstOrNull { it.first == docenteId }
        ?.second
        .orEmpty()
    val selectedCurso = cursoOptions
        .firstOrNull { it.first == cursoId }
        ?.second
        .orEmpty()
    val selectedAsignatura = asignaturaOptions
        .firstOrNull { it.first == asignaturaId }
        ?.second
        .orEmpty()

    val canSubmit = docenteId != null &&
        cursoId != null &&
        asignaturaId != null

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DocenciaDropdownField(
            label = "Docente",
            selectedLabel = selectedDocente,
            placeholder = "Seleccionar docente",
            supportingText = "El usuario debe tener rol DOCENTE",
            enabled = !isSaving,
            options = docenteOptions,
            onSelect = onDocenteIdChange
        )

        DocenciaDropdownField(
            label = "Curso",
            selectedLabel = selectedCurso,
            placeholder = "Seleccionar curso",
            supportingText = if (isEditMode) {
                "No se puede cambiar en edición"
            } else {
                "Obligatorio"
            },
            enabled = !isEditMode && !isSaving,
            options = cursoOptions,
            onSelect = onCursoIdChange
        )

        DocenciaDropdownField(
            label = "Asignatura",
            selectedLabel = selectedAsignatura,
            placeholder = "Seleccionar asignatura",
            supportingText = if (isEditMode) {
                "No se puede cambiar en edición"
            } else {
                "Obligatoria"
            },
            enabled = !isEditMode && !isSaving,
            options = asignaturaOptions,
            onSelect = onAsignaturaIdChange
        )

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
                text = if (isEditMode) "Guardar cambios" else "Crear docencia",
                onClick = onSubmit,
                modifier = Modifier.weight(1f),
                enabled = canSubmit && !isSaving,
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
}

/** Selector simple de una entidad con opciones `id`-etiqueta. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DocenciaDropdownField(
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
            if (enabled) expanded = !expanded
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
