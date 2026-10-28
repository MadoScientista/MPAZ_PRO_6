package com.ccstudio.mpaz_pro_6.data.enums

/**
 * Nivel alcanzado por un estudiante en un objetivo de aprendizaje.
 *
 * Debe mapear 1:1 el enum `NivelOa` del backend: `FORTALEZA` y `REFUERZO`. El
 * backend asigna `FORTALEZA` si `correctas * 100.0 / total >= 70` y `REFUERZO` en
 * caso contrario.
 */
enum class ObjectiveLevel(val wireValue: String, val label: String) {
    STRENGTH("FORTALEZA", "Fortaleza"),
    SUPPORT("REFUERZO", "Refuerzo");

    companion object {
        /**
         * Convierte el valor del backend en su enum.
         *
         * Devuelve `null` si el valor no corresponde a ningún nivel conocido, para
         * que el mapper pueda reportar el dato inesperado sin lanzar una excepción.
         */
        fun fromWire(value: String): ObjectiveLevel? = entries.firstOrNull { it.wireValue == value }
    }
}
