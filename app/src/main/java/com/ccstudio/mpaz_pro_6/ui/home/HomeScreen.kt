package com.ccstudio.mpaz_pro_6.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ccstudio.mpaz_pro_6.ui.navigation.NavDestination
import com.ccstudio.mpaz_pro_6.ui.screens.GenericSectionScreen
import com.ccstudio.mpaz_pro_6.ui.screens.InicioScreen
import com.ccstudio.mpaz_pro_6.ui.screens.RestrictedAccessScreen
import com.ccstudio.mpaz_pro_6.ui.utp.ActividadVistaPreviaScreen
import com.ccstudio.mpaz_pro_6.ui.utp.ContenidoDetalleScreen
import com.ccstudio.mpaz_pro_6.ui.utp.CrearUsuarioScreen
import com.ccstudio.mpaz_pro_6.ui.utp.CursoAsignaturaDetalleScreen
import com.ccstudio.mpaz_pro_6.ui.utp.CursoDetalleScreen
import com.ccstudio.mpaz_pro_6.ui.utp.CursosListScreen
import com.ccstudio.mpaz_pro_6.ui.utp.DocenteDetalleScreen
import com.ccstudio.mpaz_pro_6.ui.utp.DocentesListScreen
import com.ccstudio.mpaz_pro_6.ui.utp.RevisionesPendientesScreen
import com.ccstudio.mpaz_pro_6.ui.utp.UnidadDetalleScreen
import com.ccstudio.mpaz_pro_6.ui.utp.UsuariosListScreen
import com.ccstudio.mpaz_pro_6.ui.utp.UtpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    rol: String,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navItems = remember(rol) { NavDestination.getNavItemsForRole(rol) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavDestination.Inicio.route

    val isRouteAllowed = remember(currentRoute, rol) {
        NavDestination.isRouteAllowedForRole(currentRoute, rol)
    }

    val isTopLevelDestination = NavDestination.isTopLevelRoute(currentRoute)

    val currentTitle = if (isRouteAllowed) {
        navItems.find { it.route == currentRoute }?.title
            ?: when {
                currentRoute == NavDestination.Inicio.route -> NavDestination.Inicio.title
                currentRoute == NavDestination.RevisionesPendientes.route -> NavDestination.RevisionesPendientes.title
                currentRoute == NavDestination.Cursos.route -> NavDestination.Cursos.title
                currentRoute == NavDestination.Docentes.route -> NavDestination.Docentes.title
                currentRoute == NavDestination.Usuarios.route -> NavDestination.Usuarios.title
                currentRoute == NavDestination.Asignaturas.route -> NavDestination.Asignaturas.title
                currentRoute == NavDestination.Crear.route -> NavDestination.Crear.title
                currentRoute == NavDestination.Informes.route -> NavDestination.Informes.title
                currentRoute == NavDestination.ActividadesPendientes.route -> NavDestination.ActividadesPendientes.title
                currentRoute.startsWith("unidad_detalle/") -> NavDestination.DetalleUnidad.title
                currentRoute.startsWith("contenido_detalle/") -> NavDestination.DetalleContenido.title
                currentRoute.startsWith("actividad_vistaprevia/") -> NavDestination.VistaPreviaActividad.title
                currentRoute.startsWith("curso_detalle/") -> NavDestination.DetalleCurso.title
                currentRoute.startsWith("curso_asignatura_detalle/") -> NavDestination.DetalleAsignaturaCurso.title
                currentRoute.startsWith("docente_detalle/") -> NavDestination.DetalleDocente.title
                currentRoute == NavDestination.CrearUsuario.route -> NavDestination.CrearUsuario.title
                else -> "MPAZ PRO"
            }
    } else {
        "Acceso Restringido"
    }

    val utpViewModel: UtpViewModel = viewModel()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentTitle,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    if (!isTopLevelDestination) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
            )
        },
        bottomBar = {
            if (isTopLevelDestination && navItems.isNotEmpty()) {
                NavigationBar {
                    navItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            icon = {
                                item.icon?.let { iconVector ->
                                    Icon(
                                        imageVector = iconVector,
                                        contentDescription = item.title,
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavDestination.Inicio.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(NavDestination.Inicio.route) {
                InicioScreen(
                    rol = rol,
                    onLogoutClick = onLogoutClick,
                )
            }
            composable(NavDestination.RevisionesPendientes.route) {
                ProtectedScreen(
                    route = NavDestination.RevisionesPendientes.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    RevisionesPendientesScreen(
                        viewModel = utpViewModel,
                        onUnitClick = { unitId ->
                            navController.navigate(NavDestination.DetalleUnidad.createRoute(unitId))
                        },
                    )
                }
            }
            composable(
                route = NavDestination.DetalleUnidad.route,
                arguments = listOf(navArgument("unidadId") { type = NavType.LongType }),
            ) { backStackEntry ->
                val unidadId = backStackEntry.arguments?.getLong("unidadId") ?: 0L
                ProtectedScreen(
                    route = NavDestination.DetalleUnidad.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    UnidadDetalleScreen(
                        unidadId = unidadId,
                        viewModel = utpViewModel,
                        onContentClick = { contentId ->
                            navController.navigate(NavDestination.DetalleContenido.createRoute(contentId))
                        },
                        onPruebaFinalClick = { actividadId ->
                            navController.navigate(NavDestination.VistaPreviaActividad.createRoute(actividadId))
                        },
                    )
                }
            }
            composable(
                route = NavDestination.DetalleContenido.route,
                arguments = listOf(navArgument("contenidoId") { type = NavType.LongType }),
            ) { backStackEntry ->
                val contenidoId = backStackEntry.arguments?.getLong("contenidoId") ?: 0L
                ProtectedScreen(
                    route = NavDestination.DetalleContenido.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    ContenidoDetalleScreen(
                        contenidoId = contenidoId,
                        viewModel = utpViewModel,
                        onActivityClick = { actividadId ->
                            navController.navigate(NavDestination.VistaPreviaActividad.createRoute(actividadId))
                        },
                    )
                }
            }
            composable(
                route = NavDestination.VistaPreviaActividad.route,
                arguments = listOf(navArgument("actividadId") { type = NavType.LongType }),
            ) { backStackEntry ->
                val actividadId = backStackEntry.arguments?.getLong("actividadId") ?: 0L
                ProtectedScreen(
                    route = NavDestination.VistaPreviaActividad.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    ActividadVistaPreviaScreen(
                        actividadId = actividadId,
                        viewModel = utpViewModel,
                    )
                }
            }
            composable(NavDestination.Cursos.route) {
                ProtectedScreen(
                    route = NavDestination.Cursos.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    CursosListScreen(
                        viewModel = utpViewModel,
                        onCourseClick = { cursoId ->
                            navController.navigate(NavDestination.DetalleCurso.createRoute(cursoId))
                        },
                    )
                }
            }
            composable(
                route = NavDestination.DetalleCurso.route,
                arguments = listOf(navArgument("cursoId") { type = NavType.LongType }),
            ) { backStackEntry ->
                val cursoId = backStackEntry.arguments?.getLong("cursoId") ?: 0L
                ProtectedScreen(
                    route = NavDestination.DetalleCurso.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    CursoDetalleScreen(
                        cursoId = cursoId,
                        viewModel = utpViewModel,
                        onAsignaturaClick = { asignaturaId ->
                            navController.navigate(NavDestination.DetalleAsignaturaCurso.createRoute(cursoId, asignaturaId))
                        },
                    )
                }
            }
            composable(
                route = NavDestination.DetalleAsignaturaCurso.route,
                arguments = listOf(
                    navArgument("cursoId") { type = NavType.LongType },
                    navArgument("asignaturaId") { type = NavType.LongType },
                ),
            ) { backStackEntry ->
                val cursoId = backStackEntry.arguments?.getLong("cursoId") ?: 0L
                val asignaturaId = backStackEntry.arguments?.getLong("asignaturaId") ?: 0L
                ProtectedScreen(
                    route = NavDestination.DetalleAsignaturaCurso.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    CursoAsignaturaDetalleScreen(
                        cursoId = cursoId,
                        asignaturaId = asignaturaId,
                        viewModel = utpViewModel,
                        onUnidadClick = { unidadId ->
                            navController.navigate(NavDestination.DetalleUnidad.createRoute(unidadId))
                        },
                    )
                }
            }
            composable(NavDestination.Docentes.route) {
                ProtectedScreen(
                    route = NavDestination.Docentes.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    DocentesListScreen(
                        viewModel = utpViewModel,
                        onDocenteClick = { docenteId ->
                            navController.navigate(NavDestination.DetalleDocente.createRoute(docenteId))
                        },
                    )
                }
            }
            composable(
                route = NavDestination.DetalleDocente.route,
                arguments = listOf(navArgument("docenteId") { type = NavType.LongType }),
            ) { backStackEntry ->
                val docenteId = backStackEntry.arguments?.getLong("docenteId") ?: 0L
                ProtectedScreen(
                    route = NavDestination.DetalleDocente.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    DocenteDetalleScreen(
                        docenteId = docenteId,
                        viewModel = utpViewModel,
                    )
                }
            }
            composable(NavDestination.Usuarios.route) {
                ProtectedScreen(
                    route = NavDestination.Usuarios.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    UsuariosListScreen(
                        viewModel = utpViewModel,
                        onCreateUserClick = {
                            navController.navigate(NavDestination.CrearUsuario.route)
                        },
                    )
                }
            }
            composable(NavDestination.CrearUsuario.route) {
                ProtectedScreen(
                    route = NavDestination.CrearUsuario.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    CrearUsuarioScreen(
                        viewModel = utpViewModel,
                        onUserCreated = {
                            navController.popBackStack()
                        },
                        onCancel = {
                            navController.popBackStack()
                        },
                    )
                }
            }
            composable(NavDestination.Asignaturas.route) {
                ProtectedScreen(
                    route = NavDestination.Asignaturas.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    GenericSectionScreen(title = NavDestination.Asignaturas.title)
                }
            }
            composable(NavDestination.Crear.route) {
                ProtectedScreen(
                    route = NavDestination.Crear.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    GenericSectionScreen(title = NavDestination.Crear.title)
                }
            }
            composable(NavDestination.Informes.route) {
                ProtectedScreen(
                    route = NavDestination.Informes.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    GenericSectionScreen(title = NavDestination.Informes.title)
                }
            }
            composable(NavDestination.ActividadesPendientes.route) {
                ProtectedScreen(
                    route = NavDestination.ActividadesPendientes.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    GenericSectionScreen(title = NavDestination.ActividadesPendientes.title)
                }
            }
        }
    }
}

@Composable
private fun ProtectedScreen(
    route: String,
    rol: String,
    onGoHome: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (NavDestination.isRouteAllowedForRole(route, rol)) {
        content()
    } else {
        RestrictedAccessScreen(rol = rol, onGoHome = onGoHome)
    }
}
