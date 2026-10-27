package com.ccstudio.mpaz_pro_6.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.enums.ReportStatus
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.PedagogicalReportRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * Estado del panel de inicio del docente.
 *
 * Las unidades salen de las asignaciones del docente (`?docenteId`), y sus
 * asignaturas se resuelven sobre `GET /api/asignaturas` porque la unidad solo trae
 * el `asignaturaId`. Los cursos se obtienen con `GET /api/usuarios/{id}/cursos`.
 *
 * La actividad no guarda quién la creó, así que "actividades propias" se cuenta
 * como las actividades de las unidades asignadas al docente. Los informes se
 * filtran en cliente por `docenteId` y estado `PENDIENTE_VALIDACION`, porque el
 * backend solo admite los filtros `?estudianteId` y `?unidadId`.
 */
class HomeTeacherViewModel(
    private val userRepository: UserRepository,
    private val assignmentRepository: AssignmentRepository,
    private val unitRepository: UnitRepository,
    private val subjectRepository: SubjectRepository,
    private val activityRepository: ActivityRepository,
    private val pedagogicalReportRepository: PedagogicalReportRepository,
    private val teacherId: Int
) : ViewModel() {

    /** Indica que el panel se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Cursos en los que el docente tiene alguna asignación. */
    var cursos by mutableStateOf<List<Course>>(emptyList())
        private set

    /** Asignaciones del docente, de la más reciente a la más antigua. */
    var asignaciones by mutableStateOf<List<Assignment>>(emptyList())
        private set

    /** Unidades en las que el docente tiene alguna asignación. */
    var unidades by mutableStateOf<List<LearningUnit>>(emptyList())
        private set

    /** Asignaturas, para resolver el nombre que la unidad solo trae por id. */
    var asignaturas by mutableStateOf<List<Subject>>(emptyList())
        private set

    /** Actividades de las unidades asignadas al docente. */
    var actividades by mutableIntStateOf(0)
        private set

    /** Informes del docente pendientes de validación, del más antiguo al más reciente. */
    var informesPendientes by mutableStateOf<List<PedagogicalReport>>(emptyList())
        private set

    /** Estudiantes, para resolver el nombre que el informe solo trae por id. */
    var estudiantes by mutableStateOf<List<User>>(emptyList())
        private set

    /** Carga los datos del panel. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val asignacionesResult = assignmentRepository.list(teacherId = teacherId)

            if (asignacionesResult.isFailure) {
                error = asignacionesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val cursosResult = userRepository.listCourses(teacherId)

            if (cursosResult.isFailure) {
                error = cursosResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val usuariosResult = userRepository.list()

            if (usuariosResult.isFailure) {
                error = usuariosResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val unidadesResult = unitRepository.list()

            if (unidadesResult.isFailure) {
                error = unidadesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val asignaturasResult = subjectRepository.list()

            if (asignaturasResult.isFailure) {
                error = asignaturasResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val actividadesResult = activityRepository.list()

            if (actividadesResult.isFailure) {
                error = actividadesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val informesResult = pedagogicalReportRepository.list()

            if (informesResult.isFailure) {
                error = informesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val unidadIdsAsignadas = asignacionesResult.getOrDefault(emptyList())
                .map { it.unidadId }
                .toSet()

            asignaciones = asignacionesResult.getOrDefault(emptyList())
                .sortedByDescending { it.fecha }
            cursos = cursosResult.getOrDefault(emptyList())
            unidades = unidadesResult.getOrDefault(emptyList())
                .filter { it.id in unidadIdsAsignadas }
            asignaturas = asignaturasResult.getOrDefault(emptyList())
            estudiantes = usuariosResult.getOrDefault(emptyList())
                .filter { it.rol == UserRole.ESTUDIANTE }
            actividades = actividadesResult.getOrDefault(emptyList())
                .count { it.unidadId in unidadIdsAsignadas }
            informesPendientes = informesResult.getOrDefault(emptyList())
                .filter {
                    it.docenteId == teacherId &&
                        it.estado == ReportStatus.PENDING_VALIDATION
                }
                .sortedBy { it.fechaGeneracion }

            isLoading = false
        }
    }

    companion object {

        /** Factoría del ViewModel con los repositorios del panel. */
        fun factory(
            userRepository: UserRepository,
            assignmentRepository: AssignmentRepository,
            unitRepository: UnitRepository,
            subjectRepository: SubjectRepository,
            activityRepository: ActivityRepository,
            pedagogicalReportRepository: PedagogicalReportRepository,
            teacherId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeTeacherViewModel(
                    userRepository = userRepository,
                    assignmentRepository = assignmentRepository,
                    unitRepository = unitRepository,
                    subjectRepository = subjectRepository,
                    activityRepository = activityRepository,
                    pedagogicalReportRepository = pedagogicalReportRepository,
                    teacherId = teacherId
                )
            }
        }
    }
}
