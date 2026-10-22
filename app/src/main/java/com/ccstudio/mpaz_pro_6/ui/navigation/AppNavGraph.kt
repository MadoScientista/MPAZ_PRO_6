package com.ccstudio.mpaz_pro_6.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ccstudio.mpaz_pro_6.data.AppContainer
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.ui.screens.auth.LoginScreen
import com.ccstudio.mpaz_pro_6.ui.screens.auth.LoginViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.home.HomeUTPScreen
import com.ccstudio.mpaz_pro_6.ui.screens.home.HomeUTPViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitAuthorizationScreen
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitAuthorizationViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitDetailScreen
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitDetailViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitListScreen
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitListViewModel

/**
 * Grafo de navegación de la app.
 *
 * Conecta las rutas de `AppRoutes` con las pantallas de `ui/screens/`, aplicando
 * el destino inicial según el rol del usuario: UTP, docente o estudiante.
 *
 * En este vertical slice la UTP tiene su panel propio y los otros dos roles
 * aterrizan en su listado de unidades, que ya filtra por rol.
 */
@Composable
fun AppNavGraph(container: AppContainer) {

    val navController = rememberNavController()

    // Usuario con sesión iniciada: decide el rol y el contenido de las pantallas.
    // Sin usuario no hay rol: las pantallas de rol no se dibujan y la app vuelve
    // al login. Si se cayera a un rol por defecto, al cerrar la sesión se vería el
    // panel de ese rol un instante antes de llegar al login.
    val usuario by container.session.user.collectAsState()
    val rol = usuario?.rol

    NavHost(
        navController = navController,
        startDestination = AppRoutes.LOGIN
    ) {

        composable(AppRoutes.LOGIN) {
            val viewModel: LoginViewModel = viewModel(
                factory = LoginViewModel.factory(container.userRepository)
            )

            LoginScreen(
                username = viewModel.username,
                password = viewModel.password,
                isLoading = viewModel.isLoading,
                error = viewModel.error,
                hint = CREDENCIALES_DEMO,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onIniciarSesion = {
                    viewModel.iniciarSesion { user ->
                        container.session.iniciar(user)
                        navController.navigate(AppRoutes.HOME) {
                            popUpTo(AppRoutes.LOGIN) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(AppRoutes.HOME) { entry ->
            val rolActual = rol

            when (rolActual) {
                // Sin sesión no hay rol que mostrar: se vuelve al login.
                null -> VolverAlLogin(navController)

                UserRole.UTP -> {
                    val viewModel: HomeUTPViewModel = viewModel(
                        factory = HomeUTPViewModel.factory(
                            unitRepository = container.unitRepository,
                            courseRepository = container.courseRepository,
                            subjectRepository = container.subjectRepository
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    val refreshHome by entry.savedStateHandle
                        .getStateFlow(REFRESH_UNITS_KEY, false)
                        .collectAsState()

                    LaunchedEffect(refreshHome) {
                        if (refreshHome) {
                            viewModel.cargar()
                            entry.savedStateHandle[REFRESH_UNITS_KEY] = false
                        }
                    }

                    HomeUTPScreen(
                        userName = usuario?.nombre ?: "",
                        unidadesPendientes = viewModel.unidadesPendientes,
                        totalCursos = viewModel.totalCursos,
                        totalAsignaturas = viewModel.totalAsignaturas,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error,
                        onVerUnidades = { navController.navigate(AppRoutes.UNITS) },
                        onCerrarSesion = { container.session.cerrar() },
                        onCargar = viewModel::cargar
                    )
                }

                UserRole.DOCENTE, UserRole.ESTUDIANTE -> {
                    UnitListDestination(
                        container = container,
                        navController = navController,
                        entry = entry,
                        rol = rolActual,
                        onViewDetail = { unit ->
                            navController.navigate(AppRoutes.unitDetail(unit.id))
                        },
                        onAuthorize = { unit ->
                            navController.navigate(AppRoutes.unitAuthorization(unit.id))
                        },
                        onCerrarSesion = { container.session.cerrar() }
                    )
                }
            }
        }

        composable(AppRoutes.UNITS) { entry ->
            val rolActual = rol

            if (rolActual == null) {
                VolverAlLogin(navController)
            } else {
                UnitListDestination(
                    container = container,
                    navController = navController,
                    entry = entry,
                    rol = rolActual,
                    onViewDetail = { unit ->
                        navController.navigate(AppRoutes.unitDetail(unit.id))
                    },
                    onAuthorize = { unit ->
                        navController.navigate(AppRoutes.unitAuthorization(unit.id))
                    }
                )
            }
        }

        composable(
            route = AppRoutes.UNIT_DETAIL,
            arguments = listOf(navArgument("unitId") { type = NavType.IntType })
        ) { entry ->
            val unitId = entry.arguments?.getInt("unitId") ?: 0
            val rolActual = rol

            if (rolActual == null) {
                VolverAlLogin(navController)
            } else {
                val viewModel: UnitDetailViewModel = viewModel(
                    factory = UnitDetailViewModel.factory(
                        unitRepository = container.unitRepository,
                        subjectRepository = container.subjectRepository,
                        learningObjectiveRepository = container.learningObjectiveRepository,
                        contentRepository = container.contentRepository,
                        contentResourceRepository = container.contentResourceRepository,
                        activityRepository = container.activityRepository,
                        unitId = unitId
                    )
                )

                LaunchedEffect(Unit) {
                    viewModel.cargar()
                }

                UnitDetailScreen(
                    unidad = viewModel.unidad,
                    asignatura = viewModel.asignatura,
                    objetivos = viewModel.objetivos,
                    contenidosPorObjetivo = viewModel.contenidosPorObjetivo,
                    recursosPorContenido = viewModel.recursosPorContenido,
                    actividades = viewModel.actividades,
                    rol = rolActual,
                    isLoading = viewModel.isLoading,
                    error = viewModel.error,
                    onAutorizar = {
                        navController.navigate(AppRoutes.unitAuthorization(unitId))
                    },
                    onRetry = viewModel::cargar,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = AppRoutes.UNIT_AUTHORIZATION,
            arguments = listOf(navArgument("unitId") { type = NavType.IntType })
        ) { entry ->
            val unitId = entry.arguments?.getInt("unitId") ?: 0
            val rolActual = rol

            if (rolActual == null) {
                VolverAlLogin(navController)
            } else {
                val viewModel: UnitAuthorizationViewModel = viewModel(
                    factory = UnitAuthorizationViewModel.factory(
                        unitRepository = container.unitRepository,
                        subjectRepository = container.subjectRepository,
                        unitId = unitId
                    )
                )

                LaunchedEffect(Unit) {
                    viewModel.cargar()
                }

                UnitAuthorizationScreen(
                    unidad = viewModel.unidad,
                    asignatura = viewModel.asignatura,
                    rol = rolActual,
                    isLoading = viewModel.isLoading,
                    error = viewModel.error,
                    mensaje = viewModel.mensaje,
                    onAutorizar = {
                        val autorizanteId = usuario?.id ?: return@UnitAuthorizationScreen

                        viewModel.autorizar(autorizanteId) {
                            // Avisa al listado de atrás que tiene que recargar.
                            navController.marcarListadoParaRecarga(rolActual)
                        }
                    },
                    onRechazar = {
                        viewModel.rechazar {
                            navController.marcarListadoParaRecarga(rolActual)
                        }
                    },
                    onRetry = viewModel::cargar,
                    onNavigateBack = { navController.popBackStack() },
                    onVolverAlListado = {
                        navController.marcarListadoParaRecarga(rolActual)
                        navController.popBackStack(rutaListado(rolActual), inclusive = false)
                    }
                )
            }
        }
    }
}

/**
 * Destino sin sesión: vuelve al login.
 *
 * Al cerrar la sesión el usuario desaparece y ya no hay rol que mostrar. En vez de
 * caer en un rol por defecto (y dibujar su panel un instante antes de llegar al
 * login), la pantalla queda vacía y se regresa al login.
 */
@Composable
private fun VolverAlLogin(navController: NavHostController) {
    LaunchedEffect(Unit) {
        // Se limpia la pila (HOME es siempre el destino de abajo tras iniciar
        // sesión) para que el login no tenga detrás una pantalla sin sesión.
        navController.navigate(AppRoutes.LOGIN) {
            popUpTo(AppRoutes.HOME) { inclusive = true }
        }
    }
}

/**
 * Destino del listado de unidades.
 *
 * Recarga los datos cuando la pantalla de autorización le avisa que una unidad
 * cambió de estado.
 */
@Composable
private fun UnitListDestination(
    container: AppContainer,
    navController: NavHostController,
    entry: NavBackStackEntry,
    rol: UserRole,
    onViewDetail: (LearningUnit) -> Unit,
    onAuthorize: (LearningUnit) -> Unit,
    onCerrarSesion: (() -> Unit)? = null
) {
    val viewModel: UnitListViewModel = viewModel(
        factory = UnitListViewModel.factory(
            unitRepository = container.unitRepository,
            subjectRepository = container.subjectRepository,
            rol = rol
        )
    )

    val refresh by entry.savedStateHandle
        .getStateFlow(REFRESH_UNITS_KEY, false)
        .collectAsState()

    LaunchedEffect(refresh) {
        if (refresh) {
            viewModel.cargar()
            entry.savedStateHandle[REFRESH_UNITS_KEY] = false
        }
    }

    LaunchedEffect(Unit) {
        viewModel.cargar()
    }

    UnitListScreen(
        unidades = viewModel.unidades,
        asignaturas = viewModel.asignaturas,
        rol = rol,
        isLoading = viewModel.isLoading,
        error = viewModel.error,
        onViewDetail = { unit -> onViewDetail(unit) },
        onAuthorize = { unit -> onAuthorize(unit) },
        onRetry = viewModel::cargar,
        onCerrarSesion = onCerrarSesion
    )
}

/**
 * Ruta donde vive el listado de unidades según el rol.
 *
 * La UTP llega al listado desde su panel de inicio; los otros dos roles aterrizan
 * directamente en el listado, que es su pantalla de inicio.
 */
private fun rutaListado(rol: UserRole): String =
    if (rol == UserRole.UTP) AppRoutes.UNITS else AppRoutes.HOME

/**
 * Marca el listado de unidades para que recargue sus datos.
 *
 * El listado puede estar dos pantallas más abajo en la pila (listado → detalle →
 * autorización), así que se busca su entrada en la pila y no solo en la anterior.
 * Si el listado no está en la pila, no hay nada que marcar.
 */
private fun NavHostController.marcarListadoParaRecarga(rol: UserRole) {
    runCatching { getBackStackEntry(rutaListado(rol)) }
        .onSuccess { entry -> entry.savedStateHandle[REFRESH_UNITS_KEY] = true }
}

/** Clave con la que el listado de unidades recibe el aviso de recarga. */
private const val REFRESH_UNITS_KEY = "refrescarUnidades"

/**
 * Usuarios de prueba de la semilla en memoria.
 *
 * Se muestran en la pantalla de inicio de sesión porque la sesión se resuelve en
 * el dispositivo mientras el backend no exponga un endpoint de autenticación.
 */
private val CREDENCIALES_DEMO =
    "Usuarios de prueba: elena / utp123 (UTP), marco / doc123 (docente), camila / est123 (estudiante)"
