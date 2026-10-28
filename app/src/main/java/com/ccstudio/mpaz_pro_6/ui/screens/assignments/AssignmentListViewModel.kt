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
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.DocenciaRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch

/**
 * Estado del listado de asignaciones del docente.
 *
 * La asignación solo trae `cursoId` y `unidadId`; los cursos y las unidades se
 * cargan completos para resolver los nombres que muestra la pantalla. El docente
 * ya no forma parte de la asignación: se resuelve con la docencia del curso y la
 * asignatura de la unidad, y el listado se filtra por esas docencias.
 */
class AssignmentListViewModel(
    private val assignmentRepository: AssignmentRepository,
    private val courseRepository: CourseRepository,
    private val unitRepository: UnitRepository,
    private val docenciaRepository: DocenciaRepository,
    private val teacherId: Int
) : ViewModel() {

    /** Asignaciones del docente. */
    var asignaciones by mutableStateOf<List<Assignment>>(emptyList())
        private set

    /** Cursos, para resolver el nombre del curso de cada asignación. */
    var cursos by mutableStateOf<List<Course>>(emptyList())
        private set

    /** Unidades, para resolver el título de la unidad asignada. */
    var unidades by mutableStateOf<List<LearningUnit>>(emptyList())
        private set

    /** Indica que el listado se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga las asignaciones del docente y los catálogos de apoyo. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val asignacionesResult = assignmentRepository.list()
            if (asignacionesResult.isFailure) {
                error = asignacionesResult.exceptionOrNull() as? ApiError
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

            val docenciasResult = docenciaRepository.list(teacherId)
            if (docenciasResult.isFailure) {
                error = docenciasResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val unidadesCargadas = unidadesResult.getOrDefault(emptyList())
            val unidadesPorId = unidadesCargadas.associateBy { it.id }
            val docencias = docenciasResult.getOrDefault(emptyList())
                .map { it.cursoId to it.asignaturaId }
                .toSet()

            asignaciones = asignacionesResult.getOrDefault(emptyList())
                .filter { assignment ->
                    val unidad = unidadesPorId[assignment.unidadId]
                        ?: return@filter false
                    (assignment.cursoId to unidad.asignaturaId) in docencias
                }
            cursos = cursosResult.getOrDefault(emptyList())
            unidades = unidadesCargadas
            isLoading = false
        }
    }

    companion object {

        /** Factoría del ViewModel con los repositorios del listado. */
        fun factory(
            assignmentRepository: AssignmentRepository,
            courseRepository: CourseRepository,
            unitRepository: UnitRepository,
            docenciaRepository: DocenciaRepository,
            teacherId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AssignmentListViewModel(
                    assignmentRepository = assignmentRepository,
                    courseRepository = courseRepository,
                    unitRepository = unitRepository,
                    docenciaRepository = docenciaRepository,
                    teacherId = teacherId
                )
            }
        }
    }
}
