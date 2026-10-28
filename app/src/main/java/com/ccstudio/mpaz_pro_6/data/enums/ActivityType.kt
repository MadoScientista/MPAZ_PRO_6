package com.ccstudio.mpaz_pro_6.data.enums

/**
 * Tipo de actividad.
 *
 * Debe mapear 1:1 el enum `TipoActividad` del backend: `ACTIVIDAD`, `DESAFIO` y
 * `PRUEBA_FINAL`. Solo se admite una `PRUEBA_FINAL` por unidad y no se puede
 * asociar como actividad de una asignación.
 */
enum class ActivityType(val wireValue: String, val label: String) {
    ACTIVITY("ACTIVIDAD", "Actividad"),
    CHALLENGE("DESAFIO", "Desafío"),
    FINAL_TEST("PRUEBA_FINAL", "Prueba final");

    companion object {
        /**
         * Convierte el valor del backend en su enum.
         *
         * Devuelve `null` si el valor no corresponde a ningún tipo conocido, para que
         * el mapper pueda reportar el dato inesperado sin lanzar una excepción.
         */
        fun fromWire(value: String): ActivityType? = entries.firstOrNull { it.wireValue == value }
    }
}
