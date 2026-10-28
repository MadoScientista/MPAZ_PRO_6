package com.ccstudio.mpaz_pro_6.data.enums

/**
 * Origen de un contenido o de un recurso de contenido.
 *
 * Debe mapear 1:1 el enum `OrigenMaterial` del backend: `PROPIO`, `PUBLICO` y
 * `AUTORIZADO`. Describe si el material es propio, público o autorizado.
 */
enum class ContentOrigin(val wireValue: String, val label: String) {
    OWN("PROPIO", "Propio"),
    PUBLIC("PUBLICO", "Público"),
    AUTHORIZED("AUTORIZADO", "Autorizado");

    companion object {
        /**
         * Convierte el valor del backend en su enum.
         *
         * Devuelve `null` si el valor no corresponde a ningún origen conocido, para
         * que el mapper pueda reportar el dato inesperado sin lanzar una excepción.
         */
        fun fromWire(value: String): ContentOrigin? = entries.firstOrNull { it.wireValue == value }
    }
}
