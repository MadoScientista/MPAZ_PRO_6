package com.ccstudio.mpaz_pro_6.ui.components.content

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
import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButtonVariant
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTextField
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow

/**
 * Formulario de contenido.
 *
 * Reúne los campos de título, explicación, ejemplos, instrucciones, orden y origen
 * con las validaciones del contrato. El estado de los campos llega por parámetro y
 * cada uno expone su callback; la pantalla arma el `CreateContentRequest` o el
 * `UpdateContentRequest`.
 *
 * El objetivo de aprendizaje no forma parte del formulario: se elige en la
 * pantalla en modo creación y no se puede cambiar en edición (`PUT` parcial).
 * `ejemplos`, `instrucciones` y `orden` son opcionales.
 */
@Composable
fun ContentForm(
    titulo: String,
    explicacion: String,
    ejemplos: String,
    instrucciones: String,
    orden: String,
    origen: ContentOrigin,
    onTituloChange: (String) -> Unit,
    onExplicacionChange: (String) -> Unit,
    onEjemplosChange: (String) -> Unit,
    onInstruccionesChange: (String) -> Unit,
    onOrdenChange: (String) -> Unit,
    onOrigenChange: (ContentOrigin) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isReadOnly: Boolean = false,
    isSaving: Boolean = false,
    error: String? = null
) {
    val ordenValue = orden.trim()
    val isOrdenValid = ordenValue.isEmpty() ||
        (ordenValue.toIntOrNull()?.let { it > 0 } == true)
    val canSubmit = titulo.isNotBlank() &&
        titulo.length <= MAX_TITLE_LENGTH &&
        explicacion.isNotBlank() &&
        isOrdenValid

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        AppTextField(
            value = titulo,
            onValueChange = onTituloChange,
            label = "Título",
            placeholder = "Ej. Representación de fracciones",
            supportingText = "${titulo.length}/$MAX_TITLE_LENGTH caracteres",
            enabled = !isReadOnly && !isSaving
        )

        AppTextField(
            value = explicacion,
            onValueChange = onExplicacionChange,
            label = "Explicación",
            placeholder = "Ej. Una fracción representa partes iguales de un todo.",
            supportingText = "Obligatoria",
            singleLine = false,
            enabled = !isReadOnly && !isSaving
        )

        AppTextField(
            value = ejemplos,
            onValueChange = onEjemplosChange,
            label = "Ejemplos",
            placeholder = "Ej. 3/4 representa tres de cuatro partes iguales.",
            supportingText = "Opcional",
            singleLine = false,
            enabled = !isReadOnly && !isSaving
        )

        AppTextField(
            value = instrucciones,
            onValueChange = onInstruccionesChange,
            label = "Instrucciones",
            placeholder = "Ej. Observa el numerador y el denominador.",
            supportingText = "Opcional",
            singleLine = false,
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

        // Origen del material: PROPIO, PUBLICO o AUTORIZADO.
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Origen",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FilterChipRow(
                options = ContentOrigin.entries.map { it.label },
                selected = origen.label,
                onSelect = { label ->
                    val newOrigin = ContentOrigin.entries
                        .firstOrNull { it.label == label }
                    if (newOrigin != null && !isReadOnly && !isSaving) {
                        onOrigenChange(newOrigin)
                    }
                }
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
                text = if (isEditMode) "Guardar cambios" else "Crear contenido",
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
private const val MAX_TITLE_LENGTH = 200
