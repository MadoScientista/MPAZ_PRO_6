package com.ccstudio.mpaz_pro_6.data

import android.content.Context
import com.ccstudio.mpaz_pro_6.BuildConfig
import com.ccstudio.mpaz_pro_6.data.auth.LocalAuthRepository
import com.ccstudio.mpaz_pro_6.data.auth.RemoteAuthRepository
import com.ccstudio.mpaz_pro_6.data.local.MpazDatabase
import com.ccstudio.mpaz_pro_6.data.local.SeedLoader
import com.ccstudio.mpaz_pro_6.data.local.repository.LocalActivityRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.LocalAlternativeRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.LocalAssignmentRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.LocalAttemptRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.LocalContentRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.LocalContentResourceRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.LocalCourseRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.LocalLearningObjectiveRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.LocalPedagogicalReportRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.LocalQuestionRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.LocalStudentAnswerRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.LocalSubjectRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.LocalUnitRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.LocalUserRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.OfflineFirstActivityRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.OfflineFirstAlternativeRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.OfflineFirstAssignmentRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.OfflineFirstAttemptRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.OfflineFirstContentRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.OfflineFirstContentResourceRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.OfflineFirstCourseRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.OfflineFirstLearningObjectiveRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.OfflineFirstPedagogicalReportRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.OfflineFirstQuestionRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.OfflineFirstStudentAnswerRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.OfflineFirstSubjectRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.OfflineFirstUnitRepository
import com.ccstudio.mpaz_pro_6.data.local.repository.OfflineFirstUserRepository
import com.ccstudio.mpaz_pro_6.data.preferences.ThemePreferenceStore
import com.ccstudio.mpaz_pro_6.data.remote.ApiClient
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AlternativeRepository
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.AttemptRepository
import com.ccstudio.mpaz_pro_6.data.repository.AuthRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentResourceRepository
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.LearningObjectiveRepository
import com.ccstudio.mpaz_pro_6.data.repository.PedagogicalReportRepository
import com.ccstudio.mpaz_pro_6.data.repository.QuestionRepository
import com.ccstudio.mpaz_pro_6.data.repository.StudentAnswerRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository
import com.ccstudio.mpaz_pro_6.data.session.Session
import com.ccstudio.mpaz_pro_6.data.session.SessionState
import com.ccstudio.mpaz_pro_6.data.session.SessionStore
import com.ccstudio.mpaz_pro_6.data.sync.InitialSync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Contenedor de dependencias de la app.
 *
 * Es el único punto donde se decide qué implementación de cada repositorio usa
 * la UI. Combina Room como caché local, Retrofit como fuente remota y DataStore
 * para persistir el token.
 *
 * No se usa un contenedor de inyección de dependencias externo (Hilt, Koin): con
 * este volumen de dependencias, una clase manual es suficiente y evita agregar
 * dependencias y procesamiento de anotaciones al proyecto.
 */
class AppContainer(context: Context) {

    /** Modo de desarrollo sin backend: auth local y CRUD exclusivo en Room. */
    val localOnly: Boolean = BuildConfig.MPAZ_LOCAL_ONLY

    private val appContext = context.applicationContext

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val cacheMutex = Mutex()

    /** Base de datos local con la semilla de demostración. */
    val database: MpazDatabase = MpazDatabase.build(appContext)

    /** Almacenamiento persistente de la sesión. */
    val sessionStore = SessionStore(appContext)

    /** Preferencia de tema claro/oscuro, independiente de la sesión. */
    val themePreferenceStore = ThemePreferenceStore(appContext)

    /** Sesión en memoria que consume la navegación. */
    val session = Session()

    private val apiClient = ApiClient(
        tokenProvider = sessionStore::currentToken,
        onUnauthorized = {
            appScope.launch {
                sessionStore.clear()
                session.unauthenticated()
            }
        }
    )

    /** Autenticación remota o local según el modo de desarrollo. */
    val authRepository: AuthRepository = if (localOnly) {
        LocalAuthRepository(
            userDao = database.userDao(),
            sessionStore = sessionStore,
            session = session
        )
    } else {
        RemoteAuthRepository(
            api = apiClient.api,
            sessionStore = sessionStore,
            session = session
        )
    }

    /** Repositorio de asignaturas con caché Room. */
    val subjectRepository: SubjectRepository = if (localOnly) {
        LocalSubjectRepository(dao = database.catalogDao())
    } else {
        OfflineFirstSubjectRepository(
            api = apiClient.api,
            dao = database.catalogDao()
        )
    }

    /** Repositorio de cursos con caché Room. */
    val courseRepository: CourseRepository = if (localOnly) {
        LocalCourseRepository(dao = database.catalogDao())
    } else {
        OfflineFirstCourseRepository(
            api = apiClient.api,
            dao = database.catalogDao()
        )
    }

    /** Repositorio de unidades de aprendizaje con caché Room. */
    val unitRepository: UnitRepository = if (localOnly) {
        LocalUnitRepository(
            dao = database.unitDao(),
            catalogDao = database.catalogDao(),
            userDao = database.userDao()
        )
    } else {
        OfflineFirstUnitRepository(
            api = apiClient.api,
            dao = database.unitDao()
        )
    }

