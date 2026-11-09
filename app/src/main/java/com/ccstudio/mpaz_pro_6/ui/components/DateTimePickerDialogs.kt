package com.ccstudio.mpaz_pro_6.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

/** Diálogo de selección de fecha, alineado al uso de `DatePicker` de Material 3. */
@Composable
fun AppDatePickerDialog(
    initialDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
            ?.atStartOfDay(ZoneOffset.UTC)
            ?.toInstant()
            ?.toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    state.selectedDateMillis?.let { millis ->
                        onDateSelected(
                            Instant.ofEpochMilli(millis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                        )
                    }
                    onDismiss()
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = state)
    }
}

/** Diálogo de selección de hora en formato 24 horas. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimePickerDialog(
    initialTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    TimePickerDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar hora") },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(LocalTime.of(state.hour, state.minute))
                    onDismiss()
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        TimePicker(state = state)
    }
}
