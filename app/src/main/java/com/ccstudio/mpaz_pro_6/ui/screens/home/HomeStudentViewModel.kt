package com.ccstudio.mpaz_pro_6.ui.screens.home

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
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.AttemptRepository
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch

/**
 * Estado del panel de inicio del estudiante.
 *
 * Deriva las unidades y actividades de las asignaciones de su curso:
 * - Unidades: publicadas y asignadas a [courseId].
 * - Actividades: las asociadas a cada asignación más la `PRUEBA_FINAL` de la
 *   unidad cuando la asignación la habilita.
 * - Pendientes: las actividades disponibles en las que el estudiante todavía no
 *   tiene un intento completado, más las pruebas finales completadas que aún
 *   admiten un nuevo intento.
 *
 * El estudiante no accede a informes pedagógicos.
 */
class HomeStudentViewModel(
    private val assignmentRepository: AssignmentRepository,
    private val unitRepository: UnitRepository,
    private val subjectRepository: SubjectRepository,
    private val courseRepository: CourseRepository,
    private val activityRepository: ActivityRepository,
    private val attemptRepository: AttemptRepository,
    private val studentId: Int,
    private val courseId: Int?
) : ViewModel() {

    /** Curso de contexto del estudiante. */
    var curso by mutableStateOf<Course?>(null)
        private set

    /** Unidades publicadas y asignadas al curso del estudiante. */
    var unidades by mutableStateOf<List<LearningUnit>>(emptyList())
        private set

    /** Asignaturas visibles con al menos una unidad publicada y asignada. */
    var asignaturas by mutableStateOf<List<Subject>>(emptyList())
        private set

    /** Cantidad de unidades visibles por asignatura. */
    var unidadesPorAsignatura by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    /** Actividades disponibles que el estudiante todavía no completó. */
    var actividadesPendientes by mutableStateOf<List<Activity>>(emptyList())
        private set

    /** Actividades que tienen un intento en progreso. */
    var actividadesEnProgreso by mutableStateOf<Set<Int>>(emptySet())
        private set

    /**
     * Pruebas finales completadas que todavía admiten un nuevo intento.
     *
     * El panel las vuelve a mostrar en pendientes con la acción “Repetir
     * prueba”; cuando se alcanza `maxIntentos` desaparecen.
     */
    var actividadesParaRepetir by mutableStateOf<Set<Int>>(emptySet())
        private set

    /** `true` cuando el estudiante no tiene curso asignado. */
    var sinCurso by mutableStateOf(false)
        private set

    /** Indica que el panel se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga el panel del estudiante. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null
            sinCurso = false

            if (courseId == null) {
                curso = null
                unidades = emptyList()
                asignaturas = emptyList()
                unidadesPorAsignatura = emptyMap()
                actividadesPendientes = emptyList()
                actividadesEnProgreso = emptySet()
                actividadesParaRepetir = emptySet()
                sinCurso = true
                isLoading = false
                return@launch
            }

            // El curso es contexto: si falla, el resto del panel sigue visible.
            curso = courseRepository.getById(courseId).getOrNull()

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

            val asignaciones = asignacionesResult.getOrDefault(emptyList())
            val todasLasActividades = actividadesResult.getOrDefault(emptyList())
            val actividadesAsignadas = mutableSetOf<Int>()
            val asignacionDePruebaFinal = mutableMapOf<Int, Assignment>()

            asignaciones.forEach { assignment ->
                val asociadasResult =
                    assignmentRepository.listActivities(assignment.id)

                if (asociadasResult.isFailure) {
                    error = asociadasResult.exceptionOrNull() as? ApiError
                    isLoading = false
                    return@launch
                }

                actividadesAsignadas += asociadasResult
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

            val intentos = intentosResult.getOrDefault(emptyList())
            val actividadesCompletadas = intentos
                .filter { it.fechaFin != null }
                .map { it.actividadId }
                .toSet()
            val idsEnProgreso = intentos
                .filter { it.fechaFin == null }
                .map { it.actividadId }
                .toSet()
            val intentosPorActividad = intentos.groupingBy { it.actividadId }.eachCount()

            val unidadesAsignadas = asignaciones.map { it.unidadId }.toSet()

            val unidadesVisibles = unidadesResult.getOrDefault(emptyList())
                .filter {
                    it.id in unidadesAsignadas &&
                            it.estado == ContentStatus.PUBLISHED
                }

            unidades = unidadesVisibles

            val subjectIds = unidadesVisibles.map { it.asignaturaId }.toSet()
            asignaturas = asignaturasResult.getOrDefault(emptyList())
                .filter { it.id in subjectIds }
                .sortedBy { it.nombre.lowercase() }
            unidadesPorAsignatura = unidadesVisibles
                .groupingBy { it.asignaturaId }
                .eachCount()

            val unidadesVisiblesIds = unidadesVisibles.map { it.id }.toSet()

            fun puedeRepetirPruebaFinal(activity: Activity): Boolean {
                if (activity.tipo != ActivityType.FINAL_TEST ||
                    activity.id !in actividadesCompletadas ||
                    activity.id in idsEnProgreso
                ) {
                    return false
                }

                val maxIntentos = asignacionDePruebaFinal[activity.id]?.maxIntentos
                    ?: return true
                return (intentosPorActividad[activity.id] ?: 0) < maxIntentos
            }

            actividadesPendientes = todasLasActividades.filter { activity ->
                activity.unidadId in unidadesVisiblesIds && when {
                    activity.id !in actividadesAsignadas -> false
                    activity.id in idsEnProgreso -> true
                    activity.id in actividadesCompletadas ->
                        puedeRepetirPruebaFinal(activity)

                    else -> {
                        val maxIntentos = asignacionDePruebaFinal[activity.id]?.maxIntentos
                        maxIntentos == null ||
                                (intentosPorActividad[activity.id] ?: 0) < maxIntentos
                    }
                }
            }
            actividadesEnProgreso = idsEnProgreso
            actividadesParaRepetir = todasLasActividades
                .filter { it.id in unidadesVisiblesIds && puedeRepetirPruebaFinal(it) }
                .map { it.id }
                .toSet()

            isLoading = false
        }
    }

    companion object {

        /** Factoría del panel con los repositorios y el contexto del estudiante. */
        fun factory(
            assignmentRepository: AssignmentRepository,
            unitRepository: UnitRepository,
            subjectRepository: SubjectRepository,
            courseRepository: CourseRepository,
            activityRepository: ActivityRepository,
            attemptRepository: AttemptRepository,
            studentId: Int,
            courseId: Int?
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeStudentViewModel(
                    assignmentRepository = assignmentRepository,
                    unitRepository = unitRepository,
                    subjectRepository = subjectRepository,
                    courseRepository = courseRepository,
                    activityRepository = activityRepository,
                    attemptRepository = attemptRepository,
                    studentId = studentId,
                    courseId = courseId
                )
            }
        }
    }
}
