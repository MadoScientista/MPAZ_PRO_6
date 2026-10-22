package com.ccstudio.mpaz_pro_6.data.enums

/**
 * Rol del usuario.
 *
 * Debe mapear 1:1 el enum `Rol` del backend: `UTP`, `DOCENTE` y `ESTUDIANTE`.
 * El valor que viaja al servidor es exactamente ese texto en español; el nombre de
 * la constante es en inglés.
 */
enum class UserRole(val wireValue: String) {
    UTP("UTP"),
    DOCENTE("DOCENTE"),
    ESTUDIANTE("ESTUDIANTE");

    companion object {
        /**
         * Convierte el valor del backend en su enum.
         *
         * Devuelve `null` si el valor no corresponde a ningún rol conocido, para que
         * el mapper pueda reportar el dato inesperado sin lanzar una excepción.
         */
        fun fromWire(value: String): UserRole? = entries.firstOrNull { it.wireValue == value }
    }
}
