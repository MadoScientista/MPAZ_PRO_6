package com.ccstudio.mpaz_pro_6.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ccstudio.mpaz_pro_6.ui.docente.CrearContenidoScreen
import com.ccstudio.mpaz_pro_6.ui.docente.DocenteInformesScreen
import com.ccstudio.mpaz_pro_6.ui.estudiante.EstudianteActividadesScreen
import com.ccstudio.mpaz_pro_6.ui.estudiante.EstudianteResolverActividadScreen
import com.ccstudio.mpaz_pro_6.ui.navigation.NavDestination
import com.ccstudio.mpaz_pro_6.ui.navigation.detailEnter
import com.ccstudio.mpaz_pro_6.ui.navigation.detailExit
import com.ccstudio.mpaz_pro_6.ui.navigation.detailPopEnter
import com.ccstudio.mpaz_pro_6.ui.navigation.detailPopExit
import com.ccstudio.mpaz_pro_6.ui.navigation.tabEnter
import com.ccstudio.mpaz_pro_6.ui.navigation.tabExit
import com.ccstudio.mpaz_pro_6.ui.screens.AsignaturasScreen
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

/**
 * Contenedor principal de la aplicación autenticada.
 *
 * Administra la navegación centralizada mediante Jetpack Navigation Compose:
 * - Renderiza la cabecera dinámica [TopAppBar] según el destino activo.
 * - Gestiona la visibilidad condicional del menú inferior [NavigationBar] para destinos Top-Level.
 * - Provee la función `navigateToTopLevel` para garantizar que la navegación entre secciones principales
 *   preserve y limpie adecuadamente el estado de la pila de navegación (`popUpTo`, `saveState`, `restoreState`).
 * - Configura animaciones suaves de entrada/salida ([fadeIn], [fadeOut]) para evitar parpadeos con gestos
 *   o botones de retroceso nativos.
 *
 * @param rol Rol del usuario autenticado ("UTP", "DOCENTE", "ESTUDIANTE").
 * @param onOpenDrawer Callback para abrir el menú lateral.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    rol: String,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val navItems = remember(rol) { NavDestination.getNavItemsForRole(rol) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavDestination.Inicio.route

    val topLevelRoutes = remember(navItems) { navItems.map { it.route }.toSet() }

    fun isTabChange(from: NavBackStackEntry, to: NavBackStackEntry): Boolean =
        from.destination.route in topLevelRoutes &&
                to.destination.route in topLevelRoutes

    val utpViewModel: UtpViewModel = viewModel()

    val isRouteAllowed = remember(currentRoute, rol) {
        NavDestination.isRouteAllowedForRole(currentRoute, rol)
    }

    val isTopLevelDestination = NavDestination.isTopLevelRoute(currentRoute)

    val navigateToTopLevel: (String) -> Unit = remember(navController, currentRoute) {
        { targetRoute ->
            if (currentRoute != targetRoute) {
                navController.navigate(targetRoute) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

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
                currentRoute.startsWith("actividad_resolver/") -> NavDestination.ResolverActividad.title
                currentRoute.startsWith("curso_detalle/") -> NavDestination.DetalleCurso.title
                currentRoute.startsWith("curso_asignatura_detalle/") -> NavDestination.DetalleAsignaturaCurso.title
                currentRoute.startsWith("docente_detalle/") -> NavDestination.DetalleDocente.title
                currentRoute == NavDestination.CrearUsuario.route -> NavDestination.CrearUsuario.title
                else -> "MPAZ PRO"
            }
    } else {
        "Acceso Restringido"
    }

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
                    if (isTopLevelDestination) {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menú",
                            )
                        }
                    } else {
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
                    navigationIconContentColor = Color.White,
                ),
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = isTopLevelDestination && navItems.isNotEmpty(),
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
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
                                navigateToTopLevel(item.route)
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
            enterTransition = {
                if (isTabChange(initialState, targetState)) tabEnter() else detailEnter()
            },
            exitTransition = {
                if (isTabChange(initialState, targetState)) tabExit() else detailExit()
            },
            popEnterTransition = {
                if (isTabChange(initialState, targetState)) tabEnter() else detailPopEnter()
            },
            popExitTransition = {
                if (isTabChange(initialState, targetState)) tabExit() else detailPopExit()
            },
        ) {
            composable(NavDestination.Inicio.route) {
                InicioScreen(
                    rol = rol,
                    viewModel = utpViewModel,
                    onNavigateToRevisiones = {
                        if (rol.equals("ESTUDIANTE", ignoreCase = true)) {
                            navigateToTopLevel(NavDestination.ActividadesPendientes.route)
                        } else {
                            navigateToTopLevel(NavDestination.RevisionesPendientes.route)
                        }
                    },
                    onNavigateToCursos = {
                        if (rol.equals("ESTUDIANTE", ignoreCase = true)) {
                            navigateToTopLevel(NavDestination.Asignaturas.route)
                        } else {
                            navigateToTopLevel(NavDestination.Cursos.route)
                        }
                    },
                    onNavigateToDocentes = { navigateToTopLevel(NavDestination.Docentes.route) },
                    onNavigateToUsuarios = { navigateToTopLevel(NavDestination.Usuarios.route) },
                    onNavigateToCrearUsuario = { navController.navigate(NavDestination.CrearUsuario.route) },
                    onNavigateToDetalleUnidad = { unidadId ->
                        navController.navigate(NavDestination.DetalleUnidad.createRoute(unidadId))
                    },
                    onNavigateToAsignaturas = { navigateToTopLevel(NavDestination.Asignaturas.route) },
                    onNavigateToCrear = { navigateToTopLevel(NavDestination.Crear.route) },
                    onNavigateToInformes = { navigateToTopLevel(NavDestination.Informes.route) },
                    onNavigateToAsignaturaCurso = { cursoId, asignaturaId ->
                        navController.navigate(
                            NavDestination.DetalleAsignaturaCurso.createRoute(cursoId, asignaturaId)
                        )
                    },
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
                arguments = listOf(
                    navArgument("unidadId") { type = NavType.LongType },
                    navArgument("cursoId") { type = NavType.LongType; defaultValue = 0L },
                ),
            ) { backStackEntry ->
                val unidadId = backStackEntry.arguments?.getLong("unidadId") ?: 0L
                val cursoId = backStackEntry.arguments?.getLong("cursoId") ?: 0L
                ProtectedScreen(
                    route = NavDestination.DetalleUnidad.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    UnidadDetalleScreen(
                        unidadId = unidadId,
                        cursoId = cursoId,
                        viewModel = utpViewModel,
                        rol = rol,
                        onContentClick = { contentId ->
                            navController.navigate(NavDestination.DetalleContenido.createRoute(contentId, cursoId))
                        },
                        onPruebaFinalClick = { actividadId ->
                            if (rol.equals("ESTUDIANTE", ignoreCase = true)) {
                                navController.navigate(NavDestination.ResolverActividad.createRoute(actividadId, cursoId))
                            } else {
                                navController.navigate(NavDestination.VistaPreviaActividad.createRoute(actividadId, cursoId))
                            }
                        },
                    )
                }
            }
            composable(
                route = NavDestination.DetalleContenido.route,
                arguments = listOf(
                    navArgument("contenidoId") { type = NavType.LongType },
                    navArgument("cursoId") { type = NavType.LongType; defaultValue = 0L },
                ),
            ) { backStackEntry ->
                val contenidoId = backStackEntry.arguments?.getLong("contenidoId") ?: 0L
                val cursoId = backStackEntry.arguments?.getLong("cursoId") ?: 0L
                ProtectedScreen(
                    route = NavDestination.DetalleContenido.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    ContenidoDetalleScreen(
                        contenidoId = contenidoId,
                        cursoId = cursoId,
                        viewModel = utpViewModel,
                        rol = rol,
                        onActivityClick = { actividadId ->
                            if (rol.equals("ESTUDIANTE", ignoreCase = true)) {
                                navController.navigate(NavDestination.ResolverActividad.createRoute(actividadId, cursoId))
                            } else {
                                navController.navigate(NavDestination.VistaPreviaActividad.createRoute(actividadId, cursoId))
                            }
                        },
                    )
                }
            }
            composable(
                route = NavDestination.VistaPreviaActividad.route,
                arguments = listOf(
                    navArgument("actividadId") { type = NavType.LongType },
                    navArgument("cursoId") { type = NavType.LongType; defaultValue = 0L },
                ),
            ) { backStackEntry ->
                val actividadId = backStackEntry.arguments?.getLong("actividadId") ?: 0L
                val cursoId = backStackEntry.arguments?.getLong("cursoId") ?: 0L
                ProtectedScreen(
                    route = NavDestination.VistaPreviaActividad.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    ActividadVistaPreviaScreen(
                        actividadId = actividadId,
                        cursoId = cursoId,
                        viewModel = utpViewModel,
                        rol = rol,
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
                        rol = rol,
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
                        rol = rol,
                        onUnidadClick = { unidadId ->
                            navController.navigate(NavDestination.DetalleUnidad.createRoute(unidadId, cursoId))
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
                    AsignaturasScreen(
                        onAsignaturaClick = { cursoId, asignaturaId ->
                            navController.navigate(
                                NavDestination.DetalleAsignaturaCurso.createRoute(cursoId, asignaturaId),
                            )
                        },
                    )
                }
            }
            composable(NavDestination.Crear.route) {
                ProtectedScreen(
                    route = NavDestination.Crear.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    CrearContenidoScreen()
                }
            }
            composable(NavDestination.Informes.route) {
                ProtectedScreen(
                    route = NavDestination.Informes.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    if (rol.equals("DOCENTE", ignoreCase = true) || rol.equals("UTP", ignoreCase = true)) {
                        DocenteInformesScreen()
                    } else {
                        GenericSectionScreen(title = NavDestination.Informes.title)
                    }
                }
            }
            composable(NavDestination.ActividadesPendientes.route) {
                ProtectedScreen(
                    route = NavDestination.ActividadesPendientes.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    if (rol.equals("ESTUDIANTE", ignoreCase = true)) {
                        EstudianteActividadesScreen(
                            onIniciarIntento = { actId, cId, intentoId ->
                                navController.navigate(
                                    NavDestination.ResolverActividad.createRoute(actId, cId, intentoId)
                                )
                            }
                        )
                    } else {
                        GenericSectionScreen(title = NavDestination.ActividadesPendientes.title)
                    }
                }
            }
            composable(
                route = NavDestination.ResolverActividad.route,
                arguments = listOf(
                    navArgument("actividadId") { type = NavType.LongType },
                    navArgument("cursoId") { type = NavType.LongType; defaultValue = 0L },
                    navArgument("intentoId") { type = NavType.LongType; defaultValue = 0L },
                ),
            ) { backStackEntry ->
                val actividadId = backStackEntry.arguments?.getLong("actividadId") ?: 0L
                val cursoId = backStackEntry.arguments?.getLong("cursoId") ?: 0L
                val intentoId = backStackEntry.arguments?.getLong("intentoId") ?: 0L
                ProtectedScreen(
                    route = NavDestination.ResolverActividad.route,
                    rol = rol,
                    onGoHome = { navController.navigate(NavDestination.Inicio.route) },
                ) {
                    EstudianteResolverActividadScreen(
                        actividadId = actividadId,
                        cursoId = cursoId,
                        intentoId = intentoId,
                        onVolverAUnidad = { unidadId, cId ->
                            if (unidadId > 0L) {
                                navController.navigate(NavDestination.DetalleUnidad.createRoute(unidadId, cId)) {
                                    popUpTo(NavDestination.DetalleUnidad.route) { inclusive = false }
                                }
                            } else {
                                navController.popBackStack()
                            }
                        },
                    )
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
