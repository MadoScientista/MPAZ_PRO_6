package com.ccstudio.mpaz_pro_6.ui.components.learningobjective

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
 * Formulario de objetivo de aprendizaje.
 *
 * Reúne los campos de código, descripción, eje y texto de referencia con las
 * validaciones del contrato. El estado de los campos llega por parámetro y cada
 * uno expone su callback; la pantalla arma el
 * `CreateLearningObjectiveRequest` o el `UpdateLearningObjectiveRequest`.
 *
 * La unidad no forma parte del formulario: se elige en la pantalla en modo
 * creación y no se puede cambiar en edición (`PUT` parcial). El código y el texto
 * de referencia son opcionales; la descripción y el eje son obligatorios.
 */
@Composable
fun LearningObjectiveForm(
    codigo: String,
    descripcion: String,
    eje: String,
    textoReferencia: String,
    onCodigoChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onEjeChange: (String) -> Unit,
    onTextoReferenciaChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isReadOnly: Boolean = false,
    isSaving: Boolean = false,
    error: String? = null
) {
    val canSubmit = descripcion.isNotBlank() &&
        eje.isNotBlank() &&
        codigo.length <= MAX_CODE_LENGTH &&
        eje.length <= MAX_AXIS_LENGTH &&
        textoReferencia.length <= MAX_REFERENCE_TEXT_LENGTH

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        AppTextField(
            value = codigo,
            onValueChange = onCodigoChange,
            label = "Código",
            placeholder = "Ej. OA01",
            supportingText = "Opcional; único dentro de la unidad (máximo $MAX_CODE_LENGTH caracteres)",
            enabled = !isReadOnly && !isSaving
        )

        AppTextField(
            value = descripcion,
            onValueChange = onDescripcionChange,
            label = "Descripción",
            placeholder = "Ej. Representar fracciones propias e impropias",
            supportingText = "Obligatoria",
            singleLine = false,
            enabled = !isReadOnly && !isSaving
        )

        AppTextField(
            value = eje,
            onValueChange = onEjeChange,
            label = "Eje",
            placeholder = "Ej. Números",
            supportingText = "Obligatorio; ${eje.length}/$MAX_AXIS_LENGTH caracteres",
            enabled = !isReadOnly && !isSaving
        )

        AppTextField(
            value = textoReferencia,
            onValueChange = onTextoReferenciaChange,
            label = "Texto de referencia",
            placeholder = "Ej. Texto escolar de Matemática",
            supportingText = "Opcional; ${textoReferencia.length}/$MAX_REFERENCE_TEXT_LENGTH caracteres",
            singleLine = false,
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
                text = if (isEditMode) "Guardar cambios" else "Crear objetivo",
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

/** Longitud máxima del código según el contrato. */
private const val MAX_CODE_LENGTH = 50

/** Longitud máxima del eje según el contrato. */
private const val MAX_AXIS_LENGTH = 100

/** Longitud máxima del texto de referencia según el contrato. */
private const val MAX_REFERENCE_TEXT_LENGTH = 500
