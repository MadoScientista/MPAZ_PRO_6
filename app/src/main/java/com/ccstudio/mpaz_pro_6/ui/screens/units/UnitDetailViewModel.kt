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
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.AttemptRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentRepository
import com.ccstudio.mpaz_pro_6.data.repository.ContentResourceRepository
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.LearningObjectiveRepository
import com.ccstudio.mpaz_pro_6.data.repository.QuestionRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * Estado del detalle de una unidad.
 *
 * Consume `GET /api/unidades/{id}` y resuelve el material de la unidad:
 * objetivos, contenidos, actividades, cantidad de recursos por contenido y
 * cantidad de preguntas por actividad.
 *
 * Para la UTP y el docente se muestra todo el material de la unidad. Para el
 * estudiante se muestran solo los contenidos publicados y las actividades
 * asignadas a su curso, más la prueba final habilitada.
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
    private val unitId: Int,
    private val courseRepository: CourseRepository? = null,
    private val teacherId: Int? = null,
    private val attemptRepository: AttemptRepository? = null,
    private val studentId: Int? = null
) : ViewModel() {

    /** Unidad consultada. */
    var unidad by mutableStateOf<LearningUnit?>(null)
        private set

    /** Asignatura de la unidad. */
    var asignatura by mutableStateOf<Subject?>(null)
        private set

    /** Curso opcional donde se consulta la unidad. */
    var course by mutableStateOf<Course?>(null)
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

    /** Cantidad de actividades visibles asociadas a cada contenido. */
    var actividadesPorContenido by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    /** Intentos máximos de la prueba final (tomados de la asignación de la unidad). */
    var maxIntentosPruebaFinal by mutableStateOf<Int?>(null)
        private set

    /** Fecha de aplicación de la prueba final (tomada de la asignación de la unidad). */
    var fechaPruebaFinal by mutableStateOf<Instant?>(null)
        private set

    /** Indica si la asignación habilita la prueba final; `null` si no se conoce. */
    var pruebaFinalHabilitada by mutableStateOf<Boolean?>(null)
        private set

    /** Id de la asignación que gobierna la prueba final, para abrir su configuración. */
    var asignacionPruebaFinalId by mutableStateOf<Int?>(null)
        private set

    /** Cantidad de intentos del estudiante en la prueba final de esta unidad. */
    var intentosPruebaFinalCount by mutableStateOf<Int?>(null)
        private set

    /** `true` cuando el estudiante tiene prueba final en la unidad pero está deshabilitada. */
    var pruebaFinalDeshabilitada by mutableStateOf(false)
        private set

    /** Indica que el detalle se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
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
            maxIntentosPruebaFinal = null
            fechaPruebaFinal = null
            pruebaFinalHabilitada = null
            asignacionPruebaFinalId = null
            intentosPruebaFinalCount = null
            pruebaFinalDeshabilitada = false

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

            if (courseId != null && courseRepository != null) {
                val courseResult = courseRepository.getById(courseId)
                if (courseResult.isSuccess) {
                    course = courseResult.getOrNull()
                }
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

            val contentsResult = contentRepository.list()

            if (contentsResult.isFailure) {
                error = contentsResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val allContents = contentsResult.getOrDefault(emptyList())
                .filter { it.unidadId == unitId && it.oaId in objectiveIds }

            val visibleContents = if (rol == UserRole.ESTUDIANTE) {
                allContents.filter { it.estado == ContentStatus.PUBLISHED || unit.estado == ContentStatus.PUBLISHED }
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

            // Las reglas de la prueba final (fecha, intentos y habilitación) viven
            // en la asignación de la unidad. Para la UTP y el docente es
            // información complementaria: si falla, la tarjeta muestra los valores
            // sin definir en lugar de ocultar el material de la unidad.
            val assignmentsResult = if (rol == UserRole.ESTUDIANTE && courseId == null) {
                null
            } else {
                assignmentRepository.list(
                    courseId = courseId,
                    unitId = unitId,
                    teacherId = if (rol == UserRole.DOCENTE) teacherId else null
                )
            }

            if (assignmentsResult != null && assignmentsResult.isFailure &&
                rol == UserRole.ESTUDIANTE
            ) {
                error = assignmentsResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val assignment = assignmentsResult
                ?.getOrDefault(emptyList())
                ?.firstOrNull { it.unidadId == unitId }

            asignacionPruebaFinalId = assignment?.id
            fechaPruebaFinal = assignment?.fecha
            maxIntentosPruebaFinal = assignment?.maxIntentos
            pruebaFinalHabilitada = assignment?.pruebaFinalHabilitada

            val finalTest = unitActivities.firstOrNull {
                it.unidadId == unitId && it.tipo == ActivityType.FINAL_TEST
            }

            if (rol == UserRole.ESTUDIANTE && studentId != null && finalTest != null && attemptRepository != null) {
                val attemptsRes = attemptRepository.list(studentId = studentId, activityId = finalTest.id)
                if (attemptsRes.isSuccess) {
                    intentosPruebaFinalCount = attemptsRes.getOrDefault(emptyList()).size
                }
            }

            pruebaFinalDeshabilitada = rol == UserRole.ESTUDIANTE &&
                courseId != null &&
                finalTest != null &&
                assignment?.pruebaFinalHabilitada != true

            val visibleActivities = if (rol == UserRole.ESTUDIANTE) {
                actividadesParaEstudiante(unitActivities, assignment)
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

            // Solo se cuentan las actividades visibles para el rol.
            actividadesPorContenido = actividades
                .mapNotNull { it.contenidoId }
                .groupingBy { it }
                .eachCount()

            isLoading = false
        }
    }

    /**
     * Resuelve las actividades visibles para el estudiante: las asociadas a la
     * asignación de su curso más la prueba final habilitada.
     */
    private suspend fun actividadesParaEstudiante(
        unitActivities: List<Activity>,
        assignment: Assignment?
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

        // La prueba final no se asocia como actividad regular: si una base
        // antigua o la semilla dejó ese vínculo, se ignora. Solo se muestra
        // cuando la asignación la habilita.
        val finalTest = unitActivities.firstOrNull {
            it.unidadId == unitId && it.tipo == ActivityType.FINAL_TEST
        }

        finalTest?.let { assignedIds -= it.id }

        if (assignment.pruebaFinalHabilitada) {
            finalTest?.let { assignedIds += it.id }
        }

        return unitActivities.filter { it.id in assignedIds }
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
            unitId: Int,
            courseRepository: CourseRepository? = null,
            teacherId: Int? = null,
            attemptRepository: AttemptRepository? = null,
            studentId: Int? = null
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
                    unitId = unitId,
                    courseRepository = courseRepository,
                    teacherId = teacherId,
                    attemptRepository = attemptRepository,
                    studentId = studentId
                )
            }
        }
    }
}