    /** Repositorio de usuarios con caché Room. */
    val userRepository: UserRepository = if (localOnly) {
        LocalUserRepository(dao = database.userDao())
    } else {
        OfflineFirstUserRepository(
            api = apiClient.api,
            dao = database.userDao()
        )
    }

    /** Repositorio de objetivos de aprendizaje. */
    val learningObjectiveRepository: LearningObjectiveRepository = if (localOnly) {
        LocalLearningObjectiveRepository(
            dao = database.curriculumDao(),
            unitDao = database.unitDao()
        )
    } else {
        OfflineFirstLearningObjectiveRepository(
            api = apiClient.api,
            dao = database.curriculumDao()
        )
    }

    /** Repositorio de contenidos. */
    val contentRepository: ContentRepository = if (localOnly) {
        LocalContentRepository(dao = database.curriculumDao())
    } else {
        OfflineFirstContentRepository(
            api = apiClient.api,
            dao = database.curriculumDao()
        )
    }

    /** Repositorio de recursos de contenido. */
    val contentResourceRepository: ContentResourceRepository = if (localOnly) {
        LocalContentResourceRepository(dao = database.curriculumDao())
    } else {
        OfflineFirstContentResourceRepository(
            api = apiClient.api,
            dao = database.curriculumDao()
        )
    }

    /** Repositorio de actividades. */
    val activityRepository: ActivityRepository = if (localOnly) {
        LocalActivityRepository(
            dao = database.curriculumDao(),
            unitDao = database.unitDao()
        )
    } else {
        OfflineFirstActivityRepository(
            api = apiClient.api,
            dao = database.curriculumDao()
        )
    }

    /** Repositorio de preguntas. */
    val questionRepository: QuestionRepository = if (localOnly) {
        LocalQuestionRepository(dao = database.curriculumDao())
    } else {
        OfflineFirstQuestionRepository(
            api = apiClient.api,
            dao = database.curriculumDao()
        )
    }

    /** Repositorio de alternativas. */
    val alternativeRepository: AlternativeRepository = if (localOnly) {
        LocalAlternativeRepository(dao = database.curriculumDao())
    } else {
        OfflineFirstAlternativeRepository(
            api = apiClient.api,
            dao = database.curriculumDao()
        )
    }

    /** Repositorio de asignaciones y sus asociaciones. */
    val assignmentRepository: AssignmentRepository = if (localOnly) {
        LocalAssignmentRepository(
            dao = database.assignmentDao(),
            catalogDao = database.catalogDao(),
            unitDao = database.unitDao(),
            userDao = database.userDao(),
            curriculumDao = database.curriculumDao()
        )
    } else {
        OfflineFirstAssignmentRepository(
            api = apiClient.api,
            dao = database.assignmentDao()
        )
    }

    /** Repositorio de intentos. */
    val attemptRepository: AttemptRepository = if (localOnly) {
        LocalAttemptRepository(
            dao = database.attemptDao(),
            userDao = database.userDao(),
            curriculumDao = database.curriculumDao()
        )
    } else {
        OfflineFirstAttemptRepository(
            api = apiClient.api,
            dao = database.attemptDao()
        )
    }

    /** Repositorio de respuestas de estudiantes. */
    val studentAnswerRepository: StudentAnswerRepository = if (localOnly) {
        LocalStudentAnswerRepository(
            dao = database.attemptDao(),
            curriculumDao = database.curriculumDao()
        )
    } else {
        OfflineFirstStudentAnswerRepository(
            api = apiClient.api,
            dao = database.attemptDao()
        )
    }

    /** Repositorio de informes pedagógicos. */
    val pedagogicalReportRepository: PedagogicalReportRepository = if (localOnly) {
        LocalPedagogicalReportRepository(
            database = database,
            dao = database.reportDao(),
            unitDao = database.unitDao(),
            userDao = database.userDao(),
            curriculumDao = database.curriculumDao(),
            attemptDao = database.attemptDao()
        )
    } else {
        OfflineFirstPedagogicalReportRepository(
            api = apiClient.api,
            dao = database.reportDao()
        )
    }

    /** Sincronización completa del catálogo tras autenticarse. */
    val initialSync = InitialSync(api = apiClient.api, database = database)

    init {
        if (!localOnly) {
            appScope.launch {
                session.state
                    .mapNotNull { state ->
                        (state as? SessionState.Authenticated)?.session?.token
                    }
                    .distinctUntilChanged()
                    .collect { cacheMutex.withLock { initialSync.sync() } }
            }
        }

        appScope.launch { authRepository.bootstrap() }
    }

    /** Cierra la sesión. En modo local no borra la base de datos. */
    fun logout() {
        appScope.launch {
            authRepository.logout()
            if (!localOnly) {
                cacheMutex.withLock { database.clearAllTables() }
            }
        }
    }

    /** Restaura la semilla de demostración y deja la app sin sesión. */
    suspend fun resetLocalData() {
        authRepository.clearLocalSession()
        cacheMutex.withLock {
            database.clearAllTables()
            SeedLoader.load(appContext, database.openHelper.writableDatabase)
        }
    }
}
