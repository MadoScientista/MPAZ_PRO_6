package com.ccstudio.mpaz_pro_6.ui.screens.assignments

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.LearningObjectiveRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * Estado del detalle de una asignación.
 *
 * Carga la asignación y resuelve el curso, la unidad y el docente, que la
 * asignación solo trae como identificadores. Después carga los catálogos de OA y
 * actividades de la unidad y las asociaciones de la asignación, para que el
 * docente pueda gestionarlas.
 *
 * La edición de reglas vive en `AssignmentFormViewModel`; este ViewModel se
 * encarga de las asociaciones y del borrado.
 */
class AssignmentDetailViewModel(
    private val assignmentRepository: AssignmentRepository,
    private val courseRepository: CourseRepository,
    private val unitRepository: UnitRepository,
    private val userRepository: UserRepository,
    private val learningObjectiveRepository: LearningObjectiveRepository,
    private val activityRepository: ActivityRepository,
    private val assignmentId: Int
) : ViewModel() {

    /** Asignación que se muestra. */
    var assignment by mutableStateOf<Assignment?>(null)
        private set

    /** Nombre del curso de la asignación. */
    var courseName by mutableStateOf("")
        private set

    /** Título de la unidad asignada. */
    var unitTitle by mutableStateOf("")
        private set

    /** Nombre del docente de la asignación. */
    var teacherName by mutableStateOf("")
        private set

    /** Objetivos de aprendizaje de la unidad, para asociar o desasociar. */
    var objectives by mutableStateOf<List<LearningObjective>>(emptyList())
        private set

    /** Actividades de la unidad, para asociar o desasociar. */
    var activities by mutableStateOf<List<Activity>>(emptyList())
        private set

    /** IDs de los objetivos asociados a la asignación. */
    var associatedObjectiveIds by mutableStateOf<Set<Int>>(emptySet())
        private set

    /** IDs de las actividades asociadas a la asignación. */
    var associatedActivityIds by mutableStateOf<Set<Int>>(emptySet())
        private set

    /** Indica que el detalle se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Indica que se está eliminando la asignación. */
    var isDeleting by mutableStateOf(false)
        private set

    /** Indica que se está guardando un cambio de asociación de OA. */
    var isSavingObjectives by mutableStateOf(false)
        private set

    /** Indica que se está guardando un cambio de asociación de actividades. */
    var isSavingActivities by mutableStateOf(false)
        private set

    /** Mensaje de la última operación de asociación o borrado. */
    var mensajeOperacion by mutableStateOf<String?>(null)
        private set

    /** Carga la asignación, sus nombres, sus catálogos y sus asociaciones. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null
            mensajeOperacion = null

            val assignmentResult = assignmentRepository.getById(assignmentId)

            if (assignmentResult.isFailure) {
                error = assignmentResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val assignment = assignmentResult.getOrThrow()
            this@AssignmentDetailViewModel.assignment = assignment

            val courseResult = courseRepository.getById(assignment.cursoId)

            if (courseResult.isFailure) {
                error = courseResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            courseName = courseResult.getOrThrow().nombre

            val unitResult = unitRepository.getById(assignment.unidadId)

            if (unitResult.isFailure) {
                error = unitResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            unitTitle = unitResult.getOrThrow().titulo

            val teacherResult = userRepository.getById(assignment.docenteId)

            if (teacherResult.isFailure) {
                error = teacherResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            teacherName = teacherResult.getOrThrow().nombre

            val assignedObjectivesResult =
                assignmentRepository.listObjectives(assignmentId)

            if (assignedObjectivesResult.isFailure) {
                error = assignedObjectivesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            associatedObjectiveIds = assignedObjectivesResult.getOrThrow()
                .map { it.oaId }
                .toSet()

            val allObjectivesResult =
                learningObjectiveRepository.list(unitId = assignment.unidadId)

            if (allObjectivesResult.isFailure) {
                error = allObjectivesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            objectives = allObjectivesResult.getOrThrow()

            val assignedActivitiesResult =
                assignmentRepository.listActivities(assignmentId)

            if (assignedActivitiesResult.isFailure) {
                error = assignedActivitiesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            associatedActivityIds = assignedActivitiesResult.getOrThrow()
                .map { it.actividadId }
                .toSet()

            val allActivitiesResult =
                activityRepository.list(unitId = assignment.unidadId)

            if (allActivitiesResult.isFailure) {
                error = allActivitiesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            activities = allActivitiesResult.getOrThrow()

            isLoading = false
        }
    }

    /**
     * Asocia o desasocia un objetivo de aprendizaje.
     *
     * El selector solo ofrece objetivos de la unidad; los errores de otra unidad
     * o de una asociación duplicada se muestran sin tocar el estado local.
     */
    fun alternarObjetivo(objectiveId: Int, isAssociated: Boolean) {
        if (isSavingObjectives) return

        viewModelScope.launch {
            isSavingObjectives = true
            mensajeOperacion = null

            val failure = if (isAssociated) {
                assignmentRepository
                    .addObjective(assignmentId, objectiveId)
                    .exceptionOrNull()
            } else {
                assignmentRepository
                    .removeObjective(assignmentId, objectiveId)
                    .exceptionOrNull()
            }

            isSavingObjectives = false

            if (failure != null) {
                mensajeOperacion = mensajeObjetivo(
                    error = failure as? ApiError,
                    isAssociated = isAssociated
                )
                return@launch
            }

            associatedObjectiveIds = if (isAssociated) {
                associatedObjectiveIds + objectiveId
            } else {
                associatedObjectiveIds - objectiveId
            }
        }
    }

    /** Asocia o desasocia una actividad. */
    fun alternarActividad(activityId: Int, isAssociated: Boolean) {
        if (isSavingActivities) return

        viewModelScope.launch {
            isSavingActivities = true
            mensajeOperacion = null

            val failure = if (isAssociated) {
                assignmentRepository
                    .addActivity(assignmentId, activityId)
                    .exceptionOrNull()
            } else {
                assignmentRepository
                    .removeActivity(assignmentId, activityId)
                    .exceptionOrNull()
            }

            isSavingActivities = false

            if (failure != null) {
                mensajeOperacion = mensajeActividad(
                    error = failure as? ApiError,
                    isAssociated = isAssociated
                )
                return@launch
            }

            associatedActivityIds = if (isAssociated) {
                associatedActivityIds + activityId
            } else {
                associatedActivityIds - activityId
            }
        }
    }

    /** Elimina la asignación y avisa cuando termina correctamente. */
    fun eliminar(onDeleted: () -> Unit) {
        if (isDeleting) return

        viewModelScope.launch {
            isDeleting = true
            mensajeOperacion = null

            val result = assignmentRepository.delete(assignmentId)

            isDeleting = false

            if (result.isFailure) {
                mensajeOperacion = mensajeEliminar(result.exceptionOrNull() as? ApiError)
                return@launch
            }

            onDeleted()
        }
    }

    /** Oculta el mensaje de la última operación. */
    fun limpiarMensaje() {
        mensajeOperacion = null
    }

    /** Traduce el fallo de una asociación de OA. */
    private fun mensajeObjetivo(error: ApiError?, isAssociated: Boolean): String = when (error) {
        is ApiError.BadRequest ->
            "Ese objetivo no pertenece a la unidad de la asignación."
        is ApiError.NotFound -> if (isAssociated) {
            "La asignación o el objetivo ya no existe."
        } else {
            "La asociación ya no existía."
        }
        is ApiError.Conflict -> "El objetivo ya estaba asociado."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Unknown -> error.message
        null -> "No se pudo actualizar la asociación."
    }

    /** Traduce el fallo de una asociación de actividad. */
    private fun mensajeActividad(error: ApiError?, isAssociated: Boolean): String = when (error) {
        is ApiError.BadRequest ->
            "La actividad no pertenece a la unidad o es una prueba final."
        is ApiError.NotFound -> if (isAssociated) {
            "La asignación o la actividad ya no existe."
        } else {
            "La asociación ya no existía."
        }
        is ApiError.Conflict -> "La actividad ya estaba asociada."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Unknown -> error.message
        null -> "No se pudo actualizar la asociación."
    }

    /** Traduce el fallo del borrado. */
    private fun mensajeEliminar(error: ApiError?): String = when (error) {
        is ApiError.Conflict ->
            "No se puede eliminar: tiene objetivos, actividades o intentos asociados."
        is ApiError.NotFound -> "La asignación ya no existe."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.BadRequest -> error.message
        is ApiError.Unknown -> error.message
        null -> "No se pudo eliminar la asignación."
    }

    companion object {

        /** Factoría del ViewModel con los repositorios del detalle. */
        fun factory(
            assignmentRepository: AssignmentRepository,
            courseRepository: CourseRepository,
            unitRepository: UnitRepository,
            userRepository: UserRepository,
            learningObjectiveRepository: LearningObjectiveRepository,
            activityRepository: ActivityRepository,
            assignmentId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AssignmentDetailViewModel(
                    assignmentRepository = assignmentRepository,
                    courseRepository = courseRepository,
                    unitRepository = unitRepository,
                    userRepository = userRepository,
                    learningObjectiveRepository = learningObjectiveRepository,
                    activityRepository = activityRepository,
                    assignmentId = assignmentId
                )
            }
        }
    }
}
