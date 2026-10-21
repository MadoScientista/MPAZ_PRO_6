package com.ccstudio.mpaz_pro_6.data.enums

/**
 * Estado de un informe pedagógico.
 *
 * Debe mapear 1:1 el enum `EstadoInforme` del backend: `GENERADO`,
 * `PENDIENTE_VALIDACION` y `VALIDADO`. El backend genera el informe en
 * `PENDIENTE_VALIDACION` y lo mueve a `VALIDADO` al validarlo.
 */
enum class ReportStatus(val wireValue: String) {
    GENERATED("GENERADO"),
    PENDING_VALIDATION("PENDIENTE_VALIDACION"),
    VALIDATED("VALIDADO");

    companion object {
        /**
         * Convierte el valor del backend en su enum.
         *
         * Devuelve `null` si el valor no corresponde a ningún estado conocido, para
         * que el mapper pueda reportar el dato inesperado sin lanzar una excepción.
         */
        fun fromWire(value: String): ReportStatus? = entries.firstOrNull { it.wireValue == value }
    }
}
