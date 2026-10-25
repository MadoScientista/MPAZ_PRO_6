package com.ccstudio.mpaz_pro_6.ui.navigation

/**
 * Rutas de navegación de la app.
 *
 * Los destinos del `NavHost`, agrupados por rol (`UserRole`) y con los
 * parámetros que cada endpoint necesita: id de unidad, id de actividad, id de
 * asignación, id de intento e id de informe.
 *
 * Las rutas son constantes y ningún valor literal queda incrustado en las
 * pantallas.
 */
object AppRoutes {

    /** Inicio de sesión. */
    const val LOGIN = "login"

    /** Panel de inicio según el rol del usuario. */
    const val HOME = "home"

    /** Listado de unidades. */
    const val UNITS = "units"

    /** Detalle de una unidad. */
    const val UNIT_DETAIL = "unitDetail/{unitId}"

    /** Autorización y publicación de una unidad. */
    const val UNIT_AUTHORIZATION = "unitAuthorization/{unitId}"

    /** Lista de asignaciones (docente). */
    const val ASSIGNMENTS = "assignments"

    /** Listado de informes pedagógicos (docente). */
    const val REPORTS = "reports"

    /** Construye la ruta del detalle de una unidad. */
    fun unitDetail(unitId: Int): String = "unitDetail/$unitId"

    /** Construye la ruta de autorización de una unidad. */
    fun unitAuthorization(unitId: Int): String = "unitAuthorization/$unitId"
}
