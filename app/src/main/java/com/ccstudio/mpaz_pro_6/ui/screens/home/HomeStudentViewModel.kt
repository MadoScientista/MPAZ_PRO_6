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
import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.AttemptRepository
import com.ccstudio.mpaz_pro_6.data.repository.PedagogicalReportRepository
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
 *   tiene un intento completado.
 *
 * Los informes se cuentan como dato de apoyo: si esa consulta falla, el panel
 * conserva las unidades y actividades.
 */
class HomeStudentViewModel(
    private val assignmentRepository: AssignmentRepository,
    private val unitRepository: UnitRepository,
    private val subjectRepository: SubjectRepository,
    private val activityRepository: ActivityRepository,
    private val attemptRepository: AttemptRepository,
    private val pedagogicalReportRepository: PedagogicalReportRepository,
    private val studentId: Int,
    private val courseId: Int?
) : ViewModel() {

    /** Unidades publicadas y asignadas al curso del estudiante. */
    var unidades by mutableStateOf<List<LearningUnit>>(emptyList())
        private set

    /** Asignaturas, para resolver el nombre que la unidad solo trae por id. */
    var asignaturas by mutableStateOf<List<Subject>>(emptyList())
        private set

    /** Actividades disponibles que el estudiante todavía no completó. */
    var actividadesPendientes by mutableStateOf<List<Activity>>(emptyList())
        private set

    /** Actividades que tienen un intento en progreso. */
    var actividadesEnProgreso by mutableStateOf<Set<Int>>(emptySet())
        private set

    /** Cantidad de informes del estudiante. */
    var totalInformes by mutableIntStateOf(0)
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
                unidades = emptyList()
                asignaturas = emptyList()
                actividadesPendientes = emptyList()
                actividadesEnProgreso = emptySet()
                totalInformes = 0
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
            asignaturas = asignaturasResult.getOrDefault(emptyList())

            val unidadesVisiblesIds = unidadesVisibles.map { it.id }.toSet()

            actividadesPendientes = todasLasActividades.filter { activity ->
                activity.unidadId in unidadesVisiblesIds &&
                        activity.id in actividadesAsignadas &&
                        activity.id !in idsEnProgreso &&
                        activity.id !in actividadesCompletadas &&
                        run {
                            val maxIntentos = asignacionDePruebaFinal[activity.id]?.maxIntentos
                            maxIntentos == null ||
                                    (intentosPorActividad[activity.id] ?: 0) < maxIntentos
                        }
            }

            actividadesEnProgreso = idsEnProgreso

            // Dato de apoyo: un fallo de informes no oculta unidades ni actividades.
            totalInformes = pedagogicalReportRepository
                .list(studentId = studentId)
                .getOrDefault(emptyList())
                .size

            isLoading = false
        }
    }

    companion object {

        /** Factoría del panel con los repositorios y el contexto del estudiante. */
        fun factory(
            assignmentRepository: AssignmentRepository,
            unitRepository: UnitRepository,
            subjectRepository: SubjectRepository,
            activityRepository: ActivityRepository,
            attemptRepository: AttemptRepository,
            pedagogicalReportRepository: PedagogicalReportRepository,
            studentId: Int,
            courseId: Int?
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeStudentViewModel(
                    assignmentRepository = assignmentRepository,
                    unitRepository = unitRepository,
                    subjectRepository = subjectRepository,
                    activityRepository = activityRepository,
                    attemptRepository = attemptRepository,
                    pedagogicalReportRepository = pedagogicalReportRepository,
                    studentId = studentId,
                    courseId = courseId
                )
            }
        }
    }
}
