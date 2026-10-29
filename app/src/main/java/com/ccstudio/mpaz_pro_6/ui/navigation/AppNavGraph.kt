package com.ccstudio.mpaz_pro_6.ui.navigation

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ccstudio.mpaz_pro_6.data.AppContainer
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.session.SessionState
import com.ccstudio.mpaz_pro_6.ui.components.common.AppBottomBar
import com.ccstudio.mpaz_pro_6.ui.components.common.AppTopBar
import com.ccstudio.mpaz_pro_6.ui.components.common.ProfileHeader
import com.ccstudio.mpaz_pro_6.ui.components.common.ThemeToggleButton
import com.ccstudio.mpaz_pro_6.ui.screens.activities.ActivityFormScreen
import com.ccstudio.mpaz_pro_6.ui.screens.activities.ActivityFormViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.activities.StudentActivityListScreen
import com.ccstudio.mpaz_pro_6.ui.screens.activities.StudentActivityListViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.assignments.AssignmentDetailScreen
import com.ccstudio.mpaz_pro_6.ui.screens.assignments.AssignmentDetailViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.assignments.AssignmentFormScreen
import com.ccstudio.mpaz_pro_6.ui.screens.assignments.AssignmentFormViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.assignments.AssignmentListScreen
import com.ccstudio.mpaz_pro_6.ui.screens.assignments.AssignmentListViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.attempts.ActivityAttemptScreen
import com.ccstudio.mpaz_pro_6.ui.screens.attempts.ActivityAttemptViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.attempts.AttemptResultScreen
import com.ccstudio.mpaz_pro_6.ui.screens.attempts.AttemptResultViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.contents.ContentDetailScreen
import com.ccstudio.mpaz_pro_6.ui.screens.contents.ContentDetailViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.contents.ContentFormScreen
import com.ccstudio.mpaz_pro_6.ui.screens.contents.ContentFormViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.auth.LoginScreen
import com.ccstudio.mpaz_pro_6.ui.screens.auth.LoginViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.courses.CourseDetailScreen
import com.ccstudio.mpaz_pro_6.ui.screens.courses.CourseDetailViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.courses.CourseFormScreen
import com.ccstudio.mpaz_pro_6.ui.screens.courses.CourseFormViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.courses.CourseListScreen
import com.ccstudio.mpaz_pro_6.ui.screens.courses.CourseListViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.home.HomeStudentScreen
import com.ccstudio.mpaz_pro_6.ui.screens.home.HomeStudentViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.home.HomeTeacherScreen
import com.ccstudio.mpaz_pro_6.ui.screens.home.HomeTeacherViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.home.HomeUTPScreen
import com.ccstudio.mpaz_pro_6.ui.screens.home.HomeUTPViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.reports.PedagogicalReportDetailScreen
import com.ccstudio.mpaz_pro_6.ui.screens.reports.PedagogicalReportDetailViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.reports.PedagogicalReportListScreen
import com.ccstudio.mpaz_pro_6.ui.screens.reports.PedagogicalReportListViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.reports.StudentReportListScreen
import com.ccstudio.mpaz_pro_6.ui.screens.reports.StudentReportListViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.subjects.SubjectDetailScreen
import com.ccstudio.mpaz_pro_6.ui.screens.subjects.SubjectDetailViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.subjects.SubjectFormScreen
import com.ccstudio.mpaz_pro_6.ui.screens.subjects.SubjectFormViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.subjects.SubjectListScreen
import com.ccstudio.mpaz_pro_6.ui.screens.subjects.SubjectListViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.units.FinalTestSettingsScreen
import com.ccstudio.mpaz_pro_6.ui.screens.units.FinalTestSettingsViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitAuthorizationScreen
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitAuthorizationViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitDetailScreen
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitDetailViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitListScreen
import com.ccstudio.mpaz_pro_6.ui.screens.units.UnitListViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.users.UserFormScreen
import com.ccstudio.mpaz_pro_6.ui.screens.users.UserFormViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.users.UserListScreen
import com.ccstudio.mpaz_pro_6.ui.screens.users.UserListViewModel
import kotlinx.coroutines.launch
import androidx.core.net.toUri

/**
 * Grafo de navegación de la app.
 *
 * Espera a que `Session` termine de leer el token persistido y elige el destino
 * inicial: login si no hay sesión, panel del rol si la hay.
 */
