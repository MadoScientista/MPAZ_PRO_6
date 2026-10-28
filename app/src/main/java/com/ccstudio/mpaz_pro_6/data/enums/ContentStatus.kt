package com.ccstudio.mpaz_pro_6.data.enums

/**
 * Estado de publicación de un material del backend.
 *
 * Debe mapear 1:1 el enum `EstadoContenido` del backend: `BORRADOR`, `EN_REVISION`,
 * `AUTORIZADO` y `PUBLICADO`. El backend usa el mismo enum para la unidad de
 * aprendizaje, el contenido y el recurso de contenido, por eso la app lo modeliza
 * como un único `ContentStatus`.
 *
 * El estado gobierna la visibilidad y la edición: `BORRADOR` es material de
 * trabajo del docente y solo ese rol lo ve, `EN_REVISION` espera la revisión de
 * la UTP, `AUTORIZADO` lo ven los docentes y `PUBLICADO` lo ven los estudiantes
 * y es de solo lectura.
 */
enum class ContentStatus(val wireValue: String, val label: String) {
    DRAFT("BORRADOR", "Borrador"),
    IN_REVIEW("EN_REVISION", "En revisión"),
    AUTHORIZED("AUTORIZADO", "Autorizado"),
    PUBLISHED("PUBLICADO", "Publicado");

    /**
     * Indica si el material todavía está en su etapa inicial de edición.
     *
     * Se usa para el selector de estado del formulario. La cola de revisión de
     * la UTP solo considera las unidades `EN_REVISION`; los `BORRADOR` son
     * material de trabajo del docente.
     */
    fun esPendienteDeRevision(): Boolean = this == DRAFT || this == IN_REVIEW

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
