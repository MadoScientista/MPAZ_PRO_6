package com.ccstudio.mpaz_pro_6.ui.navigation

import android.content.Intent
import android.net.Uri
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
import com.ccstudio.mpaz_pro_6.ui.screens.activities.StudentActivityListScreen
import com.ccstudio.mpaz_pro_6.ui.screens.activities.StudentActivityListViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.assignments.AssignmentListScreen
import com.ccstudio.mpaz_pro_6.ui.screens.assignments.AssignmentListViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.attempts.ActivityAttemptScreen
import com.ccstudio.mpaz_pro_6.ui.screens.attempts.ActivityAttemptViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.attempts.AttemptResultScreen
import com.ccstudio.mpaz_pro_6.ui.screens.attempts.AttemptResultViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.contents.ContentDetailScreen
import com.ccstudio.mpaz_pro_6.ui.screens.contents.ContentDetailViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.auth.LoginScreen
import com.ccstudio.mpaz_pro_6.ui.screens.auth.LoginViewModel
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
import com.ccstudio.mpaz_pro_6.ui.screens.subjects.SubjectFormScreen
import com.ccstudio.mpaz_pro_6.ui.screens.subjects.SubjectFormViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.subjects.SubjectListScreen
import com.ccstudio.mpaz_pro_6.ui.screens.subjects.SubjectListViewModel
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
        AppRoutes.CONTENT_DETAIL -> "Detalle del contenido"
        AppRoutes.UNIT_AUTHORIZATION -> "Autorización de unidad"
        AppRoutes.SUBJECT_FORM -> if (editingSubject) {
            "Editar asignatura"
        } else {
            "Nueva asignatura"
        }

        AppRoutes.COURSE_FORM -> if (editingCourse) "Editar curso" else "Nuevo curso"
        AppRoutes.USER_FORM -> if (editingUser) "Editar usuario" else "Nuevo usuario"
        AppRoutes.ACTIVITY_ATTEMPT -> "Resolver actividad"
        AppRoutes.ATTEMPT_RESULT -> "Resultado del intento"
        AppRoutes.REPORT_DETAIL -> "Informe pedagógico"
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
                                userName = user?.nombre.orEmpty(),
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
                        navController.navigate(destination.route) {
                            // Evita apilar tabs y conserva el estado de cada uno.
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
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
                                unidadesPendientes = viewModel.unidadesPendientes,
                                totalCursos = viewModel.totalCursos,
                                totalAsignaturas = viewModel.totalAsignaturas,
                                isLoading = viewModel.isLoading,
                                error = viewModel.error,
                                onVerUnidades = { navController.navigate(AppRoutes.UNITS) },
                                onCargar = viewModel::cargar,
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
                                onCargar = viewModel::cargar,
                            )
                        }

                        UserRole.ESTUDIANTE -> {
                            HomeStudentDestination(
                                container = container,
                                navController = navController,
                                user = user,
                                onViewUnit = { unit ->
                                    navController.navigate(AppRoutes.unitDetail(unit.id))
                                },
                                onVerUnidades = {
                                    navController.navigate(AppRoutes.UNITS)
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
                        navArgument("activityId") { type = NavType.IntType }
                    )
                ) { entry ->
                    val activityId = entry.arguments?.getInt("activityId") ?: 0

                    val viewModel: ActivityAttemptViewModel = viewModel(
                        factory = ActivityAttemptViewModel.factory(
                            activityRepository = container.activityRepository,
                            questionRepository = container.questionRepository,
                            alternativeRepository = container.alternativeRepository,
                            attemptRepository = container.attemptRepository,
                            studentAnswerRepository = container.studentAnswerRepository,
                            studentId = user.id,
                            activityId = activityId
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
                        onComplete = {
                            viewModel.completar { attemptId ->
                                navController.navigate(AppRoutes.attemptResult(attemptId))
                            }
                        },
                        onViewResult = { attemptId ->
                            navController.navigate(AppRoutes.attemptResult(attemptId))
                        },
                        onRetry = viewModel::cargar,
                        onDismissMensaje = viewModel::limpiarMensaje,
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

                composable(AppRoutes.ACTIVITIES) {
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

                composable(AppRoutes.SUBJECTS) {
                    val viewModel: SubjectListViewModel = viewModel(
                        factory = SubjectListViewModel.factory(
                            subjectRepository = container.subjectRepository
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    SubjectListScreen(
                        asignaturas = viewModel.asignaturas,
                        onCreate = {
                            navController.navigate(AppRoutes.subjectForm())
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
                        error = viewModel.error
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
                            courseRepository = container.courseRepository
                        )
                    )

                    LaunchedEffect(Unit) {
                        viewModel.cargar()
                    }

                    CourseListScreen(
                        cursos = viewModel.cursos,
                        aniosDisponibles = viewModel.aniosDisponibles,
                        anioSeleccionado = viewModel.anioSeleccionado,
                        onSelectAnio = viewModel::seleccionarAnio,
                        onCreate = {
                            navController.navigate(AppRoutes.courseForm())
                        },
                        onEdit = { course ->
                            navController.navigate(AppRoutes.courseForm(course.id))
                        },
                        onDelete = { course ->
                            viewModel.eliminar(course.id)
                        },
                        mensajeOperacion = viewModel.mensajeOperacion,
                        onDismissMensaje = viewModel::limpiarMensaje,
                        onRetry = viewModel::cargar,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error
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

                composable(AppRoutes.ASSIGNMENTS) {
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

                    AssignmentListScreen(
                        asignaciones = viewModel.asignaciones,
                        cursos = viewModel.cursos,
                        unidades = viewModel.unidades,
                        docentes = viewModel.docentes,
                        // El detalle de la asignación llega con su pantalla.
                        onViewDetail = {},
                        onRetry = viewModel::cargar,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error
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
                            questionRepository = container.questionRepository,
                            assignmentRepository = container.assignmentRepository,
                            rol = rol,
                            courseId = user.cursoId,
                            unitId = unitId
                        )
                    )

                    LaunchedEffect(Unit) { viewModel.cargar() }

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
                        onViewContent = { content ->
                            navController.navigate(AppRoutes.contentDetail(content.id))
                        },
                        onOpenActivity = { activity ->
                            navController.navigate(AppRoutes.activityAttempt(activity.id))
                        },
                        onAutorizar = {
                            navController.navigate(AppRoutes.unitAuthorization(unitId))
                        },
                        onRetry = viewModel::cargar,
                        puedeAutorizar = rol == UserRole.UTP,
                        puedeResolverActividades = rol == UserRole.ESTUDIANTE
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

                    ContentDetailScreen(
                        contenido = viewModel.contenido,
                        objetivo = viewModel.objetivo,
                        recursos = viewModel.recursos,
                        isLoading = viewModel.isLoading,
                        error = viewModel.error,
                        onOpenResource = { url ->
                            runCatching {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                            }.onFailure {
                                Toast.makeText(context, "No se pudo abrir el recurso.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onRetry = viewModel::cargar,
                        actividades = viewModel.actividades,
                        preguntasPorActividad = viewModel.preguntasPorActividad,
                        onOpenActivity = { activity ->
                            navController.navigate(AppRoutes.activityAttempt(activity.id))
                        },
                        activityActionLabel = if (rol == UserRole.ESTUDIANTE) "Resolver" else "Vista previa"
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
        // Solo la UTP autoriza; docente y estudiante no ven ese botón.
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
    onViewUnit: (LearningUnit) -> Unit,
    onVerUnidades: () -> Unit
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

    HomeStudentScreen(
        unidades = viewModel.unidades,
        asignaturas = viewModel.asignaturas,
        actividadesPendientes = viewModel.actividadesPendientes,
        totalInformes = viewModel.totalInformes,
        sinCurso = viewModel.sinCurso,
        onViewUnit = { unit -> onViewUnit(unit) },
        onVerUnidades = onVerUnidades,
        onResolveActivity = { activity ->
            navController.navigate(AppRoutes.activityAttempt(activity.id))
        },
        onCargar = viewModel::cargar,
        isLoading = viewModel.isLoading,
        error = viewModel.error
    )
}

/** Clave con la que el listado de unidades recibe el aviso de recarga. */
private const val REFRESH_UNITS_KEY = "refrescarUnidades"

/** Rutas hijas que muestran la flecha de retroceso en la barra superior. */
private val ROUTES_WITH_BACK = setOf(
    AppRoutes.UNIT_DETAIL,
    AppRoutes.CONTENT_DETAIL,
    AppRoutes.UNIT_AUTHORIZATION,
    AppRoutes.SUBJECT_FORM,
    AppRoutes.COURSE_FORM,
    AppRoutes.USER_FORM,
    AppRoutes.ACTIVITY_ATTEMPT,
    AppRoutes.ATTEMPT_RESULT,
    AppRoutes.REPORT_DETAIL
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
    AppRoutes.CONTENT_DETAIL to AppRoutes.UNITS,
    AppRoutes.REPORT_DETAIL to AppRoutes.REPORTS
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
