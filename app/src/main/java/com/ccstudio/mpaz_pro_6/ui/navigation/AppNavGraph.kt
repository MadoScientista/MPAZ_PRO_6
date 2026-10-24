package com.ccstudio.mpaz_pro_6.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ccstudio.mpaz_pro_6.BuildConfig
import com.ccstudio.mpaz_pro_6.data.AppContainer
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.session.SessionState
import com.ccstudio.mpaz_pro_6.ui.screens.auth.LoginScreen
import com.ccstudio.mpaz_pro_6.ui.screens.auth.LoginViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.home.HomeTeacherScreen
import com.ccstudio.mpaz_pro_6.ui.screens.home.HomeTeacherViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.home.HomeUTPScreen
import com.ccstudio.mpaz_pro_6.ui.screens.home.HomeUTPViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitAuthorizationScreen
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitAuthorizationViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitDetailScreen
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitDetailViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitListScreen
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitListViewModel
import kotlinx.coroutines.launch

/**
 * Grafo de navegación de la app.
 *
 * Espera a que `Session` termine de leer el token persistido y elige el destino
 * inicial: login si no hay sesión, panel del rol si la hay.
 */
@Composable
fun AppNavGraph(container: AppContainer) {
    val sessionState by container.session.state.collectAsState()

    when (val state = sessionState) {
        SessionState.Loading -> LoadingScreen()

        SessionState.Unauthenticated -> {
            key(false) {
                AppNavHost(
                    container = container,
                    user = null
                )
            }
        }

        is SessionState.Authenticated -> {
            key(true) {
                AppNavHost(
                    container = container,
                    user = state.user
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun AppNavHost(
    container: AppContainer,
    user: User?
) {
    val navController = rememberNavController()
    val authenticated = user != null
    val rol = user?.rol ?: UserRole.UTP

    NavHost(
        navController = navController,
        startDestination = if (authenticated) AppRoutes.HOME else AppRoutes.LOGIN
    ) {

        composable(AppRoutes.LOGIN) {
            val viewModel: LoginViewModel = viewModel(
                factory = LoginViewModel.factory(container.authRepository)
            )
            val scope = rememberCoroutineScope()
            var isResetting by remember { mutableStateOf(false) }

            LoginScreen(
                username = viewModel.username,
                password = viewModel.password,
                isLoading = viewModel.isLoading,
                error = viewModel.error,
                onUsernameChange = viewModel::onUsernameChange,
                onPasswordChange = viewModel::onPasswordChange,
                onIniciarSesion = viewModel::iniciarSesion,
                onResetLocalData = if (BuildConfig.DEBUG) {
                    {
                        scope.launch {
                            isResetting = true
                            container.resetLocalData()
                            isResetting = false
                        }
                    }
                } else {
                    null
                },
                isResettingData = isResetting
            )
        }

        if (user != null) {
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

                        LaunchedEffect(Unit) {
                            viewModel.cargar()
                        }

                        HomeUTPScreen(
                            userName = user.nombre,
                            unidadesPendientes = viewModel.unidadesPendientes,
                            totalCursos = viewModel.totalCursos,
                            totalAsignaturas = viewModel.totalAsignaturas,
                            isLoading = viewModel.isLoading,
                            error = viewModel.error,
                            onVerUnidades = { navController.navigate(AppRoutes.UNITS) },
                            onCerrarSesion = container::logout,
                            onCargar = viewModel::cargar
                        )
                    }

                    UserRole.DOCENTE -> {
                        val viewModel: HomeTeacherViewModel = viewModel(
                            factory = HomeTeacherViewModel.factory(
                                userRepository = container.userRepository,
                                assignmentRepository = container.assignmentRepository,
                                unitRepository = container.unitRepository,
                                subjectRepository = container.subjectRepository,
                                activityRepository = container.activityRepository,
                                pedagogicalReportRepository = container.pedagogicalReportRepository,
                                teacherId = user.id
                            )
                        )

                        LaunchedEffect(Unit) {
                            viewModel.cargar()
                        }

                        HomeTeacherScreen(
                            userName = user.nombre,
                            cursos = viewModel.cursos,
                            unidades = viewModel.unidades,
                            asignaturas = viewModel.asignaturas,
                            actividades = viewModel.actividades,
                            informesPendientes = viewModel.informesPendientes,
                            isLoading = viewModel.isLoading,
                            error = viewModel.error,
                            onViewUnit = { unit ->
                                navController.navigate(AppRoutes.unitDetail(unit.id))
                            },
                            onVerUnidades = { navController.navigate(AppRoutes.UNITS) },
                            onCerrarSesion = container::logout,
                            onCargar = viewModel::cargar
                        )
                    }

                    UserRole.ESTUDIANTE -> {
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
                    unitId = unitId
                )
            )

            LaunchedEffect(Unit) {
                viewModel.cargar()
            }

            UnitDetailScreen(
                unidad = viewModel.unidad,
                asignatura = viewModel.asignatura,
                isLoading = viewModel.isLoading,
                error = viewModel.error,
                onAutorizar = {
                    navController.navigate(AppRoutes.unitAuthorization(unitId))
                },
                onRetry = viewModel::cargar
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

            LaunchedEffect(Unit) {
                viewModel.cargar()
            }

            UnitAuthorizationScreen(
                unidad = viewModel.unidad,
                isLoading = viewModel.isLoading,
                error = viewModel.error,
                mensaje = viewModel.mensaje,
                onAutorizar = {
                    viewModel.autorizar(user.id) {
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
