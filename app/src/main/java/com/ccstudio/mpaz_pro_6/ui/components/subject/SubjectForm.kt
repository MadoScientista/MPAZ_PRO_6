package com.ccstudio.mpaz_pro_6.ui.components.subject

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
 * Formulario de asignatura.
 *
 * Solo tiene el nombre, que es obligatorio y admite hasta 100 caracteres. El
 * estado del campo llega por parámetro; la pantalla arma el `CreateSubjectRequest`
 * o el `UpdateSubjectRequest`.
 *
 * En edición el `PUT` es parcial, pero el contrato exige el nombre, así que la
 * pantalla envía el valor actual. Un nombre duplicado devuelve `409 Conflict`,
 * que la pantalla se encarga de explicar.
 */
@Composable
fun SubjectForm(
    nombre: String,
    onNombreChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isReadOnly: Boolean = false,
    isSaving: Boolean = false,
    error: String? = null
) {
    val canSubmit = nombre.isNotBlank() &&
        nombre.length <= MAX_NAME_LENGTH

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        AppTextField(
            value = nombre,
            onValueChange = onNombreChange,
            label = "Nombre",
            placeholder = "Ej. Matemática",
            supportingText = "${nombre.length}/$MAX_NAME_LENGTH caracteres",
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
                text = if (isEditMode) "Guardar cambios" else "Crear asignatura",
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

/** Longitud máxima del nombre de la asignatura según el contrato. */
private const val MAX_NAME_LENGTH = 100
