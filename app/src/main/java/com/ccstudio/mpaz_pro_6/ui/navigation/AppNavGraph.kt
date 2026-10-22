package com.ccstudio.mpaz_pro_6.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val usuario by container.session.user.collectAsState()
    val rol = usuario?.rol ?: UserRole.UTP

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

        composable(AppRoutes.HOME) {
            when (rol) {
                UserRole.UTP -> {
                    val viewModel: HomeUTPViewModel = viewModel(
                        factory = HomeUTPViewModel.factory(
                            unitRepository = container.unitRepository,
                            courseRepository = container.courseRepository,
                            subjectRepository = container.subjectRepository
                        )
                    )

                    HomeUTPScreen(
                        userName = usuario?.nombre ?: "",
                        unidadesPendientes = viewModel.unidadesPendientes,
                        totalCursos = viewModel.totalCursos,
                        totalAsignaturas = viewModel.totalAsignaturas,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error,
                        onVerUnidades = { navController.navigate(AppRoutes.UNITS) },
                        onCerrarSesion = {
                            container.session.cerrar()
                            navController.navigate(AppRoutes.LOGIN) {
                                popUpTo(AppRoutes.LOGIN) { inclusive = true }
                            }
                        },
                        onCargar = viewModel::cargar
                    )
                }

                UserRole.DOCENTE, UserRole.ESTUDIANTE -> {
                    UnitListDestination(
                        container = container,
                        navController = navController,
                        rol = rol,
                        onViewDetail = { unit ->
                            navController.navigate(AppRoutes.unitDetail(unit.id))
                        },
                        onAuthorize = { unit ->
                            navController.navigate(AppRoutes.unitAuthorization(unit.id))
                        }
                    )
                }
            }
        }

        composable(AppRoutes.UNITS) {
            UnitListDestination(
                container = container,
                navController = navController,
                rol = rol,
                onViewDetail = { unit ->
                    navController.navigate(AppRoutes.unitDetail(unit.id))
                },
                onAuthorize = { unit ->
                    navController.navigate(AppRoutes.unitAuthorization(unit.id))
                }
            )
        }

        composable(
            route = AppRoutes.UNIT_DETAIL,
            arguments = listOf(navArgument("unitId") { type = NavType.IntType })
        ) { entry ->
            val unitId = entry.arguments?.getInt("unitId") ?: 0

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
                isLoading = viewModel.isLoading,
                error = viewModel.error,
                onAutorizar = {
                    navController.navigate(AppRoutes.unitAuthorization(unitId))
                },
                onRetry = viewModel::cargar,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = AppRoutes.UNIT_AUTHORIZATION,
            arguments = listOf(navArgument("unitId") { type = NavType.IntType })
        ) { entry ->
            val unitId = entry.arguments?.getInt("unitId") ?: 0

            val viewModel: UnitAuthorizationViewModel = viewModel(
                factory = UnitAuthorizationViewModel.factory(
                    unitRepository = container.unitRepository,
                    unitId = unitId
                )
            )

            UnitAuthorizationScreen(
                unidad = viewModel.unidad,
                isLoading = viewModel.isLoading,
                error = viewModel.error,
                mensaje = viewModel.mensaje,
                onAutorizar = {
                    val autorizanteId = usuario?.id ?: return@UnitAuthorizationScreen

                    viewModel.autorizar(autorizanteId) {
                        // Avisa al listado de atrás que tiene que recargar.
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(REFRESH_UNITS_KEY, true)

                        navController.popBackStack()
                    }
                },
                onRetry = viewModel::cargar
            )
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
    rol: UserRole,
    onViewDetail: (LearningUnit) -> Unit,
    onAuthorize: (LearningUnit) -> Unit
) {
    val viewModel: UnitListViewModel = viewModel(
        factory = UnitListViewModel.factory(
            unitRepository = container.unitRepository,
            subjectRepository = container.subjectRepository,
            rol = rol
        )
    )

    val refresh by navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow(REFRESH_UNITS_KEY, false)
        ?.collectAsState()
        ?: remember { mutableStateOf(false) }

    LaunchedEffect(refresh) {
        if (refresh) {
            viewModel.cargar()
            navController.currentBackStackEntry?.savedStateHandle?.set(REFRESH_UNITS_KEY, false)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.cargar()
    }

    UnitListScreen(
        unidades = viewModel.unidades,
        asignaturas = viewModel.asignaturas,
        isLoading = viewModel.isLoading,
        error = viewModel.error,
        onViewDetail = { unit -> onViewDetail(unit) },
        onAuthorize = { unit -> onAuthorize(unit) },
        onRetry = viewModel::cargar
    )
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
