package com.ccstudio.mpaz_pro_6.ui.screens.reports

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
import com.ccstudio.mpaz_pro_6.data.model.Docencia
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.model.requests.CreatePedagogicalReportRequest
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.DocenciaRepository
import com.ccstudio.mpaz_pro_6.data.repository.PedagogicalReportRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Estado del formulario para generar un informe pedagógico.
 *
 * El docente elige uno de sus cursos, un estudiante del curso y una unidad con
 * asignación en ese curso. El selector de estudiantes usa el endpoint acotado
 * `GET /api/cursos/{id}/estudiantes`; el de unidades se deriva de las
 * asignaciones del curso para no ofrecer combinaciones que el backend rechaza.
 *
 * El `docenteId` no se elige: es el docente autenticado.
 */
class ReportGenerateViewModel(
    private val assignmentRepository: AssignmentRepository,
    private val courseRepository: CourseRepository,
    private val unitRepository: UnitRepository,
    private val docenciaRepository: DocenciaRepository,
    private val userRepository: UserRepository,
    private val pedagogicalReportRepository: PedagogicalReportRepository,
    private val teacherId: Int,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
) : ViewModel() {

    /** Cursos en los que el docente tiene docencia. */
    var courses by mutableStateOf<List<Course>>(emptyList())
        private set

    /** Estudiantes del curso seleccionado. */
    var students by mutableStateOf<List<User>>(emptyList())
        private set

    /** Unidades con asignación en el curso seleccionado. */
    var units by mutableStateOf<List<LearningUnit>>(emptyList())
        private set

    /** Curso seleccionado. */
    var cursoId by mutableStateOf<Int?>(null)
        private set

    /** Estudiante seleccionado. */
    var estudianteId by mutableStateOf<Int?>(null)
        private set

    /** Unidad seleccionada. */
    var unidadId by mutableStateOf<Int?>(null)
        private set

    /** Indica que se están cargando los catálogos o los estudiantes. */
    var isLoading by mutableStateOf(false)
        private set

    /** Indica que se está generando el informe. */
    var isSaving by mutableStateOf(false)
        private set

    /** Mensaje de error visible en el formulario. */
    var error by mutableStateOf<String?>(null)
        private set

    private var cargado = false
    private var docencias: List<Docencia> = emptyList()
    private var todasLasUnidades: List<LearningUnit> = emptyList()
    private var asignaciones: List<Assignment> = emptyList()
    private var courseJob: Job? = null

    /** Carga docencias, cursos, unidades y asignaciones del docente. */
    fun cargar() {
        if (cargado) return

        viewModelScope.launch(dispatcher) {
            isLoading = true
            error = null

            val docenciasResult = docenciaRepository.list(teacherId)
            if (docenciasResult.isFailure) {
                error = mensajeDeError(docenciasResult.exceptionOrNull() as? ApiError)
                isLoading = false
                return@launch
            }

            val cursosResult = courseRepository.list()
            if (cursosResult.isFailure) {
                error = mensajeDeError(cursosResult.exceptionOrNull() as? ApiError)
                isLoading = false
                return@launch
            }

            val unidadesResult = unitRepository.list()
            if (unidadesResult.isFailure) {
                error = mensajeDeError(unidadesResult.exceptionOrNull() as? ApiError)
                isLoading = false
                return@launch
            }

            val asignacionesResult = assignmentRepository.list()
            if (asignacionesResult.isFailure) {
                error = mensajeDeError(asignacionesResult.exceptionOrNull() as? ApiError)
                isLoading = false
                return@launch
            }

            docencias = docenciasResult.getOrDefault(emptyList())
            todasLasUnidades = unidadesResult.getOrDefault(emptyList())
            asignaciones = asignacionesResult.getOrDefault(emptyList())

            val courseIds = docencias.map { it.cursoId }.toSet()
            courses = cursosResult.getOrDefault(emptyList())
                .filter { it.id in courseIds }
                .sortedWith(compareBy({ it.anio }, { it.nombre }))

            if (docencias.isEmpty() || courses.isEmpty()) {
                error = "No tienes cursos con docencia para generar informes."
                isLoading = false
                return@launch
            }

            val firstCourse = courses.first()
            seleccionarCursoInterno(firstCourse.id)
            cargado = error == null
            isLoading = false
        }
    }

    /** Cambia el curso y recarga sus estudiantes y unidades. */
    fun onCursoIdChange(value: Int?) {
        if (value == cursoId) return

        cursoId = value
        estudianteId = null
        unidadId = null
        students = emptyList()
        units = emptyList()
        error = null

        if (value == null) {
            courseJob?.cancel()
            return
        }

        courseJob?.cancel()
        courseJob = viewModelScope.launch(dispatcher) {
            isLoading = true
            seleccionarCursoInterno(value)
            isLoading = false
        }
    }

    /** Cambia el estudiante seleccionado. */
    fun onEstudianteIdChange(value: Int?) {
        estudianteId = value
        error = null
    }

    /** Cambia la unidad seleccionada. */
    fun onUnidadIdChange(value: Int?) {
        unidadId = value
        error = null
    }

    /**
     * Genera el informe y entrega el id creado.
     *
     * El backend calcula el detalle por OA y deja el informe en
     * `PENDIENTE_VALIDACION`.
     */
    fun generar(onGenerated: (Int) -> Unit) {
        val curso = cursoId
        val estudiante = estudianteId
        val unidad = unidadId

        if (curso == null) {
            error = "Selecciona un curso."
            return
        }
        if (estudiante == null) {
            error = "Selecciona un estudiante."
            return
        }
        if (unidad == null) {
            error = "Selecciona una unidad."
            return
        }
        if (isSaving) return

        viewModelScope.launch(dispatcher) {
            isSaving = true
            error = null

            val result = pedagogicalReportRepository.generate(
                CreatePedagogicalReportRequest(
                    estudianteId = estudiante,
                    unidadId = unidad,
                    docenteId = teacherId
                )
            )

            isSaving = false

            if (result.isFailure) {
                error = mensajeDeError(result.exceptionOrNull() as? ApiError)
                return@launch
            }

            val report = result.getOrNull()
            if (report == null) {
                error = "No se pudo generar el informe."
                return@launch
            }

            onGenerated(report.id)
        }
    }

    private suspend fun seleccionarCursoInterno(courseId: Int) {
        cursoId = courseId
        estudianteId = null
        unidadId = null
        error = null

        val subjectIds = docencias
            .filter { it.cursoId == courseId }
            .map { it.asignaturaId }
            .toSet()
        val unitIds = asignaciones
            .filter { it.cursoId == courseId }
            .map { it.unidadId }
            .toSet()

        units = todasLasUnidades
            .filter { it.id in unitIds && it.asignaturaId in subjectIds }
            .sortedWith(compareBy({ it.asignaturaId }, { it.orden }))

        val studentsResult = userRepository.listStudentsByCourse(courseId)
        if (studentsResult.isFailure) {
            students = emptyList()
            error = mensajeDeError(studentsResult.exceptionOrNull() as? ApiError)
            return
        }

        students = studentsResult.getOrDefault(emptyList())
            .sortedBy { it.nombre.lowercase() }
    }

    private fun mensajeDeError(error: ApiError?): String = when (error) {
        is ApiError.BadRequest -> error.message
        is ApiError.NotFound -> error.message
        is ApiError.Conflict -> error.message
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unknown -> error.message
        null -> "No se pudo completar la operación."
    }

    companion object {

        /** Factoría del formulario con los repositorios y el docente autenticado. */
        fun factory(
            assignmentRepository: AssignmentRepository,
            courseRepository: CourseRepository,
            unitRepository: UnitRepository,
            docenciaRepository: DocenciaRepository,
            userRepository: UserRepository,
            pedagogicalReportRepository: PedagogicalReportRepository,
            teacherId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ReportGenerateViewModel(
                    assignmentRepository = assignmentRepository,
                    courseRepository = courseRepository,
                    unitRepository = unitRepository,
                    docenciaRepository = docenciaRepository,
                    userRepository = userRepository,
                    pedagogicalReportRepository = pedagogicalReportRepository,
                    teacherId = teacherId
                )
            }
        }
    }
}
