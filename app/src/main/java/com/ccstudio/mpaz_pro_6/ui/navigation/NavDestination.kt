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
    val icon: ImageVector? = null,
    val isTopLevel: Boolean = true,
) {
    // --- DESTINOS PRINCIPALES (TOP-LEVEL) ---
    data object Inicio : NavDestination("inicio", "Inicio", Icons.Default.Home, isTopLevel = true)
    data object RevisionesPendientes : NavDestination("revisiones_pendientes", "Revisiones pendientes", Icons.AutoMirrored.Filled.FactCheck, isTopLevel = true)
    data object Cursos : NavDestination("cursos", "Cursos", Icons.Default.School, isTopLevel = true)
    data object Docentes : NavDestination("docentes", "Docentes", Icons.Default.Badge, isTopLevel = true)
    data object Usuarios : NavDestination("usuarios", "Usuarios", Icons.Default.People, isTopLevel = true)
    data object Asignaturas : NavDestination("asignaturas", "Asignaturas", Icons.Default.Book, isTopLevel = true)
    data object Crear : NavDestination("crear", "Crear", Icons.Default.AddCircle, isTopLevel = true)
    data object Informes : NavDestination("informes", "Informes", Icons.Default.Assessment, isTopLevel = true)
    data object ActividadesPendientes : NavDestination("actividades_pendientes", "Actividades pendientes", Icons.Default.Task, isTopLevel = true)

    // --- DESTINOS SECUNDARIOS / DETALLE (SUB-DESTINATIONS) ---
    data object DetalleUnidad : NavDestination("unidad_detalle/{unidadId}", "Detalle de Unidad", isTopLevel = false) {
        fun createRoute(unidadId: Long) = "unidad_detalle/$unidadId"
    }
    data object DetalleContenido : NavDestination("contenido_detalle/{contenidoId}", "Detalle de Contenido", isTopLevel = false) {
        fun createRoute(contenidoId: Long) = "contenido_detalle/$contenidoId"
    }
    data object VistaPreviaActividad : NavDestination("actividad_vistaprevia/{actividadId}", "Vista Previa de Actividad", isTopLevel = false) {
        fun createRoute(actividadId: Long) = "actividad_vistaprevia/$actividadId"
    }
    data object DetalleCurso : NavDestination("curso_detalle/{cursoId}", "Detalle del Curso", isTopLevel = false) {
        fun createRoute(cursoId: Long) = "curso_detalle/$cursoId"
    }
    data object DetalleAsignaturaCurso : NavDestination("curso_asignatura_detalle/{cursoId}/{asignaturaId}", "Detalle de Asignatura", isTopLevel = false) {
        fun createRoute(cursoId: Long, asignaturaId: Long) = "curso_asignatura_detalle/$cursoId/$asignaturaId"
    }
    data object DetalleDocente : NavDestination("docente_detalle/{docenteId}", "Detalle de Docente", isTopLevel = false) {
        fun createRoute(docenteId: Long) = "docente_detalle/$docenteId"
    }

    companion object {
        fun getNavItemsForRole(rol: String): List<NavDestination> {
            return when (rol.uppercase()) {
                "UTP" -> listOf(
                    Inicio,
                    RevisionesPendientes,
                    Cursos,
                    Docentes,
                    Usuarios,
                )
                "DOCENTE" -> listOf(
                    Inicio,
                    Cursos,
                    Asignaturas,
                    Crear,
                    Informes,
                )
                "ESTUDIANTE" -> listOf(
                    Inicio,
                    Asignaturas,
                    ActividadesPendientes,
                )
                else -> listOf(
                    Inicio,
                )
            }
        }

        fun isRouteAllowedForRole(route: String, rol: String): Boolean {
            val allowedTopRoutes = getNavItemsForRole(rol).map { it.route }
            if (allowedTopRoutes.contains(route)) return true

            // Sub-rutas permitidas por rol
            return when (rol.uppercase()) {
                "UTP", "DOCENTE" -> route.startsWith("unidad_detalle/") ||
                                    route.startsWith("contenido_detalle/") ||
                                    route.startsWith("actividad_vistaprevia/") ||
                                    route.startsWith("curso_detalle/") ||
                                    route.startsWith("curso_asignatura_detalle/") ||
                                    route.startsWith("docente_detalle/")
                "ESTUDIANTE" -> route.startsWith("contenido_detalle/") ||
                                route.startsWith("actividad_vistaprevia/")
                else -> false
            }
        }

        fun isTopLevelRoute(route: String): Boolean {
            return when (route) {
                Inicio.route,
                RevisionesPendientes.route,
                Cursos.route,
                Docentes.route,
                Usuarios.route,
                Asignaturas.route,
                Crear.route,
                Informes.route,
                ActividadesPendientes.route -> true
                else -> false
            }
        }
    }
}
