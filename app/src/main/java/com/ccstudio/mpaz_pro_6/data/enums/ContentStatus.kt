package com.ccstudio.mpaz_pro_6.data.enums

/**
 * Estado de publicación de un material del backend.
 *
 * Debe mapear 1:1 el enum `EstadoContenido` del backend: `BORRADOR`, `EN_REVISION`,
 * `AUTORIZADO` y `PUBLICADO`. El backend usa el mismo enum para la unidad de
 * aprendizaje, el contenido y el recurso de contenido, por eso la app lo modeliza
 * como un único `ContentStatus`.
 *
 * El estado gobierna la visibilidad y la edición: `BORRADOR` solo lo edita la UTP,
 * `EN_REVISION` espera la revisión de la UTP, `AUTORIZADO` lo ven los docentes y
 * `PUBLICADO` lo ven los estudiantes y es de solo lectura.
 */
enum class ContentStatus(val wireValue: String) {
    DRAFT("BORRADOR"),
    IN_REVIEW("EN_REVISION"),
    AUTHORIZED("AUTORIZADO"),
    PUBLISHED("PUBLICADO");

    /**
     * Indica si el material está pendiente de revisión de la UTP.
     *
     * El backend no expone un listado de unidades pendientes de revisión, así que
     * la UTP las filtra desde el listado completo. La regla actual es que solo se
     * muestran las unidades en `EN_REVISION`.
     */
    fun esPendienteDeRevision(): Boolean = this == IN_REVIEW

    companion object {
        /**
         * Convierte el valor del backend en su enum.
         *
         * Devuelve `null` si el valor no corresponde a ningún estado conocido, para
         * que el mapper pueda reportar el dato inesperado sin lanzar una excepción.
         */
        fun fromWire(value: String): ContentStatus? = entries.firstOrNull { it.wireValue == value }
    }
}
