package com.ccstudio.mpaz_pro_6.ui.screens.subjects

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.DocenciaRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Estado del detalle de una asignatura para el docente.
 *
 * Solo acepta asignaturas que el docente dicta según sus docencias. Muestra las
 * unidades del catálogo y permite filtrarlas por curso; al elegir un curso solo
 * quedan las unidades que tienen una asignación en ese curso.
 */
class TeacherSubjectDetailViewModel(
    private val docenciaRepository: DocenciaRepository,
    private val subjectRepository: SubjectRepository,
    private val courseRepository: CourseRepository,
    private val unitRepository: UnitRepository,
    private val assignmentRepository: AssignmentRepository,
    private val teacherId: Int,
    private val subjectId: Int,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
) : ViewModel() {

    /** Asignatura cargada. */
    var subject by mutableStateOf<Subject?>(null)
        private set

    /** Cursos del docente en los que se dicta esta asignatura. */
    var cursos by mutableStateOf<List<Course>>(emptyList())
        private set

    /** Unidades visibles según el filtro de curso. */
    var unidades by mutableStateOf<List<LearningUnit>>(emptyList())
        private set

    /** Curso seleccionado; `null` significa todas las unidades. */
    var selectedCourseId by mutableStateOf<Int?>(null)
        private set

    /** Indica que el detalle se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    private var todasLasUnidades: List<LearningUnit> = emptyList()
    private var unidadesPorCurso: Map<Int, Set<Int>> = emptyMap()

    /** Carga la asignatura, sus cursos, sus unidades y sus asignaciones. */
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

            val docencias = docenciasResult.getOrDefault(emptyList())
                .filter { it.asignaturaId == subjectId }
            if (docencias.isEmpty()) {
                error = ApiError.NotFound("No tienes docencia en esta asignatura.")
                isLoading = false
                return@launch
            }

            val subjectResult = subjectRepository.getById(subjectId)
            if (subjectResult.isFailure) {
                error = subjectResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val cursosResult = courseRepository.list()
            if (cursosResult.isFailure) {
                error = cursosResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val unidadesResult = unitRepository.list(subjectId)
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

            val courseIds = docencias.map { it.cursoId }.toSet()
            cursos = cursosResult.getOrDefault(emptyList())
                .filter { it.id in courseIds }
                .sortedWith(compareBy({ it.anio }, { it.nombre }))
            subject = subjectResult.getOrThrow()
            todasLasUnidades = unidadesResult.getOrDefault(emptyList())
            unidadesPorCurso = asignacionesResult.getOrDefault(emptyList())
                .filter { it.cursoId in courseIds }
                .groupBy { it.cursoId }
                .mapValues { (_, assignments) -> assignments.map { it.unidadId }.toSet() }
            aplicarFiltro()

            isLoading = false
        }
    }

    /** Filtra por curso; `null` muestra todas las unidades. */
    fun seleccionarCurso(courseId: Int?) {
        if (courseId != null && cursos.none { it.id == courseId }) return
        selectedCourseId = courseId
        aplicarFiltro()
    }

    /** Recalcula la lista visible a partir del curso seleccionado. */
    private fun aplicarFiltro() {
        val courseId = selectedCourseId
        unidades = if (courseId == null) {
            todasLasUnidades
        } else {
            val unitIds = unidadesPorCurso[courseId].orEmpty()
            todasLasUnidades.filter { it.id in unitIds }
        }
    }

    companion object {

        /** Factoría del ViewModel con los repositorios y el contexto docente. */
        fun factory(
            docenciaRepository: DocenciaRepository,
            subjectRepository: SubjectRepository,
            courseRepository: CourseRepository,
            unitRepository: UnitRepository,
            assignmentRepository: AssignmentRepository,
            teacherId: Int,
            subjectId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TeacherSubjectDetailViewModel(
                    docenciaRepository = docenciaRepository,
                    subjectRepository = subjectRepository,
                    courseRepository = courseRepository,
                    unitRepository = unitRepository,
                    assignmentRepository = assignmentRepository,
                    teacherId = teacherId,
                    subjectId = subjectId
                )
            }
        }
    }
}
