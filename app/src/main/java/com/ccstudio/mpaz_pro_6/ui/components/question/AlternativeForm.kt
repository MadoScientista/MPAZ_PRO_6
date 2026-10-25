package com.ccstudio.mpaz_pro_6.ui.components.question

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
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
 * Formulario de una alternativa.
 *
 * Reúne el texto de la alternativa y el control de "es correcta". La selección es
 * exclusiva: marcar una alternativa correcta debe desmarcar las demás, y esa
 * regla la aplica la pantalla; este formulario solo refleja y notifica el valor
 * actual. El texto admite hasta 500 caracteres según el contrato.
 */
@Composable
fun AlternativeForm(
    texto: String,
    esCorrecta: Boolean,
    onTextoChange: (String) -> Unit,
    onEsCorrectaChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isReadOnly: Boolean = false,
    isSaving: Boolean = false,
    error: String? = null
) {
    val isTextoValid = texto.isNotBlank() && texto.length <= MAX_ALTERNATIVE_TEXT_LENGTH
    val canSubmit = isTextoValid

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        AppTextField(
            value = texto,
            onValueChange = onTextoChange,
            label = "Texto de la alternativa",
            placeholder = "Ej. Tres cuartos",
            supportingText = "${texto.length}/$MAX_ALTERNATIVE_TEXT_LENGTH caracteres",
            singleLine = false,
            enabled = !isReadOnly && !isSaving
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = esCorrecta,
                onCheckedChange = { checked ->
                    if (!isReadOnly && !isSaving) {
                        onEsCorrectaChange(checked)
                    }
                },
                enabled = !isReadOnly && !isSaving
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Es la alternativa correcta",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Solo una alternativa puede ser correcta por pregunta",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
                text = if (isEditMode) "Guardar cambios" else "Agregar alternativa",
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

/** Longitud máxima del texto de una alternativa según el contrato. */
private const val MAX_ALTERNATIVE_TEXT_LENGTH = 500
