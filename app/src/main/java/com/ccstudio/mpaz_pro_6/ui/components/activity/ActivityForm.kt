package com.ccstudio.mpaz_pro_6.ui.components.activity

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
import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButtonVariant
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTextField
import com.ccstudio.mpaz_pro_6.ui.components.common.FilterChipRow
import com.ccstudio.mpaz_pro_6.ui.theme.TextFieldShape

/**
 * Formulario de actividad.
 *
 * Reúne el título, el tipo y el orden con las validaciones del contrato. El estado
 * de los campos llega por parámetro y cada uno expone su callback; la pantalla arma
 * el `CreateActivityRequest` o el `UpdateActivityRequest`.
 *
 * La unidad no forma parte del formulario: se elige en la pantalla en modo creación
 * y no se puede cambiar en edición (`PUT` parcial). La pantalla pasa `contents` ya
 * filtrado por la unidad y limpia `contenidoId` cuando esa unidad cambia. El título
 * es único dentro de la unidad y solo se admite una `PRUEBA_FINAL` por unidad.
 * Cuando la unidad ya tiene una `PRUEBA_FINAL` distinta de esta actividad, la
 * pantalla pasa `hasFinalTest` en `true`: el tipo `PRUEBA_FINAL` queda bloqueado y
 * el formulario lo explica.
 *
 * `contenidoId` nulo significa que la actividad es transversal. La `PRUEBA_FINAL`
 * se propone sin contenido y el selector queda deshabilitado para ese tipo.
 *
 * `isContentLocked` fija el contenido elegido por la pantalla de origen (por
 * ejemplo, el detalle de un contenido): el selector se bloquea y la prueba final
 * no se ofrece, porque se gestiona fuera del contenido.
 *
 * `orden` es opcional: al crear, vacío deja que el backend aplique `1`; al editar,
 * vacío conserva el orden actual.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityForm(
    titulo: String,
    tipo: ActivityType,
    orden: String,
    contents: List<Content>,
    contenidoId: Int?,
    onTituloChange: (String) -> Unit,
    onTipoChange: (ActivityType) -> Unit,
    onOrdenChange: (String) -> Unit,
    onContenidoChange: (Int?) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    isReadOnly: Boolean = false,
    isSaving: Boolean = false,
    error: String? = null,
    hasFinalTest: Boolean = false,
    isContentLocked: Boolean = false
) {
    val ordenValue = orden.trim()
    val isOrdenValid = ordenValue.isEmpty() ||
        (ordenValue.toIntOrNull()?.let { it > 0 } == true)
    val canSubmit = titulo.isNotBlank() &&
        titulo.length <= MAX_TITLE_LENGTH &&
        isOrdenValid

    val activityTypeOptions = ActivityType.entries
    val disabledTypes = buildSet {
        if (hasFinalTest) add(ActivityType.FINAL_TEST.label)
        if (isContentLocked) add(ActivityType.FINAL_TEST.label)
    }

    val canChooseContent = !isReadOnly &&
        !isSaving &&
        !isContentLocked &&
        tipo != ActivityType.FINAL_TEST
    var contentMenuExpanded by rememberSaveable { mutableStateOf(false) }
    val selectedContentTitle = contents.firstOrNull { it.id == contenidoId }?.titulo
    val contentFieldValue = when {
        contenidoId == null -> "Sin contenido (transversal)"
        selectedContentTitle != null -> selectedContentTitle
        else -> "Contenido no disponible"
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
                options = activityTypeOptions.map { it.label },
                selected = tipo.label,
                onSelect = { label ->
                    val newType = activityTypeOptions
                        .firstOrNull { it.label == label }
                    if (newType != null && !isReadOnly && !isSaving) {
                        onTipoChange(newType)
                    }
                },
                disabledOptions = disabledTypes
            )

            if (hasFinalTest) {
                Text(
                    text = "La unidad ya tiene una prueba final: no se puede elegir ese tipo.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else if (isContentLocked) {
                Text(
                    text = "La prueba final se gestiona fuera del contenido.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (tipo == ActivityType.FINAL_TEST) {
                Text(
                    text = "Solo se admite una prueba final por unidad.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Contenido opcional: sin contenido la actividad es transversal.
        ExposedDropdownMenuBox(
            expanded = contentMenuExpanded && canChooseContent,
            onExpandedChange = {
                if (canChooseContent) {
                    contentMenuExpanded = !contentMenuExpanded
                }
            }
        ) {
            OutlinedTextField(
                value = contentFieldValue,
                onValueChange = {},
                readOnly = true,
                shape = TextFieldShape,
                label = { Text(text = "Contenido") },
                supportingText = {
                    Text(
                        text = when {
                            isContentLocked ->
                                "La actividad queda asociada a este contenido."

                            tipo == ActivityType.FINAL_TEST ->
                                "La prueba final se propone sin contenido."

                            else ->
                                "Opcional; sin contenido la actividad es transversal."
                        }
                    )
                },
                trailingIcon = {
                    if (canChooseContent) {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = contentMenuExpanded
                        )
                    }
                },
                enabled = canChooseContent,
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = contentMenuExpanded && canChooseContent,
                onDismissRequest = { contentMenuExpanded = false },
                shape = TextFieldShape,
                shadowElevation = 0.dp
            ) {
                DropdownMenuItem(
                    text = { Text(text = "Sin contenido (transversal)") },
                    onClick = {
                        onContenidoChange(null)
                        contentMenuExpanded = false
                    }
                )
                contents.forEach { content ->
                    DropdownMenuItem(
                        text = { Text(text = content.titulo) },
                        onClick = {
                            onContenidoChange(content.id)
                            contentMenuExpanded = false
                        }
                    )
                }
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
