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
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.DocenciaRepository
import com.ccstudio.mpaz_pro_6.data.repository.PedagogicalReportRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Estado del panel de inicio del docente.
 *
 * El docente trabaja por asignatura. Las asignaturas y los cursos salen de sus
 * docencias; las unidades se obtienen del catálogo de esas asignaturas y las
 * actividades se cuentan solo sobre las unidades que tienen una asignación en
 * alguno de sus cursos.
 *
 * No se consulta `/api/usuarios`: el nombre del estudiante llega en la
 * respuesta de los informes. El Home muestra los informes pendientes del
 * docente autenticado.
 */
class HomeTeacherViewModel(
    private val docenciaRepository: DocenciaRepository,
    private val courseRepository: CourseRepository,
    private val assignmentRepository: AssignmentRepository,
    private val unitRepository: UnitRepository,
    private val activityRepository: ActivityRepository,
    private val pedagogicalReportRepository: PedagogicalReportRepository,
    private val teacherId: Int,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
) : ViewModel() {

    /** Indica que el panel se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Cursos en los que el docente tiene alguna docencia. */
    var cursos by mutableStateOf<List<Course>>(emptyList())
        private set

    /** Asignaturas que dicta el docente, sin repetir curso. */
    var asignaturas by mutableStateOf<List<Subject>>(emptyList())
        private set

    /** Nombres de los cursos en los que se dicta cada asignatura. */
    var cursosPorAsignatura by mutableStateOf<Map<Int, List<String>>>(emptyMap())
        private set

    /** Cantidad de unidades del catálogo por asignatura. */
    var unidadesPorAsignatura by mutableStateOf<Map<Int, Int>>(emptyMap())
        private set

    /** Actividades de las unidades que el docente tiene asignadas. */
    var actividades by mutableIntStateOf(0)
        private set

    /** Informes del docente pendientes de validación. */
    var informesPendientes by mutableStateOf<List<PedagogicalReport>>(emptyList())
        private set

    /** Carga docencias, cursos, unidades, asignaciones y actividades. */
    fun cargar() {
        viewModelScope.launch(dispatcher) {
            isLoading = true
            error = null

            val docenciasResult = docenciaRepository.list(teacherId)
            if (docenciasResult.isFailure) {
                error = docenciasResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val cursosResult = courseRepository.list()
            if (cursosResult.isFailure) {
                error = cursosResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val unidadesResult = unitRepository.list()
            if (unidadesResult.isFailure) {
                error = unidadesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val asignacionesResult = assignmentRepository.list()
            if (asignacionesResult.isFailure) {
                error = asignacionesResult.exceptionOrNull() as? ApiError
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

            val docencias = docenciasResult.getOrDefault(emptyList())
            val courseIds = docencias.map { it.cursoId }.toSet()
            val subjectIds = docencias.map { it.asignaturaId }.toSet()
            val cursosPorId = cursosResult.getOrDefault(emptyList())
                .filter { it.id in courseIds }
                .associateBy { it.id }
            val unidades = unidadesResult.getOrDefault(emptyList())
                .filter { it.asignaturaId in subjectIds }
            val unidadesPorId = unidades.associateBy { it.id }
            val docenciaKeys = docencias.map { it.cursoId to it.asignaturaId }.toSet()

            val asignacionesDocente = asignacionesResult.getOrDefault(emptyList())
                .filter { assignment ->
                    val unidad = unidadesPorId[assignment.unidadId] ?: return@filter false
                    (assignment.cursoId to unidad.asignaturaId) in docenciaKeys
                }
            val unidadesAsignadas = asignacionesDocente.map { it.unidadId }.toSet()

            cursos = cursosPorId.values.sortedWith(compareBy({ it.anio }, { it.nombre }))
            asignaturas = docencias
                .distinctBy { it.asignaturaId }
                .map { Subject(id = it.asignaturaId, nombre = it.asignaturaNombre) }
                .sortedBy { it.nombre }
            cursosPorAsignatura = docencias
                .groupBy { it.asignaturaId }
                .mapValues { (_, items) ->
                    items.mapNotNull { cursosPorId[it.cursoId]?.nombre }
                        .distinct()
                        .sorted()
                }
            unidadesPorAsignatura = unidades
                .groupingBy { it.asignaturaId }
                .eachCount()
            actividades = actividadesResult.getOrDefault(emptyList())
                .count { it.unidadId in unidadesAsignadas }
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
            docenciaRepository: DocenciaRepository,
            courseRepository: CourseRepository,
            assignmentRepository: AssignmentRepository,
            unitRepository: UnitRepository,
            activityRepository: ActivityRepository,
            pedagogicalReportRepository: PedagogicalReportRepository,
            teacherId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeTeacherViewModel(
                    docenciaRepository = docenciaRepository,
                    courseRepository = courseRepository,
                    assignmentRepository = assignmentRepository,
                    unitRepository = unitRepository,
                    activityRepository = activityRepository,
                    pedagogicalReportRepository = pedagogicalReportRepository,
                    teacherId = teacherId
                )
            }
        }
    }
}
