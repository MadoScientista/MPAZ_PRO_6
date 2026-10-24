package com.ccstudio.mpaz_pro_6.data.preferences

/**
 * Modo de tema elegido por el usuario.
 *
 * `SYSTEM` sigue la preferencia del teléfono; `LIGHT` y `DARK` son elecciones
 * explícitas que persisten entre sesiones. La UI siempre recibe el booleano ya
 * resuelto con [resolveDarkTheme].
 */
enum class ThemeMode {

    /** Sigue el modo claro/oscuro del sistema. */
    SYSTEM,

    /** Fuerza el tema claro. */
    LIGHT,

    /** Fuerza el tema oscuro. */
    DARK;

    /** Indica si el tema debe ser oscuro, resolviendo el modo del sistema. */
    fun resolveDarkTheme(systemDark: Boolean): Boolean = when (this) {
        SYSTEM -> systemDark
        LIGHT -> false
        DARK -> true
    }

    /**
     * Devuelve el modo explícito opuesto al tema que se está viendo.
     *
     * Se usa al tocar el botón cuando el modo actual es `SYSTEM`: la primera
     * pulsación fija una elección explícita.
     */
    fun toggled(currentlyDark: Boolean): ThemeMode =
        if (currentlyDark) LIGHT else DARK

    companion object {

        /** Convierte el valor persistido; cualquier valor desconocido es `SYSTEM`. */
        fun fromStored(value: String?): ThemeMode =
            entries.firstOrNull { it.name == value } ?: SYSTEM
    }
}
