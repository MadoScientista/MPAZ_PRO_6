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
import com.ccstudio.mpaz_pro_6.data.model.Assignment
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
 * Estado del listado de unidades.
 *
 * Cambia su contenido según el `UserRole`: la UTP ve solo las unidades
 * `EN_REVISION` que esperan su revisión (el `BORRADOR` pertenece al docente), el
 * docente las `BORRADOR`, `AUTORIZADO` y `PUBLICADO`, y el estudiante solo las
 * `PUBLICADO` que además estén asignadas a su curso.
 *
 * También resuelve el resumen de cada unidad: cantidad de contenidos, cantidad de
 * actividades y si tiene prueba final. Para el estudiante se cuentan solo los
 * contenidos publicados y las actividades asignadas a su curso.
 *
 * La búsqueda por texto libre es **client-side**: el backend no la expone.
 */
class UnitListViewModel(
    private val unitRepository: UnitRepository,
    private val subjectRepository: SubjectRepository,
    private val assignmentRepository: AssignmentRepository,
    private val learningObjectiveRepository: LearningObjectiveRepository,
    private val contentRepository: ContentRepository,
    private val activityRepository: ActivityRepository,
    private val rol: UserRole,
    private val courseId: Int?
) : ViewModel() {

    /** Unidades visibles según el rol del usuario. */
    var unidades by mutableStateOf<List<LearningUnit>>(emptyList())
        private set

    /** Asignaturas, para resolver el nombre que la unidad solo trae por id. */
    var asignaturas by mutableStateOf<List<Subject>>(emptyList())
        private set

    /** Cantidad de contenidos visibles de cada unidad. */
    var contenidosPorUnidad by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    /** Cantidad de actividades visibles de cada unidad. */
    var actividadesPorUnidad by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    /** Indica si cada unidad cuenta con una prueba final visible. */
    var pruebaFinalPorUnidad by mutableStateOf<Map<Int, Boolean>>(emptyMap())
        private set

    /** `true` cuando el estudiante no tiene curso y no puede ver unidades. */
    var sinCurso by mutableStateOf(false)
        private set

    /** Indica que el listado se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga las unidades, las asignaturas y el resumen de cada unidad. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null
            sinCurso = false

            // Sin curso, el estudiante no tiene unidades asignadas que mostrar.
            if (rol == UserRole.ESTUDIANTE && courseId == null) {
                unidades = emptyList()
                asignaturas = emptyList()
                contenidosPorUnidad = emptyMap()
                actividadesPorUnidad = emptyMap()
                pruebaFinalPorUnidad = emptyMap()
                sinCurso = true
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

            val asignaciones: List<Assignment> = if (rol == UserRole.ESTUDIANTE) {
                val asignacionesResult = assignmentRepository.list(courseId = courseId)

                if (asignacionesResult.isFailure) {
                    error = asignacionesResult.exceptionOrNull() as? ApiError
                    isLoading = false
                    return@launch
                }

                asignacionesResult.getOrDefault(emptyList())
            } else {
                emptyList()
            }

            val unidadesAsignadas: Set<Int>? = if (rol == UserRole.ESTUDIANTE) {
                asignaciones.map { it.unidadId }.toSet()
            } else {
                null
            }

            unidades = unidadesResult.getOrDefault(emptyList())
                .filter { unit ->
                    esVisiblePara(unit) &&
                            (unidadesAsignadas == null || unit.id in unidadesAsignadas)
                }

            asignaturas = asignaturasResult.getOrDefault(emptyList())

            cargarResumen(asignaciones)
            isLoading = false
        }
    }

    /**
     * Resuelve los conteos de contenidos, actividades y prueba final.
     *
     * Las consultas son complementarias: si alguna falla, la tarjeta conserva
     * valores en cero en lugar de ocultar el listado.
     */
    private suspend fun cargarResumen(asignaciones: List<Assignment>) {
        val objetivos = learningObjectiveRepository.list()
            .getOrDefault(emptyList())
        val contenidos = contentRepository.list()
            .getOrDefault(emptyList())
        val actividades = activityRepository.list()
            .getOrDefault(emptyList())

        val unidadPorObjetivo = objetivos.associate { it.id to it.unidadId }

        val contenidosVisibles = if (rol == UserRole.ESTUDIANTE) {
            contenidos.filter { it.estado == ContentStatus.PUBLISHED }
        } else {
            contenidos
        }

        contenidosPorUnidad = contenidosVisibles
            .mapNotNull { contenido ->
                unidadPorObjetivo[contenido.oaId]?.let { unidadId ->
                    unidadId to contenido
                }
            }
            .groupingBy { it.first }
            .eachCount()

        if (rol == UserRole.ESTUDIANTE) {
            val actividadesVisibles = mutableMapOf<Int, Int>()
            val pruebaFinalVisible = mutableMapOf<Int, Boolean>()

            asignaciones.forEach { assignment ->
                val enlaces = assignmentRepository.listActivities(assignment.id)
                    .getOrDefault(emptyList())

                val ids = enlaces.map { it.actividadId }.toMutableSet()

                val finalTest = actividades.firstOrNull {
                    it.unidadId == assignment.unidadId &&
                            it.tipo == ActivityType.FINAL_TEST
                }

                if (assignment.pruebaFinalHabilitada && finalTest != null) {
                    ids += finalTest.id
                }

                actividadesVisibles[assignment.unidadId] =
                    actividades.count { it.id in ids }
                pruebaFinalVisible[assignment.unidadId] =
                    assignment.pruebaFinalHabilitada && finalTest != null
            }

            actividadesPorUnidad = actividadesVisibles
            pruebaFinalPorUnidad = pruebaFinalVisible
        } else {
            actividadesPorUnidad = actividades
                .groupingBy { it.unidadId }
                .eachCount()

            pruebaFinalPorUnidad = actividades
                .groupBy { it.unidadId }
                .mapValues { (_, actividadesUnidad) ->
                    actividadesUnidad.any { it.tipo == ActivityType.FINAL_TEST }
                }
        }
    }

    /**
     * Indica si la unidad debe mostrarse al usuario con el rol de la pantalla.
     *
     * La UTP solo revisa unidades `EN_REVISION`: un `BORRADOR` sigue siendo
     * material de trabajo del docente y solo ese rol lo ve.
     */
    private fun esVisiblePara(unit: LearningUnit): Boolean = when (rol) {
        UserRole.UTP -> unit.estado == ContentStatus.IN_REVIEW
        UserRole.DOCENTE -> unit.estado == ContentStatus.DRAFT ||
                unit.estado == ContentStatus.AUTHORIZED ||
                unit.estado == ContentStatus.PUBLISHED
        UserRole.ESTUDIANTE -> unit.estado == ContentStatus.PUBLISHED
    }

    companion object {

        /** Factoría del ViewModel con los repositorios del listado. */
        fun factory(
            unitRepository: UnitRepository,
            subjectRepository: SubjectRepository,
            assignmentRepository: AssignmentRepository,
            learningObjectiveRepository: LearningObjectiveRepository,
            contentRepository: ContentRepository,
            activityRepository: ActivityRepository,
            rol: UserRole,
            courseId: Int?
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UnitListViewModel(
                    unitRepository = unitRepository,
                    subjectRepository = subjectRepository,
                    assignmentRepository = assignmentRepository,
                    learningObjectiveRepository = learningObjectiveRepository,
                    contentRepository = contentRepository,
                    activityRepository = activityRepository,
                    rol = rol,
                    courseId = courseId
                )
            }
        }
    }
}
