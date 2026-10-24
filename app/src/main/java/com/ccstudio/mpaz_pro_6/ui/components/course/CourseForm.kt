package com.ccstudio.mpaz_pro_6.ui.components.course

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButtonVariant
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTextField

/**
 * Formulario de curso.
 *
 * Reúne el nombre y el año del curso con las validaciones del contrato: nombre
 * obligatorio de hasta 50 caracteres y año obligatorio entre 2000 y 2100. El
 * estado de los campos llega por parámetro y cada uno expone su callback; la
 * pantalla arma el `CreateCourseRequest` o el `UpdateCourseRequest`.
 *
 * En edición el `PUT` es parcial: la pantalla envía solo los campos modificados.
 * El par nombre-año es único y un duplicado devuelve `409 Conflict`, que la
 * pantalla se encarga de explicar.
 */
@Composable
fun CourseForm(
    nombre: String,
    anio: String,
    onNombreChange: (String) -> Unit,
    onAnioChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isReadOnly: Boolean = false,
    isSaving: Boolean = false,
    error: String? = null
) {
    val anioValue = anio.trim().toIntOrNull()
    val isAnioValid = anioValue != null && anioValue in MIN_YEAR..MAX_YEAR
    val canSubmit = nombre.isNotBlank() &&
        nombre.length <= MAX_NAME_LENGTH &&
        isAnioValid

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        AppTextField(
            value = nombre,
            onValueChange = onNombreChange,
            label = "Nombre",
            placeholder = "Ej. 6A",
            supportingText = "${nombre.length}/$MAX_NAME_LENGTH caracteres",
            enabled = !isReadOnly && !isSaving
        )

        AppTextField(
            value = anio,
            onValueChange = onAnioChange,
            label = "Año",
            placeholder = "Ej. 2026",
            supportingText = "Obligatorio; entre $MIN_YEAR y $MAX_YEAR",
            enabled = !isReadOnly && !isSaving
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
                text = if (isEditMode) "Guardar cambios" else "Crear curso",
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

/** Longitud máxima del nombre del curso según el contrato. */
private const val MAX_NAME_LENGTH = 50

/** Año mínimo admitido por el contrato. */
private const val MIN_YEAR = 2000

/** Año máximo admitido por el contrato. */
private const val MAX_YEAR = 2100
