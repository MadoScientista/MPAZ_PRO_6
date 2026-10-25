package com.ccstudio.mpaz_pro_6.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.ui.components.common.BottomDestination

/**
 * Destinos de la barra inferior según el rol.
 *
 * Solo incluye pantallas de nivel superior que ya existen como destino del
 * `NavHost`. Los detalles, formularios y la resolución de intentos quedan fuera
 * de la barra y se alcanzan desde su listado.
 *
 * El docente tiene la barra completa del piloto. El UTP arranca con Inicio y
 * Unidades, que ya están implementados; Asignaturas, Cursos y Usuarios se suman
 * en las etapas siguientes. El estudiante todavía no la tiene: su Inicio aún es
 * un esqueleto y hoy `HOME` renderiza el mismo listado que `UNITS`.
 */
fun bottomDestinationsFor(role: UserRole): List<BottomDestination> = when (role) {
    UserRole.UTP -> listOf(
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
            route = AppRoutes.SUBJECTS,
            label = "Asignaturas",
            icon = Icons.Filled.Category
        ),
        BottomDestination(
            route = AppRoutes.COURSES,
            label = "Cursos",
            icon = Icons.Filled.School
        ),
        BottomDestination(
            route = AppRoutes.USERS,
            label = "Usuarios",
            icon = Icons.Filled.People
        )
    )

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

    UserRole.ESTUDIANTE -> listOf(
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
            route = AppRoutes.ACTIVITIES,
            label = "Actividades",
            icon = Icons.Filled.Checklist
        ),
        BottomDestination(
            route = AppRoutes.REPORTS,
            label = "Informes",
            icon = Icons.Filled.Assessment
        )
    )
}
