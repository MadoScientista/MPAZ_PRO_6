package com.ccstudio.mpaz_pro_6.ui.components.unit

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButtonVariant
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTextField
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow

/**
 * Formulario de unidad.
 *
 * Reúne los campos de creación y edición de una unidad: asignatura, nivel
 * educativo, título, orden y estado. El estado de los campos llega por parámetro y
 * cada uno expone su callback; la pantalla arma el `CreateLearningUnitRequest` o
 * el `UpdateLearningUnitRequest`.
 *
 * La asignatura no se puede cambiar en edición. La creación solo admite los
 * estados iniciales (`BORRADOR` y `EN_REVISION`), así que el formulario de alta
 * fija el estado en `BORRADOR`. Los estados `AUTORIZADO` y `PUBLICADO` se
 * gestionan desde la pantalla de autorización.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitForm(
    subjects: List<Subject>,
    asignaturaId: Int?,
    nivelEducativo: String,
    titulo: String,
    orden: String,
    estado: ContentStatus,
    onAsignaturaIdChange: (Int?) -> Unit,
    onNivelEducativoChange: (String) -> Unit,
    onTituloChange: (String) -> Unit,
    onOrdenChange: (String) -> Unit,
    onEstadoChange: (ContentStatus) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isReadOnly: Boolean = false,
    isSaving: Boolean = false,
    error: String? = null
) {
    var subjectMenuExpanded by rememberSaveable { mutableStateOf(false) }

    val ordenValue = orden.trim()
    val isOrdenValid = ordenValue.isEmpty() ||
        (ordenValue.toIntOrNull()?.let { it > 0 } == true)
    val canSubmit = asignaturaId != null &&
        titulo.isNotBlank() &&
        titulo.length <= MAX_TITULO_LENGTH &&
        nivelEducativo.length <= MAX_NIVEL_LENGTH &&
        isOrdenValid

    val selectedSubjectLabel = subjects
        .firstOrNull { it.id == asignaturaId }
        ?.nombre
        .orEmpty()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        ExposedDropdownMenuBox(
            expanded = subjectMenuExpanded,
            onExpandedChange = {
                if (!isEditMode && !isReadOnly && !isSaving) {
                    subjectMenuExpanded = !subjectMenuExpanded
                }
            }
        ) {
            OutlinedTextField(
                value = selectedSubjectLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text(text = "Asignatura") },
                placeholder = { Text(text = "Seleccionar asignatura") },
                supportingText = {
                    Text(
                        text = if (isEditMode) {
                            "La asignatura no se puede cambiar"
                        } else {
                            "Obligatoria"
                        }
                    )
                },
                trailingIcon = {
                    if (!isEditMode) {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = subjectMenuExpanded
                        )
                    }
                },
                enabled = !isEditMode && !isReadOnly && !isSaving,
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = subjectMenuExpanded,
                onDismissRequest = { subjectMenuExpanded = false },
                shadowElevation = 0.dp
            ) {
                subjects.forEach { subject ->
                    DropdownMenuItem(
                        text = { Text(text = subject.nombre) },
                        onClick = {
                            onAsignaturaIdChange(subject.id)
                            subjectMenuExpanded = false
                        }
                    )
                }
            }
        }

        AppTextField(
            value = nivelEducativo,
            onValueChange = onNivelEducativoChange,
            label = "Nivel educativo",
            placeholder = "Ej. Sexto Basico",
            supportingText = "Opcional; por defecto el backend usa \"Sexto Basico\" (máximo $MAX_NIVEL_LENGTH caracteres)",
            enabled = !isReadOnly && !isSaving
        )

        AppTextField(
            value = titulo,
            onValueChange = onTituloChange,
            label = "Título",
            placeholder = "Ej. Fracciones",
            supportingText = "${titulo.length}/$MAX_TITULO_LENGTH caracteres",
            enabled = !isReadOnly && !isSaving
        )

        AppTextField(
            value = orden,
            onValueChange = onOrdenChange,
            label = "Orden",
            placeholder = "Ej. 1",
            supportingText = "Opcional; debe ser mayor que cero",
            enabled = !isReadOnly && !isSaving
        )

        if (isEditMode) {
            if (estado.esPendienteDeRevision()) {
                FilterChipRow(
                    options = INITIAL_STATUSES.map { it.wireValue },
                    selected = estado.wireValue,
                    onSelect = { wireValue ->
                        val newStatus = ContentStatus.fromWire(wireValue)
                        if (newStatus != null && !isReadOnly && !isSaving) {
                            onEstadoChange(newStatus)
                        }
                    }
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Estado",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    UnitStatusChip(status = estado)

                    Text(
                        text = "El estado se gestiona desde la pantalla de autorización",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Estado inicial",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                UnitStatusChip(status = ContentStatus.DRAFT)
            }
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
                text = if (isEditMode) "Guardar cambios" else "Crear unidad",
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
}

/** Longitud máxima del título según el contrato. */
private const val MAX_TITULO_LENGTH = 200

/** Longitud máxima del nivel educativo según el contrato. */
private const val MAX_NIVEL_LENGTH = 50

/** Estados que admite la creación de una unidad. */
private val INITIAL_STATUSES = listOf(
    ContentStatus.DRAFT,
    ContentStatus.IN_REVIEW
)
