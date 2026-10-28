package com.ccstudio.mpaz_pro_6.ui.screens.activities

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
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.AttemptRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import com.ccstudio.mpaz_pro_6.ui.components.activity.StudentActivityStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Fila del listado de actividades del estudiante.
 *
 * Agrupa la actividad con el título de su unidad, el estado de realización y el
 * último intento completado, para que la pantalla no tenga que cruzar datos.
 */
data class StudentActivityItem(
    val activity: Activity,
    val unitTitle: String,
    val status: StudentActivityStatus,
    val attemptsCount: Int,
    val latestCompletedAttemptId: Int?,
    /** La prueba final ya se completó, pero quedan intentos disponibles. */
    val canRetry: Boolean = false
)

/**
 * Estado del listado de actividades del estudiante.
 *
 * Deriva las actividades de las asignaciones de su curso y las cruza con sus
 * intentos. Incluye las actividades completadas para que el estudiante pueda
 * volver a ver su resultado.
 */
class StudentActivityListViewModel(
    private val assignmentRepository: AssignmentRepository,
    private val unitRepository: UnitRepository,
    private val activityRepository: ActivityRepository,
    private val attemptRepository: AttemptRepository,
    private val studentId: Int,
    private val courseId: Int?,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
) : ViewModel() {

    /** Actividades disponibles con su estado. */
    var items by mutableStateOf<List<StudentActivityItem>>(emptyList())
        private set

    /** `true` cuando el estudiante no tiene curso asignado. */
    var sinCurso by mutableStateOf(false)
        private set

    /** Indica que el listado se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga las actividades del estudiante y las cruza con sus intentos. */
    fun cargar() {
        viewModelScope.launch(dispatcher) {
            isLoading = true
            error = null
            sinCurso = false

            if (courseId == null) {
                items = emptyList()
                sinCurso = true
                isLoading = false
                return@launch
            }

            val asignacionesResult = assignmentRepository.list(courseId = courseId)

            if (asignacionesResult.isFailure) {
                error = asignacionesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val unidadesResult = unitRepository.list()

            if (unidadesResult.isFailure) {
                error = unidadesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val actividadesResult = activityRepository.list()

            if (actividadesResult.isFailure) {
                error = actividadesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val asignaciones = asignacionesResult.getOrDefault(emptyList())
            val todasLasActividades = actividadesResult.getOrDefault(emptyList())
            val actividadesAsignadas = mutableSetOf<Int>()
            val asignacionDePruebaFinal = mutableMapOf<Int, Assignment>()

            asignaciones.forEach { assignment ->
                val enlacesResult = assignmentRepository.listActivities(assignment.id)

                if (enlacesResult.isFailure) {
                    error = enlacesResult.exceptionOrNull() as? ApiError
                    isLoading = false
                    return@launch
                }

                actividadesAsignadas += enlacesResult
                    .getOrDefault(emptyList())
                    .map { it.actividadId }

                if (assignment.pruebaFinalHabilitada) {
                    todasLasActividades
                        .firstOrNull {
                            it.unidadId == assignment.unidadId &&
                                    it.tipo == ActivityType.FINAL_TEST
                        }
                        ?.let { finalTest ->
                            actividadesAsignadas += finalTest.id
                            asignacionDePruebaFinal[finalTest.id] = assignment
                        }
                }
            }

            val intentosResult = attemptRepository.list(studentId = studentId)

            if (intentosResult.isFailure) {
                error = intentosResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val unidadesAsignadas = asignaciones.map { it.unidadId }.toSet()

            val unidadesVisibles = unidadesResult.getOrDefault(emptyList())
                .filter {
                    it.id in unidadesAsignadas &&
                            it.estado == ContentStatus.PUBLISHED
                }

            val unidadesVisiblesIds = unidadesVisibles.map { it.id }.toSet()
            val unitTitles = unidadesVisibles.associate { it.id to it.titulo }

            val intentosPorActividad = intentosResult.getOrDefault(emptyList())
                .groupBy { it.actividadId }

            items = todasLasActividades
                .filter {
                    it.id in actividadesAsignadas &&
                            it.unidadId in unidadesVisiblesIds
                }
                .map { activity ->
                    val intentos = intentosPorActividad[activity.id].orEmpty()
                    val enProgreso = intentos.any { it.fechaFin == null }
                    val ultimoCompletado = intentos
                        .filter { it.fechaFin != null }
                        .maxByOrNull { it.numero }

                    val status = when {
                        enProgreso -> StudentActivityStatus.IN_PROGRESS
                        ultimoCompletado != null -> StudentActivityStatus.COMPLETED
                        else -> StudentActivityStatus.PENDING
                    }

                    val maxIntentos = asignacionDePruebaFinal[activity.id]?.maxIntentos
                    val canRetry = activity.tipo == ActivityType.FINAL_TEST &&
                        ultimoCompletado != null &&
                        !enProgreso &&
                        (maxIntentos == null || intentos.size < maxIntentos)

                    StudentActivityItem(
                        activity = activity,
                        unitTitle = unitTitles[activity.unidadId] ?: "Sin unidad",
                        status = status,
                        attemptsCount = intentos.size,
                        latestCompletedAttemptId = ultimoCompletado?.id,
                        canRetry = canRetry
                    )
                }
                .sortedWith(
                    compareBy<StudentActivityItem> {
                        when (it.status) {
                            StudentActivityStatus.IN_PROGRESS -> 0
                            StudentActivityStatus.PENDING -> 1
                            StudentActivityStatus.COMPLETED -> 2
                        }
                    }
                        .thenBy { it.unitTitle }
                        .thenBy { it.activity.orden }
                )

            isLoading = false
        }
    }

    companion object {

        /** Factoría del listado con los repositorios y el contexto del estudiante. */
        fun factory(
            assignmentRepository: AssignmentRepository,
            unitRepository: UnitRepository,
            activityRepository: ActivityRepository,
            attemptRepository: AttemptRepository,
            studentId: Int,
            courseId: Int?
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                StudentActivityListViewModel(
                    assignmentRepository = assignmentRepository,
                    unitRepository = unitRepository,
                    activityRepository = activityRepository,
                    attemptRepository = attemptRepository,
                    studentId = studentId,
                    courseId = courseId
                )
            }
        }
    }
}
