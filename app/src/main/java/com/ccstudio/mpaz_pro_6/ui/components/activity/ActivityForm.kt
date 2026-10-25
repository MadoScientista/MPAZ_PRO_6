package com.ccstudio.mpaz_pro_6.ui.components.activity

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
import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButtonVariant
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTextField
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow

/**
 * Formulario de actividad.
 *
 * Reúne el título, el tipo y el orden con las validaciones del contrato. El estado
 * de los campos llega por parámetro y cada uno expone su callback; la pantalla arma
 * el `CreateActivityRequest` o el `UpdateActivityRequest`.
 *
 * La unidad no forma parte del formulario: se elige en la pantalla en modo creación
 * y no se puede cambiar en edición (`PUT` parcial). El título es único dentro de la
 * unidad y solo se admite una `PRUEBA_FINAL` por unidad. Cuando la unidad ya tiene
 * una `PRUEBA_FINAL` distinta de esta actividad, la pantalla pasa `hasFinalTest`
 * en `true`: el tipo `PRUEBA_FINAL` queda bloqueado y el formulario lo explica.
 *
 * `orden` es opcional: al crear, vacío deja que el backend aplique `1`; al editar,
 * vacío conserva el orden actual.
 */
@Composable
fun ActivityForm(
    titulo: String,
    tipo: ActivityType,
    orden: String,
    onTituloChange: (String) -> Unit,
    onTipoChange: (ActivityType) -> Unit,
    onOrdenChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isReadOnly: Boolean = false,
    isSaving: Boolean = false,
    error: String? = null,
    hasFinalTest: Boolean = false
) {
    val ordenValue = orden.trim()
    val isOrdenValid = ordenValue.isEmpty() ||
        (ordenValue.toIntOrNull()?.let { it > 0 } == true)
    val canSubmit = titulo.isNotBlank() &&
        titulo.length <= MAX_TITLE_LENGTH &&
        isOrdenValid

    val disabledTypes = if (hasFinalTest) {
        setOf(ActivityType.FINAL_TEST.wireValue)
    } else {
        emptySet()
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        AppTextField(
            value = titulo,
            onValueChange = onTituloChange,
            label = "Título",
            placeholder = "Ej. Explorar un texto informativo",
            supportingText = "${titulo.length}/$MAX_TITLE_LENGTH caracteres",
            enabled = !isReadOnly && !isSaving
        )

        // Tipo de actividad: selección exclusiva entre ACTIVIDAD, DESAFIO y PRUEBA_FINAL.
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tipo",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FilterChipRow(
                options = ActivityType.entries.map { it.wireValue },
                selected = tipo.wireValue,
                onSelect = { wireValue ->
                    val newType = ActivityType.fromWire(wireValue)
                    if (newType != null && !isReadOnly && !isSaving) {
                        onTipoChange(newType)
                    }
                },
                disabledOptions = disabledTypes
            )

            if (hasFinalTest) {
                Text(
                    text = "La unidad ya tiene una PRUEBA_FINAL: no se puede elegir ese tipo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else if (tipo == ActivityType.FINAL_TEST) {
                Text(
                    text = "Solo se admite una PRUEBA_FINAL por unidad.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        AppTextField(
            value = orden,
            onValueChange = onOrdenChange,
            label = "Orden",
            placeholder = "Ej. 1",
            supportingText = if (isEditMode) {
                "Opcional; vacío conserva el orden actual. Debe ser mayor que cero"
            } else {
                "Opcional; vacío deja que el backend aplique 1. Debe ser mayor que cero"
            },
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
                text = if (isEditMode) "Guardar cambios" else "Crear actividad",
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
