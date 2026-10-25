package com.ccstudio.mpaz_pro_6.ui.screens.units

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentRepository
import com.ccstudio.mpaz_pro_6.data.repository.LearningObjectiveRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch

/**
 * Estado del detalle de una unidad.
 *
 * Consume `GET /api/unidades/{id}`. Para el estudiante agrega el material de la
 * unidad: objetivos de aprendizaje, contenidos publicados y actividades asignadas
 * a su curso. El docente y la UTP conservan la vista de gestión de la unidad.
 */
class UnitDetailViewModel(
    private val unitRepository: UnitRepository,
    private val subjectRepository: SubjectRepository,
    private val learningObjectiveRepository: LearningObjectiveRepository,
    private val contentRepository: ContentRepository,
    private val activityRepository: ActivityRepository,
    private val assignmentRepository: AssignmentRepository,
    private val rol: UserRole,
    private val courseId: Int?,
    private val unitId: Int
) : ViewModel() {

    /** Unidad consultada. */
    var unidad by mutableStateOf<LearningUnit?>(null)
        private set

    /** Nombre de la asignatura de la unidad. */
    var asignatura by mutableStateOf<Subject?>(null)
        private set

    /** Objetivos de aprendizaje de la unidad. */
    var objetivos by mutableStateOf<List<LearningObjective>>(emptyList())
        private set

    /** Contenidos publicados de los objetivos de la unidad. */
    var contenidos by mutableStateOf<List<Content>>(emptyList())
        private set

    /** Actividades asignadas al curso del estudiante, más la prueba final habilitada. */
    var actividades by mutableStateOf<List<Activity>>(emptyList())
        private set

    /** Indica que el detalle se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga la unidad y, para el estudiante, su material. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null
            objetivos = emptyList()
            contenidos = emptyList()
            actividades = emptyList()

            val unitResult = unitRepository.getById(unitId)

            if (unitResult.isFailure) {
                error = unitResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val unit = unitResult.getOrNull()

            if (unit == null) {
                error = ApiError.NotFound("La unidad ya no existe.")
                isLoading = false
                return@launch
            }

            unidad = unit

            val subjectsResult = subjectRepository.list()

            if (subjectsResult.isSuccess) {
                asignatura = subjectsResult.getOrDefault(emptyList())
                    .find { it.id == unit.asignaturaId }
            }

            if (rol != UserRole.ESTUDIANTE) {
                isLoading = false
                return@launch
            }

            val objetivosResult = learningObjectiveRepository.list(unitId = unitId)

            if (objetivosResult.isFailure) {
                error = objetivosResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val loadedObjetivos = objetivosResult.getOrDefault(emptyList())
            objetivos = loadedObjetivos

            val contenidosResult = contentRepository.list()

            if (contenidosResult.isFailure) {
                error = contenidosResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val oaIds = loadedObjetivos.map { it.id }.toSet()
            contenidos = contenidosResult.getOrDefault(emptyList())
                .filter {
                    it.oaId in oaIds &&
                            it.estado == ContentStatus.PUBLISHED
                }

            if (courseId == null) {
                actividades = emptyList()
                isLoading = false
                return@launch
            }

            val asignacionesResult = assignmentRepository.list(
                courseId = courseId,
                unitId = unitId
            )

            if (asignacionesResult.isFailure) {
                error = asignacionesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val assignment = asignacionesResult.getOrDefault(emptyList()).firstOrNull()

            if (assignment == null) {
                actividades = emptyList()
                isLoading = false
                return@launch
            }

            val enlacesResult = assignmentRepository.listActivities(assignment.id)

            if (enlacesResult.isFailure) {
                error = enlacesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val assignedIds = enlacesResult.getOrDefault(emptyList())
                .map { it.actividadId }
                .toMutableSet()

            val activitiesResult = activityRepository.list(unitId = unitId)

            if (activitiesResult.isFailure) {
                error = activitiesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val unitActivities = activitiesResult.getOrDefault(emptyList())

            if (assignment.pruebaFinalHabilitada) {
                unitActivities
                    .firstOrNull {
                        it.unidadId == unitId &&
                                it.tipo == ActivityType.FINAL_TEST
                    }
                    ?.let { assignedIds += it.id }
            }

            actividades = unitActivities.filter { it.id in assignedIds }
            isLoading = false
        }
    }

    companion object {

        /** Factoría del detalle con los repositorios y el contexto del rol. */
        fun factory(
            unitRepository: UnitRepository,
            subjectRepository: SubjectRepository,
            learningObjectiveRepository: LearningObjectiveRepository,
            contentRepository: ContentRepository,
            activityRepository: ActivityRepository,
            assignmentRepository: AssignmentRepository,
            rol: UserRole,
            courseId: Int?,
            unitId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UnitDetailViewModel(
                    unitRepository = unitRepository,
                    subjectRepository = subjectRepository,
                    learningObjectiveRepository = learningObjectiveRepository,
                    contentRepository = contentRepository,
                    activityRepository = activityRepository,
                    assignmentRepository = assignmentRepository,
                    rol = rol,
                    courseId = courseId,
                    unitId = unitId
                )
            }
        }
    }
}
