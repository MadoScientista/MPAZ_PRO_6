package com.ccstudio.mpaz_pro_6.data.enums

/**
 * Tipo de un recurso de contenido.
 *
 * Debe mapear 1:1 el enum `TipoRecurso` del backend: `IMAGEN` y `AUDIO`.
 */
enum class ContentResourceType(val wireValue: String, val label: String) {
    IMAGE("IMAGEN", "Imagen"),
    AUDIO("AUDIO", "Audio");

    companion object {
        /**
         * Convierte el valor del backend en su enum.
         *
         * Devuelve `null` si el valor no corresponde a ningún tipo conocido, para que
         * el mapper pueda reportar el dato inesperado sin lanzar una excepción.
         */
        fun fromWire(value: String): ContentResourceType? = entries.firstOrNull { it.wireValue == value }
    }
}
