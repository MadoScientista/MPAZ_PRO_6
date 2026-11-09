package com.ccstudio.mpaz_pro_6.ui.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val DATE_TIME_INPUT_FORMATS = listOf(
    "yyyy-MM-dd HH:mm:ss",
    "yyyy-MM-dd HH:mm",
    "yyyy-MM-dd'T'HH:mm:ss"
)

private val DATE_INPUT_FORMATS = listOf(
    "yyyy-MM-dd"
)

private val DATE_TIME_OUTPUT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
private val DATE_OUTPUT_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val DATE_TIME_INPUT_OUTPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
private val DATE_TIME_INPUT_OUTPUT_SECONDS_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
private val DATE_INPUT_OUTPUT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd")

/**
 * Formatea fechas del backend para mostrarlas en la UI.
 *
 * Acepta `yyyy-MM-dd`, `yyyy-MM-dd HH:mm`, `yyyy-MM-dd HH:mm:ss` y el formato
 * ISO con `T`. Si el valor no coincide con ninguno, se devuelve tal cual para no
 * ocultar información.
 */
fun formatFecha(value: String?): String {
    if (value.isNullOrBlank()) return ""

    DATE_TIME_INPUT_FORMATS.forEach { pattern ->
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(pattern))
                .format(DATE_TIME_OUTPUT_FORMAT)
        } catch (_: Exception) {
            // Se prueba el siguiente formato.
        }
    }

    DATE_INPUT_FORMATS.forEach { pattern ->
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern))
                .format(DATE_OUTPUT_FORMAT)
        } catch (_: Exception) {
            // Se prueba el siguiente formato.
        }
    }

    return value
}

/** Convierte un valor del backend a fecha-hora, o `null` si no se puede parsear. */
fun parseFechaHora(value: String?): LocalDateTime? {
    if (value.isNullOrBlank()) return null

    DATE_TIME_INPUT_FORMATS.forEach { pattern ->
        try {
            return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(pattern))
        } catch (_: Exception) {
            // Se prueba el siguiente formato.
        }
    }

    DATE_INPUT_FORMATS.forEach { pattern ->
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern)).atStartOfDay()
        } catch (_: Exception) {
            // Se prueba el siguiente formato.
        }
    }

    return null
}

/** Convierte un valor del backend a fecha, o `null` si no se puede parsear. */
fun parseFecha(value: String?): LocalDate? {
    if (value.isNullOrBlank()) return null

    DATE_INPUT_FORMATS.forEach { pattern ->
        try {
            return LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern))
        } catch (_: Exception) {
            // Se prueba el siguiente formato.
        }
    }

    return parseFechaHora(value)?.toLocalDate()
}

/** Formato de entrada usado por las configuraciones de actividad. */
fun formatFechaHoraInput(value: LocalDateTime): String =
    value.format(DATE_TIME_INPUT_OUTPUT_FORMAT)

/** Formato de entrada usado por los informes (incluye segundos). */
fun formatFechaHoraInputSegundos(value: LocalDateTime): String =
    value.format(DATE_TIME_INPUT_OUTPUT_SECONDS_FORMAT)

/** Formato de entrada de fecha usado por los informes. */
fun formatFechaInput(value: LocalDate): String =
    value.format(DATE_INPUT_OUTPUT_FORMAT)
