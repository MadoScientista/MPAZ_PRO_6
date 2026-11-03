package com.ccstudio.mpaz_pro_6.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Task
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector,
) {
    data object Inicio : NavDestination("inicio", "Inicio", Icons.Default.Home)
    data object RevisionesPendientes : NavDestination("revisiones_pendientes", "Revisiones pendientes", Icons.AutoMirrored.Filled.FactCheck)
    data object Cursos : NavDestination("cursos", "Cursos", Icons.Default.School)
    data object Docentes : NavDestination("docentes", "Docentes", Icons.Default.Badge)
    data object Usuarios : NavDestination("usuarios", "Usuarios", Icons.Default.People)
    data object Asignaturas : NavDestination("asignaturas", "Asignaturas", Icons.Default.Book)
    data object Crear : NavDestination("crear", "Crear", Icons.Default.AddCircle)
    data object Informes : NavDestination("informes", "Informes", Icons.Default.Assessment)
    data object ActividadesPendientes : NavDestination("actividades_pendientes", "Actividades pendientes", Icons.Default.Task)

    companion object {
        fun getNavItemsForRole(rol: String): List<NavDestination> {
            return when (rol.uppercase()) {
                "UTP" -> listOf(
                    Inicio,
                    RevisionesPendientes,
                    Cursos,
                    Docentes,
                    Usuarios
                )
                "DOCENTE" -> listOf(
                    Inicio,
                    Cursos,
                    Asignaturas,
                    Crear,
                    Informes
                )
                "ESTUDIANTE" -> listOf(
                    Inicio,
                    Asignaturas,
                    ActividadesPendientes
                )
                else -> listOf(
                    Inicio
                )
            }
        }
    }
}
