package com.ccstudio.mpaz_pro_6.ui.components.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButton
import com.ccstudio.mpaz_pro_6.ui.components.common.AppButtonVariant
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTextField
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Formulario simple para configurar la prueba final de una unidad.
 *
 * Solo reúne los campos que admite `PUT /api/asignaciones/{id}`: fecha de
 * aplicación, máximo de intentos y habilitación de la prueba final. La fecha se
 * elige con el selector de Material 3 y conserva la hora del valor actual.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalTestForm(
    fecha: Instant?,
    maxIntentos: String,
    pruebaFinalHabilitada: Boolean,
    onFechaChange: (Instant?) -> Unit,
    onMaxIntentosChange: (String) -> Unit,
    onPruebaFinalHabilitadaChange: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    isSaving: Boolean = false,
    error: String? = null
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }

    val maxIntentosValue = maxIntentos.trim()
    val isMaxIntentosValid = maxIntentosValue.isEmpty() ||
        (maxIntentosValue.toIntOrNull()?.let { it > 0 } == true)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Fecha de aplicación.
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Fecha de aplicación",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving
            ) {
                Text(
                    text = fecha?.let { formatInstant(it) } ?: "Seleccionar fecha"
                )
            }

            Text(
                text = "Obligatoria; solo se envía si se modifica",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AppTextField(
            value = maxIntentos,
            onValueChange = onMaxIntentosChange,
            label = "Número máximo de intentos",
            placeholder = "Ej. 2",
            supportingText = "Opcional; vacío deja el valor actual. Debe ser mayor que cero",
            enabled = !isSaving
        )

        // Habilitación de la prueba final.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Prueba final " +
                        if (pruebaFinalHabilitada) "activada" else "desactivada",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = if (pruebaFinalHabilitada) {
                        "Los estudiantes pueden resolver la prueba final."
                    } else {
                        "Los estudiantes verán que la prueba final todavía no está habilitada."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = pruebaFinalHabilitada,
                onCheckedChange = onPruebaFinalHabilitadaChange,
                enabled = !isSaving
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
                text = "Guardar cambios",
                onClick = onSubmit,
                modifier = Modifier.weight(1f),
                enabled = fecha != null && isMaxIntentosValid && !isSaving,
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

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = fecha?.let { toPickerMillis(it) }
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedMillis ->
                            onFechaChange(mergeDate(selectedMillis, fecha))
                        }
                        showDatePicker = false
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

/** Formato local de la fecha de aplicación. */
private val FINAL_TEST_FORM_DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

private fun formatInstant(instant: Instant): String =
    FINAL_TEST_FORM_DATE_FORMATTER.withZone(ZoneId.systemDefault()).format(instant)

/** Convierte un instante a los milisegundos que espera el selector de fecha. */
private fun toPickerMillis(instant: Instant): Long =
    instant
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

/**
 * Combina la fecha elegida con la hora del instante actual.
 *
 * El selector de Material 3 solo devuelve la fecha. Para no alterar la hora ya
 * definida en la asignación, se conserva su hora local; si no existe, se usa la
 * hora actual.
 */
private fun mergeDate(selectedDateMillis: Long, current: Instant?): Instant {
    val selectedDate = Instant.ofEpochMilli(selectedDateMillis)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
    val currentTime = current
        ?.atZone(ZoneId.systemDefault())
        ?.toLocalTime()
        ?: LocalTime.now()

    return selectedDate
        .atTime(currentTime)
        .atZone(ZoneId.systemDefault())
        .toInstant()
}
