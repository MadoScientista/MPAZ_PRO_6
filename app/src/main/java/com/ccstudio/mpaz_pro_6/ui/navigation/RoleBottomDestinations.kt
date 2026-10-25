package com.ccstudio.mpaz_pro_6.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.ui.components.common.BottomDestination

/**
 * Destinos de la barra inferior según el rol.
 *
 * Solo incluye pantallas de nivel superior que ya existen como destino del
 * `NavHost`. Los detalles, formularios y la resolución de intentos quedan fuera
 * de la barra y se alcanzan desde su listado.
 *
 * Este primer piloto habilita la barra solo para el docente. El UTP y el
 * estudiante la reciben cuando sus secciones de nivel superior dejen de ser
 * esqueletos: hoy el estudiante aterrizaría en el mismo listado desde Inicio y
 * desde Unidades.
 */
fun bottomDestinationsFor(role: UserRole): List<BottomDestination> = when (role) {
    UserRole.UTP -> emptyList()

    UserRole.DOCENTE -> listOf(
        BottomDestination(
            route = AppRoutes.HOME,
            label = "Inicio",
            icon = Icons.Filled.Home
        ),
        BottomDestination(
            route = AppRoutes.UNITS,
            label = "Unidades",
            icon = Icons.AutoMirrored.Filled.MenuBook
        ),
        BottomDestination(
            route = AppRoutes.ASSIGNMENTS,
            label = "Asignaciones",
            icon = Icons.AutoMirrored.Filled.Assignment
        ),
        BottomDestination(
            route = AppRoutes.REPORTS,
            label = "Informes",
            icon = Icons.Filled.Assessment
        )
    )

    UserRole.ESTUDIANTE -> emptyList()
}
