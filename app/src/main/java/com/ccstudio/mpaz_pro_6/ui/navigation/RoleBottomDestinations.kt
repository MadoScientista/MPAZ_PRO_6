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
 * Solo incluye pantallas de nivel superior que existen como destino del
 * `NavHost`. Los detalles, formularios, la resolución de intentos y el detalle de
 * contenido quedan fuera de la barra y se alcanzan desde su listado.
 *
 * - UTP: Inicio, Unidades, Asignaturas, Cursos y Usuarios.
 * - Docente: Inicio, Unidades, Cursos, Asignaciones e Informes.
 * - Estudiante: Inicio, Unidades, Actividades e Informes.
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
            route = AppRoutes.COURSES,
            label = "Cursos",
            icon = Icons.Filled.School
        ),
        BottomDestination(
            route = AppRoutes.ASSIGNMENTS,
            label = "Asignación",
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
