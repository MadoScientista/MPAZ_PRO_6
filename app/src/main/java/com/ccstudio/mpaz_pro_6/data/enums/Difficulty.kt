package com.ccstudio.mpaz_pro_6.data.enums

/**
 * Dificultad de una pregunta.
 *
 * Debe mapear 1:1 el enum `Dificultad` del backend: `BAJA`, `MEDIA` y `ALTA`.
 */
enum class Difficulty(val wireValue: String) {
    LOW("BAJA"),
    MEDIUM("MEDIA"),
    HIGH("ALTA");

    companion object {
        /**
         * Convierte el valor del backend en su enum.
         *
         * Devuelve `null` si el valor no corresponde a ninguna dificultad conocida,
         * para que el mapper pueda reportar el dato inesperado sin lanzar una
         * excepción.
         */
        fun fromWire(value: String): Difficulty? = entries.firstOrNull { it.wireValue == value }
    }
}
