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
import com.ccstudio.mpaz_pro_6.data.enums.ContentResourceType
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButtonVariant
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTextField
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow

/**
 * Formulario de recurso de contenido.
 *
 * Reúne los campos de tipo, URL, descripción y origen. El catálogo actual del
 * contrato admite `IMAGEN` y `AUDIO`; si el backend agrega valores, se amplía el
 * enum `ContentResourceType` y el formulario los toma automáticamente.
 *
 * El contenido asociado no forma parte del formulario: se elige en la pantalla en
 * modo creación y no se puede cambiar en edición (`PUT` parcial). La descripción
 * es opcional; la URL admite hasta 500 caracteres.
 */
@Composable
fun ContentResourceForm(
    tipo: ContentResourceType,
    url: String,
    descripcion: String,
    origen: ContentOrigin,
    onTipoChange: (ContentResourceType) -> Unit,
    onUrlChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onOrigenChange: (ContentOrigin) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isReadOnly: Boolean = false,
    isSaving: Boolean = false,
    error: String? = null
) {
    val canSubmit = url.isNotBlank() &&
        url.length <= MAX_RESOURCE_TEXT_LENGTH &&
        descripcion.length <= MAX_RESOURCE_TEXT_LENGTH

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Tipo de recurso: IMAGEN o AUDIO.
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tipo de recurso",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FilterChipRow(
                options = ContentResourceType.entries.map { it.label },
                selected = tipo.label,
                onSelect = { label ->
                    val newType = ContentResourceType.entries
                        .firstOrNull { it.label == label }
                    if (newType != null && !isReadOnly && !isSaving) {
                        onTipoChange(newType)
                    }
                }
            )
        }

        AppTextField(
            value = url,
            onValueChange = onUrlChange,
            label = "URL",
            placeholder = "Ej. https://ejemplo.test/imagen.png",
            supportingText = "Obligatoria; ${url.length}/$MAX_RESOURCE_TEXT_LENGTH caracteres",
            singleLine = false,
            enabled = !isReadOnly && !isSaving
        )

        AppTextField(
            value = descripcion,
            onValueChange = onDescripcionChange,
            label = "Descripción",
            placeholder = "Ej. Representación visual del concepto",
            supportingText = "Opcional; ${descripcion.length}/$MAX_RESOURCE_TEXT_LENGTH caracteres",
            singleLine = false,
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
                text = if (isEditMode) "Guardar cambios" else "Crear recurso",
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

/** Longitud máxima de la URL y de la descripción según el contrato. */
private const val MAX_RESOURCE_TEXT_LENGTH = 500
