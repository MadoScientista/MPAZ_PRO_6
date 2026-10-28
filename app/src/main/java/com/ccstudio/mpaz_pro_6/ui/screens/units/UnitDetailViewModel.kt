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
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentResourceRepository
import com.ccstudio.mpaz_pro_6.data.repository.LearningObjectiveRepository
import com.ccstudio.mpaz_pro_6.data.repository.QuestionRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch

/**
 * Estado del detalle de una unidad.
 *
 * Consume `GET /api/unidades/{id}` y resuelve el material de la unidad:
 * objetivos, contenidos, actividades visibles, cantidad de recursos por contenido
 * y cantidad de actividades por contenido.
 *
 * Para la UTP y el docente se muestra todo el material de la unidad. Para el
 * estudiante se muestran solo los contenidos publicados de los OAs asociados a
 * su asignación y las actividades asociadas a esa asignación, más la prueba
 * final habilitada. Las actividades regulares se abren desde el detalle del
 * contenido; la prueba final se queda en este detalle de unidad.
 */
class UnitDetailViewModel(
    private val unitRepository: UnitRepository,
    private val subjectRepository: SubjectRepository,
    private val learningObjectiveRepository: LearningObjectiveRepository,
    private val contentRepository: ContentRepository,
    private val contentResourceRepository: ContentResourceRepository,
    private val activityRepository: ActivityRepository,
    private val questionRepository: QuestionRepository,
    private val assignmentRepository: AssignmentRepository,
    private val rol: UserRole,
    private val courseId: Int?,
    private val unitId: Int
) : ViewModel() {

    /** Unidad consultada. */
    var unidad by mutableStateOf<LearningUnit?>(null)
        private set

    /** Asignatura de la unidad. */
    var asignatura by mutableStateOf<Subject?>(null)
        private set

    /** Objetivos de aprendizaje de la unidad. */
    var objetivos by mutableStateOf<List<LearningObjective>>(emptyList())
        private set

    /** Contenidos visibles de los objetivos de la unidad. */
    var contenidos by mutableStateOf<List<Content>>(emptyList())
        private set

    /** Actividades visibles de la unidad. */
    var actividades by mutableStateOf<List<Activity>>(emptyList())
        private set

    /** Cantidad de preguntas por actividad. */
    var preguntasPorActividad by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    /** Cantidad de recursos por contenido. */
    var recursosPorContenido by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    /** Cantidad de actividades regulares por contenido. */
    var actividadesPorContenido by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    /** Indica que el detalle se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Indica que se está enviando la unidad a revisión. */
    var isSendingReview by mutableStateOf(false)
        private set

    /** Mensaje de la última operación de revisión. */
    var mensajeRevision by mutableStateOf<String?>(null)
        private set

    /** Carga la unidad y su material según el rol. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null
            objetivos = emptyList()
            contenidos = emptyList()
            actividades = emptyList()
            preguntasPorActividad = emptyMap()
            recursosPorContenido = emptyMap()
            actividadesPorContenido = emptyMap()

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

            val objectivesResult = learningObjectiveRepository.list(unitId)

            if (objectivesResult.isFailure) {
                error = objectivesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val unitObjectives = objectivesResult.getOrDefault(emptyList())
                .filter { it.unidadId == unitId }
                .sortedWith(compareBy({ it.codigo.orEmpty() }, { it.id }))

            objetivos = unitObjectives
            val objectiveIds = unitObjectives.map { it.id }.toSet()

            val assignment = if (rol == UserRole.ESTUDIANTE) {
                cargarAsignacionEstudiante()
            } else {
                null
            }
            if (rol == UserRole.ESTUDIANTE && error != null) {
                isLoading = false
                return@launch
            }

            val assignedObjectiveIds = if (rol == UserRole.ESTUDIANTE && assignment != null) {
                val linksResult = assignmentRepository.listObjectives(assignment.id)
                if (linksResult.isFailure) {
                    error = linksResult.exceptionOrNull() as? ApiError
                    isLoading = false
                    return@launch
                }
                linksResult.getOrDefault(emptyList()).map { it.oaId }.toSet()
            } else {
                emptySet()
            }

            val contentsResult = contentRepository.list()

            if (contentsResult.isFailure) {
                error = contentsResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val allContents = contentsResult.getOrDefault(emptyList())
                .filter { it.oaId in objectiveIds }

            val visibleContents = if (rol == UserRole.ESTUDIANTE) {
                allContents.filter {
                    it.estado == ContentStatus.PUBLISHED &&
                        it.oaId in assignedObjectiveIds
                }
            } else {
                allContents
            }

            contenidos = visibleContents.sortedWith(compareBy({ it.orden }, { it.id }))

            val activitiesResult = activityRepository.list(unitId)

            if (activitiesResult.isFailure) {
                error = activitiesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val unitActivities = activitiesResult.getOrDefault(emptyList())
                .filter { it.unidadId == unitId }

            val visibleActivities = if (rol == UserRole.ESTUDIANTE) {
                actividadesParaEstudiante(assignment, unitActivities)
            } else {
                unitActivities
            }

            if (error != null) {
                isLoading = false
                return@launch
            }

            actividades = visibleActivities
                .sortedWith(compareBy({ it.orden }, { it.id }))

            // Los conteos son complementarios: si fallan, se muestran en cero.
            val activityIds = actividades.map { it.id }.toSet()
            val questionsResult = questionRepository.list()

            preguntasPorActividad = if (questionsResult.isSuccess) {
                questionsResult.getOrDefault(emptyList())
                    .filter { it.actividadId in activityIds }
                    .groupingBy { it.actividadId }
                    .eachCount()
            } else {
                emptyMap()
            }

            actividadesPorContenido = actividades
                .filter { it.tipo != ActivityType.FINAL_TEST }
                .mapNotNull { it.contenidoId }
                .groupingBy { it }
                .eachCount()

            val contentIds = contenidos.map { it.id }.toSet()
            val resourcesResult = contentResourceRepository.list()

            recursosPorContenido = if (resourcesResult.isSuccess) {
                resourcesResult.getOrDefault(emptyList())
                    .filter { it.contenidoId in contentIds }
                    .groupingBy { it.contenidoId }
                    .eachCount()
            } else {
                emptyMap()
            }

            isLoading = false
        }
    }

    /** Carga la asignación del curso del estudiante para esta unidad. */
    private suspend fun cargarAsignacionEstudiante(): Assignment? {
        val idCurso = courseId ?: return null

        val assignmentsResult = assignmentRepository.list(
            courseId = idCurso,
            unitId = unitId
        )

        if (assignmentsResult.isFailure) {
            error = assignmentsResult.exceptionOrNull() as? ApiError
            return null
        }

        return assignmentsResult.getOrDefault(emptyList()).firstOrNull()
    }

    /**
     * Resuelve las actividades visibles para el estudiante: las asociadas a la
     * asignación de su curso más la prueba final habilitada.
     */
    private suspend fun actividadesParaEstudiante(
        assignment: Assignment?,
        unitActivities: List<Activity>
    ): List<Activity> {
        if (assignment == null) return emptyList()

        val linksResult = assignmentRepository.listActivities(assignment.id)

        if (linksResult.isFailure) {
            error = linksResult.exceptionOrNull() as? ApiError
            return emptyList()
        }

        val assignedIds = linksResult.getOrDefault(emptyList())
            .map { it.actividadId }
            .toMutableSet()

        if (assignment.pruebaFinalHabilitada) {
            unitActivities
                .firstOrNull {
                    it.unidadId == unitId &&
                            it.tipo == ActivityType.FINAL_TEST
                }
                ?.let { assignedIds += it.id }
        }

        return unitActivities.filter { it.id in assignedIds }
    }

    /** Envía la unidad en borrador a revisión de la UTP. */
    fun enviarARevision() {
        val current = unidad ?: return
        if (isSendingReview) return

        viewModelScope.launch {
            isSendingReview = true
            mensajeRevision = null
            error = null

            val result = unitRepository.sendToReview(current.id)
            isSendingReview = false

            if (result.isFailure) {
                error = result.exceptionOrNull() as? ApiError
                return@launch
            }

            unidad = result.getOrNull() ?: current
            mensajeRevision = "Unidad enviada a revisión."
        }
    }

    companion object {

        /** Factoría del detalle con los repositorios y el contexto del rol. */
        fun factory(
            unitRepository: UnitRepository,
            subjectRepository: SubjectRepository,
            learningObjectiveRepository: LearningObjectiveRepository,
            contentRepository: ContentRepository,
            contentResourceRepository: ContentResourceRepository,
            activityRepository: ActivityRepository,
            questionRepository: QuestionRepository,
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
                    contentResourceRepository = contentResourceRepository,
                    activityRepository = activityRepository,
                    questionRepository = questionRepository,
                    assignmentRepository = assignmentRepository,
                    rol = rol,
                    courseId = courseId,
                    unitId = unitId
                )
            }
        }
    }
}