@Composable
fun AppNavGraph(
    container: AppContainer,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val sessionState by container.session.state.collectAsState()

    when (val state = sessionState) {
        SessionState.Loading -> LoadingScreen()

        SessionState.Unauthenticated -> {
            key(false) {
                AppNavHost(
                    container = container,
                    user = null,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
                )
            }
        }

        is SessionState.Authenticated -> {
            key(true) {
                AppNavHost(
                    container = container,
                    user = state.user,
                    isDarkTheme = isDarkTheme,
                    onToggleTheme = onToggleTheme
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
    user: User?,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()
    val authenticated = user != null
    val rol = user?.rol ?: UserRole.UTP

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Destinos de la barra inferior para el rol actual. La barra se muestra en
    // los destinos de nivel superior y en los detalles hijos de una pestaña
    // (unidad, contenido e informe). Queda oculta en login, autorización,
    // formularios, intentos y resultado, que son pantallas con acciones propias.
    val bottomDestinations = remember(rol) { bottomDestinationsFor(rol) }
    val topLevelRoutes = remember(rol) { bottomDestinations.map { it.route }.toSet() }

    // Pestaña a la que pertenece el detalle vigente, solo si el rol la tiene.
    val parentTab = CHILD_ROUTE_PARENT_TAB[currentRoute]?.takeIf { it in topLevelRoutes }
    val showBottomBar = authenticated && (currentRoute in topLevelRoutes || parentTab != null)

    // En un detalle la barra marca su pestaña padre; en una pestaña, la vigente.
    val selectedRoute = parentTab ?: currentRoute

    // Indica si la transición ocurre entre dos destinos de la barra inferior.
    fun isTabChange(from: NavBackStackEntry, to: NavBackStackEntry): Boolean =
        from.destination.route in topLevelRoutes &&
                to.destination.route in topLevelRoutes

    // El login se dibuja a pantalla completa: el fondo llega hasta atrás de la
    // barra del sistema y el formulario queda centrado en el centro físico de la
    // pantalla. El `null` cubre el primer frame, antes de que el `NavHost`
    // resuelva la ruta, para no aplicar el padding y quitarlo después.
    val isFullBleed = currentRoute == AppRoutes.LOGIN ||
            (currentRoute == null && !authenticated)

    // Modo edición de los formularios: el id llega en los argumentos de la
    // entrada vigente. Los destinos sin esos argumentos devuelven el default.
    val editingSubject = (backStackEntry?.arguments?.getInt("subjectId") ?: -1) > 0
    val editingCourse = (backStackEntry?.arguments?.getInt("courseId") ?: -1) > 0
    val editingUser = (backStackEntry?.arguments?.getInt("userId") ?: -1) > 0

    // Vista previa de la actividad: la piden la UTP y el docente desde el
    // material de la unidad, porque ninguno de los dos registra intentos.
    val previewingActivity = backStackEntry?.arguments?.getBoolean("preview") == true

    // Título de la barra superior para los destinos profundos (detalles y
    // formularios). Las pestañas muestran la identidad del usuario en su lugar:
    // su contenido ya indica de qué pantalla se trata.
    val topBarTitle = when (currentRoute) {
        AppRoutes.UNITS -> "Unidades"
        AppRoutes.SUBJECTS -> "Asignaturas"
        AppRoutes.COURSES -> "Cursos"
        AppRoutes.USERS -> "Usuarios"
        AppRoutes.ACTIVITIES -> "Actividades"
        AppRoutes.ASSIGNMENTS -> "Mis asignaciones"
        AppRoutes.REPORTS -> if (rol == UserRole.ESTUDIANTE) {
            "Informes"
        } else {
            "Informes pedagógicos"
        }

        AppRoutes.UNIT_DETAIL -> "Detalle de la unidad"
        AppRoutes.COURSE_DETAIL -> "Curso"
        AppRoutes.SUBJECT_DETAIL -> "Asignatura"
        AppRoutes.CONTENT_DETAIL -> "Detalle del contenido"
        AppRoutes.CONTENT_FORM -> "Nuevo contenido"
        AppRoutes.ACTIVITY_FORM -> "Nueva actividad"
        AppRoutes.UNIT_AUTHORIZATION -> "Autorización de unidad"
        AppRoutes.SUBJECT_FORM -> if (editingSubject) {
            "Editar asignatura"
        } else {
            "Nueva asignatura"
        }

        AppRoutes.COURSE_FORM -> if (editingCourse) "Editar curso" else "Nuevo curso"
        AppRoutes.USER_FORM -> if (editingUser) "Editar usuario" else "Nuevo usuario"
        AppRoutes.ACTIVITY_ATTEMPT -> if (previewingActivity) {
            "Vista previa de la actividad"
        } else {
            "Resolver actividad"
        }
        AppRoutes.ATTEMPT_RESULT -> "Resultado del intento"
        AppRoutes.REPORT_DETAIL -> "Informe pedagógico"
        AppRoutes.ASSIGNMENT_DETAIL -> "Detalle de la asignación"
        AppRoutes.ASSIGNMENT_FORM -> "Editar asignación"
        AppRoutes.FINAL_TEST_SETTINGS -> "Configurar prueba final"
        else -> ""
    }

    // El login se dibuja a pantalla completa y sin barra superior.
    val showTopBar = authenticated &&
            currentRoute != null &&
            currentRoute != AppRoutes.LOGIN

    // Shell de la app: el `Scaffold` reserva los insets del sistema para que el
    // contenido no quede debajo de las barras. La superior la monta el propio
    // `Scaffold` (queda fija entre destinos) y la inferior se mantiene en las
    // pestañas y en sus detalles para que el área de contenido no cambie.
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Bottom),
        topBar = {
            if (showTopBar) {
                AppTopBar(
                    title = {
                        if (currentRoute in topLevelRoutes) {
                            ProfileHeader(
                                userName = user.nombre,
                                role = rol
                            )
                        } else {
                            Text(
                                text = topBarTitle,
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    onNavigateBack = if (currentRoute in ROUTES_WITH_BACK) {
                        { navController.popBackStack() }
                    } else {
                        null
                    },
                    actions = {
                        ThemeToggleButton(
                            isDarkTheme = isDarkTheme,
                            onToggleTheme = onToggleTheme
                        )

                        // El cierre de sesión solo aplica al panel de inicio.
                        if (currentRoute == AppRoutes.HOME) {
                            TextButton(
                                onClick = container::logout,
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = LocalContentColor.current
                                )
                            ) {
                                Text(text = "Salir")
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                AppBottomBar(
                    destinations = bottomDestinations,
                    selectedRoute = selectedRoute,
                    onSelect = { destination ->
                        if (destination.route == AppRoutes.HOME) {
                            // Home siempre vuelve a su raíz. Si se navegara con
                            // restoreState, Navigation restauraría la pila
                            // guardada de Home y podría reabrir el detalle que
                            // se abrió desde una card.
                            val popped = navController.popBackStack(
                                route = AppRoutes.HOME,
                                inclusive = false,
                                saveState = true
                            )

                            if (!popped && currentRoute != AppRoutes.HOME) {
                                navController.navigate(AppRoutes.HOME) {
                                    launchSingleTop = true
                                }
                            }
                        } else {
                            navController.navigate(destination.route) {
                                // Evita apilar tabs y conserva el estado de cada uno.
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (authenticated) AppRoutes.HOME else AppRoutes.LOGIN,
            modifier = if (isFullBleed) Modifier else Modifier.padding(innerPadding),
            // Pestañas (patrón "top level" de Material 3): la saliente se apaga y
            // la entrante aparece con fundido y una escala breve. Nunca en
            // horizontal: sugeriría que se puede deslizar entre pestañas.
            enterTransition = {
                if (isTabChange(initialState, targetState)) tabEnter() else detailEnter()
            },
            exitTransition = {
                if (isTabChange(initialState, targetState)) tabExit() else detailExit()
            },
            // Detalles, formularios e intentos ("forward and backward"): shared
            // axis Z, escala más fundido. Al volver, la escala se invierte.
            popEnterTransition = {
                if (isTabChange(initialState, targetState)) tabEnter() else detailPopEnter()
            },
            popExitTransition = {
                if (isTabChange(initialState, targetState)) tabExit() else detailPopExit()
            }
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
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onResetLocalData = {
                        scope.launch {
                            isResetting = true

                            try {
                                container.resetLocalData()
                            } catch (e: Exception) {
                                Log.e("ResetLocalData", "Error al restablecer datos locales", e)
                            } finally {
                                isResetting = false
                            }
                        }
                    }
               ,
                isResettingData = isResetting
            )
        }

            if (user != null) {
                composable(AppRoutes.HOME) { entry ->
                    when (rol) {
                        UserRole.UTP -> {
                            val viewModel: HomeUTPViewModel = viewModel(
                                factory = HomeUTPViewModel.factory(
                                    unitRepository = container.unitRepository,
                                    courseRepository = container.courseRepository,
                                    subjectRepository = container.subjectRepository,
                                    userRepository = container.userRepository
                                )
                            )

                            LaunchedEffect(Unit) {
                                viewModel.cargar()
                            }

                            HomeUTPScreen(
                                unidadesPendientes = viewModel.unidadesPendientes,
                                cursos = viewModel.cursos,
                                asignaturas = viewModel.asignaturas,
                                unidadesPorAsignatura = viewModel.unidadesPorAsignatura,
                                estudiantesPorCurso = viewModel.estudiantesPorCurso,
                                isLoading = viewModel.isLoading,
                                error = viewModel.error,
                                onViewUnit = { unit ->
                                    navController.navigate(AppRoutes.unitDetail(unit.id))
                                },
                                onViewSubject = { subject ->
                                    navController.navigate(AppRoutes.subjectDetail(subject.id))
                                },
                                onViewCourse = { course ->
                                    navController.navigate(AppRoutes.courseDetail(course.id))
                                },
                                onVerUnidades = { navController.navigate(AppRoutes.UNITS) },
                                onVerCursos = { navController.navigate(AppRoutes.COURSES) },
                                onVerAsignaturas = { navController.navigate(AppRoutes.SUBJECTS) },
                                onCargar = { viewModel.cargar() }
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

                            val refresh by entry.savedStateHandle
                                .getStateFlow(REFRESH_ASSIGNMENTS_KEY, false)
                                .collectAsState()

                            LaunchedEffect(refresh) {
                                if (refresh) {
                                    viewModel.cargar()
                                    entry.savedStateHandle
                                        .set(REFRESH_ASSIGNMENTS_KEY, false)
                                }
                            }

                            HomeTeacherScreen(
                                cursos = viewModel.cursos,
                                unidades = viewModel.unidades,
                                asignaturas = viewModel.asignaturas,
                                informesPendientes = viewModel.informesPendientes,
                                estudiantes = viewModel.estudiantes,
                                actividades = viewModel.actividades,
                                isLoading = viewModel.isLoading,
                                error = viewModel.error,
                                onViewUnit = { unit ->
                                    navController.navigate(AppRoutes.unitDetail(unit.id))
                                },
                                onViewCourse = { course ->
                                    navController.navigate(
                                        AppRoutes.courseDetail(course.id)
                                    )
                                },
                                onViewSubject = { subject ->
                                    navController.navigate(
                                        AppRoutes.subjectDetail(subject.id)
                                    )
                                },

                                onVerUnidades = { navController.navigate(AppRoutes.UNITS) },
                                onVerCursos = { navController.navigate(AppRoutes.COURSES) },
                                onVerAsignaturas = { navController.navigate(AppRoutes.SUBJECTS) },

                                onCargar = viewModel::cargar,
                            )
                        }

                        UserRole.ESTUDIANTE -> {
                            HomeStudentDestination(
                                container = container,
                                navController = navController,
                                user = user,
                                onVerActividades = {
                                    navController.navigate(AppRoutes.ACTIVITIES)
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
                        courseId = user.cursoId,
                        onViewDetail = { unit ->
                            navController.navigate(AppRoutes.unitDetail(unit.id))
                        },
                        onAuthorize = { unit ->
                            navController.navigate(AppRoutes.unitAuthorization(unit.id))
                        }
                    )
                }

                composable(
                    route = AppRoutes.ACTIVITY_ATTEMPT,
                    arguments = listOf(
                        navArgument("activityId") { type = NavType.IntType },
                        navArgument("preview") {
                            type = NavType.BoolType
                            defaultValue = false
                        }
                    )
                ) { entry ->
                    val activityId = entry.arguments?.getInt("activityId") ?: 0
                    val preview = entry.arguments?.getBoolean("preview") == true

                    val viewModel: ActivityAttemptViewModel = viewModel(
                        factory = ActivityAttemptViewModel.factory(
                            activityRepository = container.activityRepository,
                            questionRepository = container.questionRepository,
                            alternativeRepository = container.alternativeRepository,
                            attemptRepository = container.attemptRepository,
                            studentAnswerRepository = container.studentAnswerRepository,
                            studentId = user.id,
                            activityId = activityId,
                            previewMode = preview,
                            assignmentRepository = container.assignmentRepository,
                            courseId = user.cursoId
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    ActivityAttemptScreen(
                        activity = viewModel.activity,
                        questions = viewModel.questions,
                        alternativesByQuestion = viewModel.alternativesByQuestion,
                        attempt = viewModel.attempt,
                        selectedAlternatives = viewModel.selectedAlternatives,
                        registeredQuestionIds = viewModel.registeredQuestionIds,
                        answeredCount = viewModel.answeredCount,
                        allAnswered = viewModel.allAnswered,
                        isLoading = viewModel.isLoading,
                        isSaving = viewModel.isSaving,
                        error = viewModel.error,
                        mensaje = viewModel.mensaje,
                        onStart = viewModel::iniciarIntento,
                        onSelectAlternative = viewModel::seleccionar,
                        maxIntentos = viewModel.maxIntentos,
                        previousAttemptsCount = viewModel.previousAttemptsCount,
                        onComplete = {
                            viewModel.completar { attemptId ->
                                markActivityCompleted(navController)
                                navController.navigate(AppRoutes.attemptResult(attemptId)) {
                                    popUpTo(AppRoutes.ACTIVITY_ATTEMPT) {
                                        inclusive = true
                                    }
                                }
                            }
                        },
                        onViewResult = { attemptId ->
                            markActivityCompleted(navController)
                            navController.navigate(AppRoutes.attemptResult(attemptId)) {
                                popUpTo(AppRoutes.ACTIVITY_ATTEMPT) {
                                    inclusive = true
                                }
                            }
                        },
                        onRetry = viewModel::cargar,
                        onDismissMensaje = viewModel::limpiarMensaje,
                        previewMode = preview
                    )
                }

                composable(
                    route = AppRoutes.ATTEMPT_RESULT,
                    arguments = listOf(
                        navArgument("attemptId") { type = NavType.IntType }
                    )
                ) { entry ->
                    val attemptId = entry.arguments?.getInt("attemptId") ?: 0

                    val viewModel: AttemptResultViewModel = viewModel(
                        factory = AttemptResultViewModel.factory(
                            attemptRepository = container.attemptRepository,
                            studentAnswerRepository = container.studentAnswerRepository,
                            activityRepository = container.activityRepository,
                            questionRepository = container.questionRepository,
                            alternativeRepository = container.alternativeRepository,
                            attemptId = attemptId
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    AttemptResultScreen(
                        activity = viewModel.activity,
                        attempt = viewModel.attempt,
                        questions = viewModel.questions,
                        alternativesByQuestion = viewModel.alternativesByQuestion,
                        answers = viewModel.answers,
                        correctas = viewModel.correctas,
                        total = viewModel.total,
                        porcentaje = viewModel.porcentaje,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error,
                        onRetry = viewModel::cargar,
                    )
                }

                composable(AppRoutes.ACTIVITIES) { entry ->
                    val viewModel: StudentActivityListViewModel = viewModel(
                        factory = StudentActivityListViewModel.factory(
                            assignmentRepository = container.assignmentRepository,
                            unitRepository = container.unitRepository,
                            activityRepository = container.activityRepository,
                            attemptRepository = container.attemptRepository,
                            studentId = user.id,
                            courseId = user.cursoId
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    val refresh by entry.savedStateHandle
                        .getStateFlow(REFRESH_ACTIVITIES_KEY, false)
                        .collectAsState()

                    LaunchedEffect(refresh) {
                        if (refresh) {
                            viewModel.cargar()
                            entry.savedStateHandle.set(REFRESH_ACTIVITIES_KEY, false)
                        }
                    }

                    StudentActivityListScreen(
                        items = viewModel.items,
                        sinCurso = viewModel.sinCurso,
                        onOpenActivity = { activity ->
                            navController.navigate(AppRoutes.activityAttempt(activity.id))
                        },
                        onViewResult = { attemptId ->
                            navController.navigate(AppRoutes.attemptResult(attemptId))
                        },
                        onRetry = viewModel::cargar,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error
                    )
                }

                composable(
                    route = AppRoutes.SUBJECT_DETAIL,
                    arguments = listOf(
                        navArgument("subjectId") { type = NavType.IntType },
                        navArgument("courseId") {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    )
                ) { entry ->
                    val subjectId = entry.arguments?.getInt("subjectId") ?: 0
                    val courseIdArg = entry.arguments?.getInt("courseId") ?: -1
                    val courseId = if (courseIdArg > 0) courseIdArg else null

                    val viewModel: SubjectDetailViewModel = viewModel(
                        factory = SubjectDetailViewModel.factory(
                            subjectRepository = container.subjectRepository,
                            unitRepository = container.unitRepository,
                            courseRepository = container.courseRepository,
                            subjectId = subjectId,
                            courseId = courseId,
                            assignmentRepository = container.assignmentRepository
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    SubjectDetailScreen(
                        subject = viewModel.subject,
                        course = viewModel.course,
                        unidades = viewModel.unidades,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error,
                        onViewUnit = { unit ->
                            navController.navigate(AppRoutes.unitDetail(unit.id, courseId = viewModel.course?.id))
                        },
                        onAuthorize = if (rol == UserRole.UTP) {
                            { unit -> navController.navigate(AppRoutes.unitAuthorization(unit.id)) }
                        } else {
                            null
                        },
                        onRetry = viewModel::cargar
                    )
                }

                composable(AppRoutes.SUBJECTS) {
                    val viewModel: SubjectListViewModel = viewModel(
                        factory = SubjectListViewModel.factory(
                            subjectRepository = container.subjectRepository,
                            unitRepository = container.unitRepository,
                            assignmentRepository = container.assignmentRepository,
                            rol = rol,
                            courseId = user.cursoId
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    val headerTitle = when (rol) {
                        UserRole.ESTUDIANTE -> "Asignaturas de tu curso"
                        else -> "Asignaturas del currículo"
                    }

                    SubjectListScreen(
                        asignaturas = viewModel.asignaturas,
                        estadisticasPorAsignatura = viewModel.estadisticasPorAsignatura,
                        onCreate = {
                            navController.navigate(AppRoutes.subjectForm())
                        },
                        onViewDetail = { subject ->
                            if (rol == UserRole.ESTUDIANTE) {
                                user.cursoId?.let { courseId ->
                                    navController.navigate(
                                        AppRoutes.subjectDetail(subject.id, courseId = courseId)
                                    )
                                } ?: navController.navigate(AppRoutes.subjectDetail(subject.id))
                            } else {
                                navController.navigate(AppRoutes.subjectDetail(subject.id))
                            }
                        },
                        onEdit = { subject ->
                            navController.navigate(AppRoutes.subjectForm(subject.id))
                        },
                        onDelete = { subject ->
                            viewModel.eliminar(subject.id)
                        },
                        mensajeOperacion = viewModel.mensajeOperacion,
                        onDismissMensaje = viewModel::limpiarMensaje,
                        onRetry = viewModel::cargar,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error,
                        canCreate = rol == UserRole.UTP,
                        showCardActions = rol == UserRole.UTP,
                        showPendingChip = rol == UserRole.UTP,
                        headerTitle = headerTitle,
                        sinCurso = viewModel.sinCurso
                    )
                }

                composable(
                    route = AppRoutes.SUBJECT_FORM,
                    arguments = listOf(
                        navArgument("subjectId") {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    )
                ) { entry ->
                    val subjectId = entry.arguments
                        ?.getInt("subjectId")
                        ?.takeIf { it > 0 }

                    val viewModel: SubjectFormViewModel = viewModel(
                        factory = SubjectFormViewModel.factory(
                            subjectRepository = container.subjectRepository,
                            subjectId = subjectId
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    SubjectFormScreen(
                        nombre = viewModel.nombre,
                        isEditMode = viewModel.isEditMode,
                        isLoading = viewModel.isLoading,
                        isSaving = viewModel.isSaving,
                        error = viewModel.error,
                        onNombreChange = viewModel::onNombreChange,
                        onSubmit = {
                            viewModel.guardar {
                                navController.popBackStack()
                            }
                        },
                        onCancel = { navController.popBackStack() },
                    )
                }

                composable(AppRoutes.COURSES) {
                    val viewModel: CourseListViewModel = viewModel(
                        factory = CourseListViewModel.factory(
                            courseRepository = container.courseRepository,
                            userRepository = container.userRepository,
                            teacherId = user.id.takeIf { rol == UserRole.DOCENTE }
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    CourseListScreen(
                        cursos = viewModel.cursos,
                        aniosDisponibles = viewModel.aniosDisponibles,
                        anioSeleccionado = viewModel.anioSeleccionado,
                        estudiantesPorCurso = viewModel.estudiantesPorCurso,
                        onSelectAnio = viewModel::seleccionarAnio,
                        onCreate = {
                            if (rol == UserRole.UTP) {
                                navController.navigate(AppRoutes.courseForm())
                            }
                        },
                        onViewDetail = { course ->
                            navController.navigate(AppRoutes.courseDetail(course.id))
                        },
                        onEdit = { course ->
                            if (rol == UserRole.UTP) {
                                navController.navigate(AppRoutes.courseForm(course.id))
                            }
                        },
                        onDelete = { course ->
                            if (rol == UserRole.UTP) {
                                viewModel.eliminar(course.id)
                            }
                        },
                        mensajeOperacion = viewModel.mensajeOperacion,
                        onDismissMensaje = viewModel::limpiarMensaje,
                        onRetry = viewModel::cargar,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error,
                        canManage = rol == UserRole.UTP
                    )
                }

                composable(
                    route = AppRoutes.COURSE_DETAIL,
                    arguments = listOf(navArgument("courseId") { type = NavType.IntType })
                ) { entry ->
                    val courseId = entry.arguments?.getInt("courseId") ?: 0

                    val viewModel: CourseDetailViewModel = viewModel(
                        factory = CourseDetailViewModel.factory(
                            courseRepository = container.courseRepository,
                            userRepository = container.userRepository,
                            assignmentRepository = container.assignmentRepository,
                            unitRepository = container.unitRepository,
                            subjectRepository = container.subjectRepository,
                            courseId = courseId
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    CourseDetailScreen(
                        course = viewModel.course,
                        asignaturas = viewModel.asignaturas,
                        unidadesPorAsignatura = viewModel.unidadesPorAsignatura,
                        estudiantes = viewModel.estudiantes,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error,
                        onRetry = viewModel::cargar,
                        onViewSubject = { subject ->
                            navController.navigate(
                                AppRoutes.subjectDetail(
                                    subjectId = subject.id,
                                    courseId = viewModel.course?.id ?: courseId
                                )
                            )
                        }
                    )
                }

                composable(
                    route = AppRoutes.COURSE_FORM,
                    arguments = listOf(
                        navArgument("courseId") {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    )
                ) { entry ->
                    val courseId = entry.arguments
                        ?.getInt("courseId")
                        ?.takeIf { it > 0 }

                    val viewModel: CourseFormViewModel = viewModel(
                        factory = CourseFormViewModel.factory(
                            courseRepository = container.courseRepository,
                            courseId = courseId
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    CourseFormScreen(
                        nombre = viewModel.nombre,
                        anio = viewModel.anio,
                        isEditMode = viewModel.isEditMode,
                        isLoading = viewModel.isLoading,
                        isSaving = viewModel.isSaving,
                        error = viewModel.error,
                        onNombreChange = viewModel::onNombreChange,
                        onAnioChange = viewModel::onAnioChange,
                        onSubmit = {
                            viewModel.guardar {
                                navController.popBackStack()
                            }
                        },
                        onCancel = { navController.popBackStack() },
                    )
                }

                composable(AppRoutes.USERS) {
                    val viewModel: UserListViewModel = viewModel(
                        factory = UserListViewModel.factory(
                            userRepository = container.userRepository,
                            courseRepository = container.courseRepository
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    UserListScreen(
                        usuarios = viewModel.usuarios,
                        cursos = viewModel.cursos,
                        onCreate = {
                            navController.navigate(AppRoutes.userForm())
                        },
                        onEdit = { user ->
                            navController.navigate(AppRoutes.userForm(user.id))
                        },
                        onDelete = { user ->
                            viewModel.eliminar(user.id)
                        },
                        mensajeOperacion = viewModel.mensajeOperacion,
                        onDismissMensaje = viewModel::limpiarMensaje,
                        onRetry = viewModel::cargar,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error
                    )
                }

                composable(
                    route = AppRoutes.USER_FORM,
                    arguments = listOf(
                        navArgument("userId") {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    )
                ) { entry ->
                    val userId = entry.arguments
                        ?.getInt("userId")
                        ?.takeIf { it > 0 }

                    val viewModel: UserFormViewModel = viewModel(
                        factory = UserFormViewModel.factory(
                            userRepository = container.userRepository,
                            courseRepository = container.courseRepository,
                            userId = userId
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    UserFormScreen(
                        nombre = viewModel.nombre,
                        username = viewModel.username,
                        password = viewModel.password,
                        rol = viewModel.rol,
                        cursoId = viewModel.cursoId,
                        cursos = viewModel.cursos,
                        isEditMode = viewModel.isEditMode,
                        isLoading = viewModel.isLoading,
                        isSaving = viewModel.isSaving,
                        error = viewModel.error,
                        onNombreChange = viewModel::onNombreChange,
                        onUsernameChange = viewModel::onUsernameChange,
                        onPasswordChange = viewModel::onPasswordChange,
                        onRolChange = viewModel::onRolChange,
                        onCursoIdChange = viewModel::onCursoIdChange,
                        onSubmit = {
                            viewModel.guardar {
                                navController.popBackStack()
                            }
                        },
                        onCancel = { navController.popBackStack() },
                    )
                }

                composable(AppRoutes.ASSIGNMENTS) { entry ->
                    val viewModel: AssignmentListViewModel = viewModel(
                        factory = AssignmentListViewModel.factory(
                            assignmentRepository = container.assignmentRepository,
                            courseRepository = container.courseRepository,
                            unitRepository = container.unitRepository,
                            userRepository = container.userRepository,
                            teacherId = user.id
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    val refresh by entry.savedStateHandle
                        .getStateFlow(REFRESH_ASSIGNMENTS_KEY, false)
                        .collectAsState()

                    LaunchedEffect(refresh) {
                        if (refresh) {
                            viewModel.cargar()
                            entry.savedStateHandle.set(REFRESH_ASSIGNMENTS_KEY, false)
                        }
                    }

                    AssignmentListScreen(
                        asignaciones = viewModel.asignaciones,
                        cursos = viewModel.cursos,
                        unidades = viewModel.unidades,
                        docentes = viewModel.docentes,
                        onViewDetail = { assignment ->
                            navController.navigate(
                                AppRoutes.assignmentDetail(assignment.id)
                            )
                        },
                        onRetry = viewModel::cargar,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error
                    )
                }

                composable(
                    route = AppRoutes.ASSIGNMENT_DETAIL,
                    arguments = listOf(
                        navArgument("assignmentId") { type = NavType.IntType }
                    )
                ) { entry ->
                    val assignmentId = entry.arguments?.getInt("assignmentId") ?: 0

                    val viewModel: AssignmentDetailViewModel = viewModel(
                        factory = AssignmentDetailViewModel.factory(
                            assignmentRepository = container.assignmentRepository,
                            courseRepository = container.courseRepository,
                            unitRepository = container.unitRepository,
                            userRepository = container.userRepository,
                            learningObjectiveRepository = container.learningObjectiveRepository,
                            activityRepository = container.activityRepository,
                            assignmentId = assignmentId
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    val refresh by entry.savedStateHandle
                        .getStateFlow(REFRESH_ASSIGNMENT_DETAIL_KEY, false)
                        .collectAsState()

                    LaunchedEffect(refresh) {
                        if (refresh) {
                            viewModel.cargar()
                            entry.savedStateHandle
                                .set(REFRESH_ASSIGNMENT_DETAIL_KEY, false)
                            // Las pestañas Home y Asignación también deben
                            // reflejar los cambios de reglas.
                            markAssignmentsChanged(navController)
                        }
                    }

                    AssignmentDetailScreen(
                        assignment = viewModel.assignment,
                        courseName = viewModel.courseName,
                        unitTitle = viewModel.unitTitle,
                        teacherName = viewModel.teacherName,
                        objectives = viewModel.objectives,
                        activities = viewModel.activities,
                        associatedObjectiveIds = viewModel.associatedObjectiveIds,
                        associatedActivityIds = viewModel.associatedActivityIds,
                        canManage = rol == UserRole.DOCENTE,
                        isDeleting = viewModel.isDeleting,
                        isSavingObjectives = viewModel.isSavingObjectives,
                        isSavingActivities = viewModel.isSavingActivities,
                        mensajeOperacion = viewModel.mensajeOperacion,
                        onViewUnit = {
                            viewModel.assignment?.let { assignment ->
                                navController.navigate(
                                    AppRoutes.unitDetail(assignment.unidadId)
                                )
                            }
                        },
                        onViewCourse = {
                            viewModel.assignment?.let { assignment ->
                                navController.navigate(
                                    AppRoutes.courseDetail(assignment.cursoId)
                                )
                            }
                        },
                        onEdit = {
                            navController.navigate(
                                AppRoutes.assignmentForm(assignmentId)
                            )
                        },
                        onDelete = {
                            viewModel.eliminar {
                                markAssignmentsChanged(navController)
                                navController.popBackStack()
                            }
                        },
                        onObjectiveAssociationChange = viewModel::alternarObjetivo,
                        onActivityAssociationChange = viewModel::alternarActividad,
                        onDismissMensaje = viewModel::limpiarMensaje,
                        onRetry = viewModel::cargar,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error
                    )
                }

                composable(
                    route = AppRoutes.ASSIGNMENT_FORM,
                    arguments = listOf(
                        navArgument("assignmentId") { type = NavType.IntType }
                    )
                ) { entry ->
                    val assignmentId = entry.arguments?.getInt("assignmentId") ?: 0

                    val viewModel: AssignmentFormViewModel = viewModel(
                        factory = AssignmentFormViewModel.factory(
                            assignmentRepository = container.assignmentRepository,
                            courseRepository = container.courseRepository,
                            unitRepository = container.unitRepository,
                            userRepository = container.userRepository,
                            assignmentId = assignmentId
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    AssignmentFormScreen(
                        courses = viewModel.courses,
                        units = viewModel.units,
                        teachers = viewModel.teachers,
                        cursoId = viewModel.cursoId,
                        unidadId = viewModel.unidadId,
                        docenteId = viewModel.docenteId,
                        fecha = viewModel.fecha,
                        maxIntentos = viewModel.maxIntentos,
                        pruebaFinalHabilitada = viewModel.pruebaFinalHabilitada,
                        isEditMode = viewModel.isEditMode,
                        isLoading = viewModel.isLoading,
                        isSaving = viewModel.isSaving,
                        error = viewModel.error,
                        onCursoIdChange = {},
                        onUnidadIdChange = {},
                        onDocenteIdChange = {},
                        onFechaChange = viewModel::onFechaChange,
                        onMaxIntentosChange = viewModel::onMaxIntentosChange,
                        onPruebaFinalHabilitadaChange =
                            viewModel::onPruebaFinalHabilitadaChange,
                        onSubmit = {
                            viewModel.guardar {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set(REFRESH_ASSIGNMENT_DETAIL_KEY, true)
                                navController.popBackStack()
                            }
                        },
                        onCancel = { navController.popBackStack() }
                    )
                }

                composable(
                    route = AppRoutes.FINAL_TEST_SETTINGS,
                    arguments = listOf(
                        navArgument("assignmentId") { type = NavType.IntType }
                    )
                ) { entry ->
                    val assignmentId = entry.arguments?.getInt("assignmentId") ?: 0

                    val viewModel: FinalTestSettingsViewModel = viewModel(
                        factory = FinalTestSettingsViewModel.factory(
                            assignmentRepository = container.assignmentRepository,
                            assignmentId = assignmentId
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    FinalTestSettingsScreen(
                        fecha = viewModel.fecha,
                        maxIntentos = viewModel.maxIntentos,
                        pruebaFinalHabilitada = viewModel.pruebaFinalHabilitada,
                        isLoading = viewModel.isLoading,
                        isSaving = viewModel.isSaving,
                        error = viewModel.error,
                        onFechaChange = viewModel::onFechaChange,
                        onMaxIntentosChange = viewModel::onMaxIntentosChange,
                        onPruebaFinalHabilitadaChange =
                            viewModel::onPruebaFinalHabilitadaChange,
                        onSubmit = {
                            viewModel.guardar {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set(REFRESH_UNIT_DETAIL_KEY, true)
                                markAssignmentsChanged(navController)
                                navController.popBackStack()
                            }
                        },
                        onCancel = { navController.popBackStack() }
                    )
                }

                composable(AppRoutes.REPORTS) {
                    if (rol == UserRole.ESTUDIANTE) {
                        val viewModel: StudentReportListViewModel = viewModel(
                            factory = StudentReportListViewModel.factory(
                                pedagogicalReportRepository = container.pedagogicalReportRepository,
                                unitRepository = container.unitRepository,
                                studentId = user.id
                            )
                        )

                        LaunchedEffect(Unit) {
                            viewModel.cargar()
                        }

                        StudentReportListScreen(
                            informes = viewModel.informes,
                            unitTitles = viewModel.unitTitles,
                            onViewDetail = { report ->
                                navController.navigate(AppRoutes.reportDetail(report.id))
                            },
                            onRetry = viewModel::cargar,
                            isLoading = viewModel.isLoading,
                            error = viewModel.error
                        )
                    } else {
                        val viewModel: PedagogicalReportListViewModel = viewModel(
                            factory = PedagogicalReportListViewModel.factory(
                                pedagogicalReportRepository = container.pedagogicalReportRepository,
                                userRepository = container.userRepository,
                                unitRepository = container.unitRepository,
                                teacherId = user.id
                            )
                        )

                        LaunchedEffect(Unit) {
                            viewModel.cargar()
                        }

                        PedagogicalReportListScreen(
                            informes = viewModel.informes,
                            usuarios = viewModel.usuarios,
                            unidades = viewModel.unidades,
                            onViewDetail = { report ->
                                navController.navigate(AppRoutes.reportDetail(report.id))
                            },
                            onRetry = viewModel::cargar,
                            isLoading = viewModel.isLoading,
                            error = viewModel.error
                        )
                    }
                }

                composable(
                    route = AppRoutes.REPORT_DETAIL,
                    arguments = listOf(navArgument("reportId") { type = NavType.IntType })
                ) { entry ->
                    val reportId = entry.arguments?.getInt("reportId") ?: 0

                    val viewModel: PedagogicalReportDetailViewModel = viewModel(
                        factory = PedagogicalReportDetailViewModel.factory(
                            pedagogicalReportRepository = container.pedagogicalReportRepository,
                            unitRepository = container.unitRepository,
                            learningObjectiveRepository = container.learningObjectiveRepository,
                            reportId = reportId
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    PedagogicalReportDetailScreen(
                        report = viewModel.report,
                        unitTitle = viewModel.unitTitle,
                        details = viewModel.details,
                        objectives = viewModel.objectives,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error,
                        onRetry = viewModel::cargar,
                        canValidate = rol == UserRole.DOCENTE,
                        isValidating = viewModel.isValidating,
                        mensaje = viewModel.mensaje,
                        onValidate = viewModel::validar,
                        onDismissMensaje = viewModel::limpiarMensaje
                    )
                }

                composable(
                    route = AppRoutes.UNIT_DETAIL,
                    arguments = listOf(
                        navArgument("unitId") { type = NavType.IntType },
                        navArgument("courseId") {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    )
                ) { entry ->
                    val unitId = entry.arguments?.getInt("unitId") ?: 0
                    val courseIdArg = entry.arguments?.getInt("courseId") ?: -1
                    val courseId = if (courseIdArg > 0) courseIdArg else user.cursoId

                    val viewModel: UnitDetailViewModel = viewModel(
                        factory = UnitDetailViewModel.factory(
                            unitRepository = container.unitRepository,
                            subjectRepository = container.subjectRepository,
                            learningObjectiveRepository = container.learningObjectiveRepository,
                            contentRepository = container.contentRepository,
                            contentResourceRepository = container.contentResourceRepository,
                            activityRepository = container.activityRepository,
                            questionRepository = container.questionRepository,
                            assignmentRepository = container.assignmentRepository,
                            rol = rol,
                            courseId = courseId,
                            unitId = unitId,
                            courseRepository = container.courseRepository,
                            teacherId = user.id,
                            attemptRepository = container.attemptRepository,
                            studentId = user.id
                        )
                    )

                    LaunchedEffect(Unit) { viewModel.cargar() }

                    val refresh by entry.savedStateHandle
                        .getStateFlow(REFRESH_UNIT_DETAIL_KEY, false)
                        .collectAsState()

                    LaunchedEffect(refresh) {
                        if (refresh) {
                            viewModel.cargar()
                            entry.savedStateHandle
                                .set(REFRESH_UNIT_DETAIL_KEY, false)
                        }
                    }

                    UnitDetailScreen(
                        unidad = viewModel.unidad,
                        asignatura = viewModel.asignatura,
                        contenidos = viewModel.contenidos,
                        objetivos = viewModel.objetivos,
                        actividades = viewModel.actividades,
                        recursosPorContenido = viewModel.recursosPorContenido,
                        preguntasPorActividad = viewModel.preguntasPorActividad,
                        actividadesPorContenido = viewModel.actividadesPorContenido,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error,
                        course = viewModel.course,
                        onViewContent = { content ->
                            navController.navigate(AppRoutes.contentDetail(content.id))
                        },
                        onOpenActivity = { activity ->
                            navController.navigate(
                                AppRoutes.activityAttempt(
                                    activityId = activity.id,
                                    preview = rol != UserRole.ESTUDIANTE
                                )
                            )
                        },
                        maxIntentosPruebaFinal = viewModel.maxIntentosPruebaFinal,
                        fechaPruebaFinal = viewModel.fechaPruebaFinal,
                        pruebaFinalHabilitada = viewModel.pruebaFinalHabilitada,
                        pruebaFinalDeshabilitada = viewModel.pruebaFinalDeshabilitada,
                        intentosPruebaFinalCount = viewModel.intentosPruebaFinalCount,
                        onEditFinalTest = if (rol == UserRole.DOCENTE) {
                            viewModel.asignacionPruebaFinalId?.let { assignmentId ->
                                {
                                    navController.navigate(
                                        AppRoutes.finalTestSettings(assignmentId)
                                    )
                                }
                            }
                        } else null,
                        onAutorizar = {
                            navController.navigate(AppRoutes.unitAuthorization(unitId))
                        },
                        onRetry = viewModel::cargar,
                        rol = rol,
                        puedeAutorizar = rol == UserRole.UTP,
                        puedeResolverActividades = rol == UserRole.ESTUDIANTE,
                        puedeCrearContenido = rol == UserRole.DOCENTE,
                        // Solo el docente crea el material de trabajo de la unidad.
                        onCreateContent = if (rol == UserRole.DOCENTE) {
                            { navController.navigate(AppRoutes.contentForm(unitId)) }
                        } else {
                            null
                        }
                    )
                }

                composable(
                    route = AppRoutes.CONTENT_FORM,
                    arguments = listOf(navArgument("unitId") { type = NavType.IntType })
                ) { entry ->
                    val unitId = entry.arguments?.getInt("unitId") ?: 0

                    val viewModel: ContentFormViewModel = viewModel(
                        factory = ContentFormViewModel.factory(
                            contentRepository = container.contentRepository,
                            learningObjectiveRepository = container.learningObjectiveRepository,
                            unitId = unitId
                        )
                    )

                    LaunchedEffect(Unit) { viewModel.cargar() }

                    ContentFormScreen(
                        objetivos = viewModel.objetivos,
                        selectedObjectiveId = viewModel.selectedObjectiveId,
                        titulo = viewModel.titulo,
                        explicacion = viewModel.explicacion,
                        ejemplos = viewModel.ejemplos,
                        instrucciones = viewModel.instrucciones,
                        orden = viewModel.orden,
                        origen = viewModel.origen,
                        isLoading = viewModel.isLoading,
                        isSaving = viewModel.isSaving,
                        loadError = viewModel.loadError,
                        error = viewModel.error,
                        onObjectiveChange = viewModel::onObjectiveChange,
                        onTituloChange = viewModel::onTituloChange,
                        onExplicacionChange = viewModel::onExplicacionChange,
                        onEjemplosChange = viewModel::onEjemplosChange,
                        onInstruccionesChange = viewModel::onInstruccionesChange,
                        onOrdenChange = viewModel::onOrdenChange,
                        onOrigenChange = viewModel::onOrigenChange,
                        onSubmit = {
                            viewModel.guardar {
                                // El detalle de la unidad recarga para mostrar
                                // el contenido recién creado.
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set(REFRESH_UNIT_DETAIL_KEY, true)

                                navController.popBackStack()
                            }
                        },
                        onCancel = { navController.popBackStack() },
                        onRetry = viewModel::cargar,
                    )
                }

                composable(
                    route = AppRoutes.CONTENT_DETAIL,
                    arguments = listOf(navArgument("contentId") { type = NavType.IntType })
                ) { entry ->
                    val contentId = entry.arguments?.getInt("contentId") ?: 0
                    val context = LocalContext.current

                    val viewModel: ContentDetailViewModel = viewModel(
                        factory = ContentDetailViewModel.factory(
                            contentRepository = container.contentRepository,
                            learningObjectiveRepository = container.learningObjectiveRepository,
                            contentResourceRepository = container.contentResourceRepository,
                            activityRepository = container.activityRepository,
                            questionRepository = container.questionRepository,
                            assignmentRepository = container.assignmentRepository,
                            rol = rol,
                            courseId = user.cursoId,
                            contentId = contentId
                        )
                    )

                    LaunchedEffect(Unit) { viewModel.cargar() }

                    val refresh by entry.savedStateHandle
                        .getStateFlow(REFRESH_CONTENT_DETAIL_KEY, false)
                        .collectAsState()

                    LaunchedEffect(refresh) {
                        if (refresh) {
                            viewModel.cargar()
                            entry.savedStateHandle
                                .set(REFRESH_CONTENT_DETAIL_KEY, false)
                        }
                    }

                    ContentDetailScreen(
                        contenido = viewModel.contenido,
                        objetivo = viewModel.objetivo,
                        recursos = viewModel.recursos,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error,
                        onOpenResource = { url ->
                            runCatching {
                                context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
                            }.onFailure {
                                Toast.makeText(context, "No se pudo abrir el recurso.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onRetry = viewModel::cargar,
                        actividades = viewModel.actividades,
                        preguntasPorActividad = viewModel.preguntasPorActividad,
                        onOpenActivity = { activity ->
                            navController.navigate(
                                AppRoutes.activityAttempt(
                                    activityId = activity.id,
                                    preview = rol != UserRole.ESTUDIANTE
                                )
                            )
                        },
                        activityActionLabel = if (rol == UserRole.ESTUDIANTE) "Resolver" else "Vista previa",
                        // Solo el docente crea actividades; el contenido se
                        // preselecciona y queda bloqueado en el formulario.
                        onCreateActivity = if (rol == UserRole.DOCENTE) {
                            {
                                viewModel.contenido?.let { content ->
                                    navController.navigate(
                                        AppRoutes.activityForm(
                                            unitId = content.unidadId,
                                            contentId = content.id
                                        )
                                    )
                                }
                            }
                        } else {
                            null
                        },
                        onEditActivity = if (rol == UserRole.DOCENTE) {
                            { activity ->
                                navController.navigate(
                                    AppRoutes.activityForm(
                                        unitId = activity.unidadId,
                                        contentId = activity.contenidoId,
                                        activityId = activity.id
                                    )
                                )
                            }
                        } else null
                    )
                }

                composable(
                    route = AppRoutes.ACTIVITY_FORM,
                    arguments = listOf(
                        navArgument("unitId") { type = NavType.IntType },
                        navArgument("contentId") {
                            type = NavType.IntType
                            defaultValue = -1
                        },
                        navArgument("activityId") {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    )
                ) { entry ->
                    val unitId = entry.arguments?.getInt("unitId") ?: 0
                    val lockedContentId = entry.arguments
                        ?.getInt("contentId")
                        ?.takeIf { it > 0 }
                    val activityId = entry.arguments
                        ?.getInt("activityId")
                        ?.takeIf { it > 0 }

                    val viewModel: ActivityFormViewModel = viewModel(
                        factory = ActivityFormViewModel.factory(
                            activityRepository = container.activityRepository,
                            contentRepository = container.contentRepository,
                            assignmentRepository = container.assignmentRepository,
                            unitId = unitId,
                            lockedContentId = lockedContentId,
                            activityId = activityId
                        )
                    )

                    LaunchedEffect(Unit) { viewModel.cargar() }

                    ActivityFormScreen(
                        contenidos = viewModel.contenidos,
                        titulo = viewModel.titulo,
                        tipo = viewModel.tipo,
                        orden = viewModel.orden,
                        contenidoId = viewModel.contenidoId,
                        maxIntentos = viewModel.maxIntentos,
                        hasFinalTest = viewModel.hasFinalTest,
                        isLoading = viewModel.isLoading,
                        isSaving = viewModel.isSaving,
                        loadError = viewModel.loadError,
                        error = viewModel.error,
                        isEditMode = viewModel.isEditMode,
                        isContentLocked = viewModel.isContentLocked,
                        onTituloChange = viewModel::onTituloChange,
                        onTipoChange = viewModel::onTipoChange,
                        onOrdenChange = viewModel::onOrdenChange,
                        onContenidoChange = viewModel::onContenidoChange,
                        onMaxIntentosChange = viewModel::onMaxIntentosChange,
                        onSubmit = {
                            viewModel.guardar {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set(REFRESH_CONTENT_DETAIL_KEY, true)
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set(REFRESH_UNIT_DETAIL_KEY, true)

                                navController.popBackStack()
                            }
                        },
                        onCancel = { navController.popBackStack() },
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
                    onRetry = viewModel::cargar,
                )
                }
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
    courseId: Int?,
    onViewDetail: (LearningUnit) -> Unit,
    onAuthorize: (LearningUnit) -> Unit
) {
    val viewModel: UnitListViewModel = viewModel(
        factory = UnitListViewModel.factory(
            unitRepository = container.unitRepository,
            subjectRepository = container.subjectRepository,
            assignmentRepository = container.assignmentRepository,
            learningObjectiveRepository = container.learningObjectiveRepository,
            contentRepository = container.contentRepository,
            activityRepository = container.activityRepository,
            rol = rol,
            courseId = courseId
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

    val headerTitle = when (rol) {
        UserRole.UTP -> "Revisión de unidades"
        UserRole.DOCENTE -> "Unidades asignadas"
        UserRole.ESTUDIANTE -> "Mis unidades"
    }

    val emptyMessage = if (rol == UserRole.ESTUDIANTE && viewModel.sinCurso) {
        "No tienes curso asignado."
    } else {
        null
    }

    UnitListScreen(
        unidades = viewModel.unidades,
        asignaturas = viewModel.asignaturas,
        isLoading = viewModel.isLoading,
        error = viewModel.error,
        onViewDetail = { unit -> onViewDetail(unit) },
        // Solo la UTP puede autorizar y publicar unidades.
        onAuthorize = if (rol == UserRole.UTP) {
            { unit -> onAuthorize(unit) }
        } else {
            null
        },
        onRetry = viewModel::cargar,
        headerTitle = headerTitle,
        emptyMessage = emptyMessage,
        contenidosPorUnidad = viewModel.contenidosPorUnidad,
        actividadesPorUnidad = viewModel.actividadesPorUnidad,
        pruebaFinalPorUnidad = viewModel.pruebaFinalPorUnidad
    )
}

/**
 * Destino del panel del estudiante.
 *
 * Carga sus unidades y actividades derivadas de las asignaciones de su curso.
 */
@Composable
private fun HomeStudentDestination(
    container: AppContainer,
    navController: NavHostController,
    user: User,
    onVerActividades: () -> Unit
) {
    val viewModel: HomeStudentViewModel = viewModel(
        factory = HomeStudentViewModel.factory(
            assignmentRepository = container.assignmentRepository,
            unitRepository = container.unitRepository,
            subjectRepository = container.subjectRepository,
            activityRepository = container.activityRepository,
            attemptRepository = container.attemptRepository,
            pedagogicalReportRepository = container.pedagogicalReportRepository,
            studentId = user.id,
            courseId = user.cursoId
        )
    )

    LaunchedEffect(Unit) {
        viewModel.cargar()
    }

    val refresh by (navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow(REFRESH_HOME_KEY, false)
        ?.collectAsState() ?: remember { mutableStateOf(false) })

    LaunchedEffect(refresh) {
        if (refresh) {
            viewModel.cargar()
            navController.currentBackStackEntry?.savedStateHandle?.set(REFRESH_HOME_KEY, false)
        }
    }

    HomeStudentScreen(
        unidades = viewModel.unidades,
        asignaturas = viewModel.asignaturas,
        actividadesPendientes = viewModel.actividadesPendientes,
        actividadesEnProgreso = viewModel.actividadesEnProgreso,
        totalInformes = viewModel.totalInformes,
        sinCurso = viewModel.sinCurso,
        courseId = user.cursoId,
        onViewSubject = { subject ->
            user.cursoId?.let { courseId ->
                navController.navigate(
                    AppRoutes.subjectDetail(subject.id, courseId)
                )
            }
        },
        onVerActividades = onVerActividades,
        onResolveActivity = { activity ->
            navController.navigate(
                AppRoutes.activityAttempt(activity.id)
            )
        },
        onCargar = viewModel::cargar,
        isLoading = viewModel.isLoading,
        error = viewModel.error
    )
}

/** Clave con la que el listado de unidades recibe el aviso de recarga. */
private const val REFRESH_UNITS_KEY = "refrescarUnidades"

/** Clave con la que el detalle de una unidad recibe el aviso de recarga. */
private const val REFRESH_UNIT_DETAIL_KEY = "refrescarDetalleUnidad"

/** Clave con la que el detalle de un contenido recibe el aviso de recarga. */
private const val REFRESH_CONTENT_DETAIL_KEY = "refrescarDetalleContenido"

/** Clave con la que el detalle de una asignación recibe el aviso de recarga. */
private const val REFRESH_ASSIGNMENT_DETAIL_KEY = "refrescarAsignacion"

/** Clave con la que el Home y el listado de asignaciones reciben el aviso de recarga. */
private const val REFRESH_ASSIGNMENTS_KEY = "refrescarAsignaciones"

/** Clave con la que el listado de actividades del estudiante recibe el aviso de recarga. */
private const val REFRESH_ACTIVITIES_KEY = "refrescarActividades"

/** Clave con la que el panel de inicio del estudiante recibe el aviso de recarga. */
private const val REFRESH_HOME_KEY = "refrescarHome"

/**
 * Marca el Home y el listado de asignaciones para que recarguen.
 *
 * La pantalla de origen puede ser cualquiera de las dos; se usa `runCatching`
 * porque una pestaña puede no estar en la pila en ese momento.
 */
private fun markAssignmentsChanged(navController: NavHostController) {
    listOf(AppRoutes.HOME, AppRoutes.ASSIGNMENTS).forEach { route ->
        runCatching {
            navController.getBackStackEntry(route).savedStateHandle
                .set(REFRESH_ASSIGNMENTS_KEY, true)
        }
    }
}

/**
 * Marca el Home, el listado de actividades y los detalles de unidad y contenido
 * para que recarguen tras completar o cambiar un intento de actividad.
 */
private fun markActivityCompleted(navController: NavHostController) {
    listOf(
        AppRoutes.HOME,
        AppRoutes.ACTIVITIES,
        AppRoutes.UNIT_DETAIL,
        AppRoutes.CONTENT_DETAIL
    ).forEach { route ->
        runCatching {
            navController.getBackStackEntry(route).savedStateHandle.apply {
                set(REFRESH_HOME_KEY, true)
                set(REFRESH_ACTIVITIES_KEY, true)
                set(REFRESH_UNIT_DETAIL_KEY, true)
                set(REFRESH_CONTENT_DETAIL_KEY, true)
            }
        }
    }
}

/** Rutas hijas que muestran la flecha de retroceso en la barra superior. */
private val ROUTES_WITH_BACK = setOf(
    AppRoutes.UNIT_DETAIL,
    AppRoutes.SUBJECT_DETAIL,
    AppRoutes.COURSE_DETAIL,
    AppRoutes.CONTENT_DETAIL,
    AppRoutes.CONTENT_FORM,
    AppRoutes.ACTIVITY_FORM,
    AppRoutes.UNIT_AUTHORIZATION,
    AppRoutes.SUBJECT_FORM,
    AppRoutes.COURSE_FORM,
    AppRoutes.USER_FORM,
    AppRoutes.ACTIVITY_ATTEMPT,
    AppRoutes.ATTEMPT_RESULT,
    AppRoutes.REPORT_DETAIL,
    AppRoutes.ASSIGNMENT_DETAIL,
    AppRoutes.ASSIGNMENT_FORM,
    AppRoutes.FINAL_TEST_SETTINGS
)

/**
 * Detalles que conservan la barra inferior y la pestaña que representan.
 *
 * Son vistas hijas de una pestaña, no tareas modales: mantener la barra evita
 * que el `Scaffold` cambie su padding al navegar y deja a mano la sección de
 * origen. Formularios, intentos y autorización quedan fuera: tienen acciones
 * propias y la barra podría descartar lo que se está editando.
 */
private val CHILD_ROUTE_PARENT_TAB = mapOf(
    AppRoutes.UNIT_DETAIL to AppRoutes.UNITS,
    AppRoutes.SUBJECT_DETAIL to AppRoutes.SUBJECTS,
    AppRoutes.COURSE_DETAIL to AppRoutes.COURSES,
    AppRoutes.CONTENT_DETAIL to AppRoutes.UNITS,
    AppRoutes.REPORT_DETAIL to AppRoutes.REPORTS,
    AppRoutes.ASSIGNMENT_DETAIL to AppRoutes.ASSIGNMENTS
)

/** Duración total de las transiciones (token `motion.duration.long1` de M3). */
private const val TRANSITION_MS = 450

/** Primer 35 % de la transición: la pantalla saliente se apaga. */
private const val TRANSITION_FADE_OUT_MS = 158

/** 65 % restante: la pantalla entrante aparece (Material "fade through"). */
private const val TRANSITION_FADE_IN_MS = 292

/** Escala inicial de la pestaña entrante en el patrón "fade through". */
private const val TAB_START_SCALE = 0.92f

/** Escala inicial de un detalle al entrar (shared axis Z hacia adelante). */
private const val DETAIL_ENTER_SCALE = 0.8f

/** Escala final del listado al salir hacia un detalle (shared axis Z). */
private const val DETAIL_EXIT_SCALE = 1.1f

/** Easing "emphasized decelerate" de Material 3, para lo que entra. */
private val EmphasizedEnter = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)

/** Easing "emphasized accelerate" de Material 3, para lo que sale. */
private val EmphasizedExit = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

/**
 * Entrada de una pestaña: "fade through" de Material 3.
 *
 * Solo escala la pantalla entrante; la saliente únicamente se apaga, como en
 * `MaterialFadeThrough`.
 */
private fun tabEnter(): EnterTransition =
    fadeIn(
        tween(
            TRANSITION_FADE_IN_MS,
            delayMillis = TRANSITION_FADE_OUT_MS,
            easing = EmphasizedEnter
        )
    ) + scaleIn(
        tween(TRANSITION_MS, easing = EmphasizedEnter),
        initialScale = TAB_START_SCALE
    )

/** Salida de una pestaña: se apaga en el primer tramo de la transición. */
private fun tabExit(): ExitTransition =
    fadeOut(tween(TRANSITION_FADE_OUT_MS, easing = EmphasizedExit))

/** Entrada de un detalle: shared axis Z, la pantalla crece desde 0.8. */
private fun detailEnter(): EnterTransition =
    fadeIn(
        tween(
            TRANSITION_FADE_IN_MS,
            delayMillis = TRANSITION_FADE_OUT_MS,
            easing = EmphasizedEnter
        )
    ) + scaleIn(
        tween(TRANSITION_MS, easing = EmphasizedEnter),
        initialScale = DETAIL_ENTER_SCALE
    )

/** Salida del listado al abrir un detalle: se aleja hasta 1.1. */
private fun detailExit(): ExitTransition =
    fadeOut(tween(TRANSITION_FADE_OUT_MS, easing = EmphasizedExit)) +
        scaleOut(
            tween(TRANSITION_MS, easing = EmphasizedExit),
            targetScale = DETAIL_EXIT_SCALE
        )

/** Vuelta desde un detalle: la pantalla anterior reaparece desde 1.1. */
private fun detailPopEnter(): EnterTransition =
    fadeIn(
        tween(
            TRANSITION_FADE_IN_MS,
            delayMillis = TRANSITION_FADE_OUT_MS,
            easing = EmphasizedEnter
        )
    ) + scaleIn(
        tween(TRANSITION_MS, easing = EmphasizedEnter),
        initialScale = DETAIL_EXIT_SCALE
    )

/** Vuelta desde un detalle: la pantalla actual se aleja hasta 0.8. */
private fun detailPopExit(): ExitTransition =
    fadeOut(tween(TRANSITION_FADE_OUT_MS, easing = EmphasizedExit)) +
        scaleOut(
            tween(TRANSITION_MS, easing = EmphasizedExit),
            targetScale = DETAIL_ENTER_SCALE
        )
