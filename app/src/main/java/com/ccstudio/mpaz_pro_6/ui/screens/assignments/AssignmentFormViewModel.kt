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
import com.ccstudio.mpaz_pro_6.data.model.Course
import com.ccstudio.mpaz_pro_6.data.model.Docencia
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.DocenciaRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * Estado del formulario de asignación.
 *
 * Sin `assignmentId` crea una asignación para una docencia del docente. Con id
 * edita solo las reglas que admite el `PUT`: fecha, máximo de intentos y prueba
 * final habilitada. En edición el curso y la unidad quedan bloqueados.
 *
 * Al crear, el curso se limita a las docencias del docente y la unidad a las
 * asignaturas que dicta en ese curso. El docente no se elige: lo deriva el
 * backend desde la docencia.
 */
class AssignmentFormViewModel(
    private val assignmentRepository: AssignmentRepository,
    private val courseRepository: CourseRepository,
    private val unitRepository: UnitRepository,
    private val docenciaRepository: DocenciaRepository,
    private val assignmentId: Int?,
    private val teacherId: Int,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main.immediate
) : ViewModel() {

    /** Cursos disponibles para crear o el curso actual al editar. */
    var courses by mutableStateOf<List<Course>>(emptyList())
        private set

    /** Unidades disponibles para el curso seleccionado. */
    var units by mutableStateOf<List<LearningUnit>>(emptyList())
        private set

    /** Curso seleccionado. */
    var cursoId by mutableStateOf<Int?>(null)
        private set

    /** Unidad seleccionada. */
    var unidadId by mutableStateOf<Int?>(null)
        private set

    /** Fecha de la asignación. */
    var fecha by mutableStateOf<Instant?>(null)
        private set

    /** Máximo de intentos como texto, para validarlo en el formulario. */
    var maxIntentos by mutableStateOf("")
        private set

    /** Indica si la prueba final está habilitada para el curso. */
    var pruebaFinalHabilitada by mutableStateOf(false)
        private set

    /** Indica que se están cargando los catálogos o la asignación. */
    var isLoading by mutableStateOf(false)
        private set

    /** Indica que se está guardando. */
    var isSaving by mutableStateOf(false)
        private set

    /** Mensaje de error visible en el formulario. */
    var error by mutableStateOf<String?>(null)
        private set

    /** `true` cuando se edita una asignación existente. */
    val isEditMode: Boolean = assignmentId != null

    private var cargado = false
    private var originalFecha: Instant? = null
    private var originalMaxIntentos: Int? = null
    private var originalPruebaFinal = false
    private var todasLasUnidades: List<LearningUnit> = emptyList()
    private var docencias: List<Docencia> = emptyList()

    /** Cambia el curso y recalcula las unidades disponibles. */
    fun onCursoIdChange(value: Int?) {
        if (value == cursoId) return
        cursoId = value
        unidadId = null
        actualizarUnidades()
        error = null
    }

    /** Cambia la unidad asignada. */
    fun onUnidadIdChange(value: Int?) {
        unidadId = value
        error = null
    }

    /** Cambia el máximo de intentos, solo con dígitos. */
    fun onMaxIntentosChange(value: String) {
        maxIntentos = value.filter { it.isDigit() }
        error = null
    }

    /** Cambia la fecha. */
    fun onFechaChange(value: Instant?) {
        fecha = value
        error = null
    }

    /** Cambia la habilitación de la prueba final. */
    fun onPruebaFinalHabilitadaChange(value: Boolean) {
        pruebaFinalHabilitada = value
        error = null
    }

    /** Carga la asignación o las docencias y catálogos para crear. */
    fun cargar() {
        if (cargado) return

        viewModelScope.launch(dispatcher) {
            isLoading = true
            error = null

            if (assignmentId != null) {
                cargarEdicion(assignmentId)
            } else {
                cargarCreacion()
            }

            if (error == null) {
                cargado = true
            }
            isLoading = false
        }
    }

    private suspend fun cargarEdicion(id: Int) {
        val assignmentResult = assignmentRepository.getById(id)
        if (assignmentResult.isFailure) {
            error = mensajeDeError(assignmentResult.exceptionOrNull() as? ApiError)
            return
        }

        val assignment = assignmentResult.getOrThrow()
        val courseResult = courseRepository.getById(assignment.cursoId)
        if (courseResult.isFailure) {
            error = mensajeDeError(courseResult.exceptionOrNull() as? ApiError)
            return
        }

        val unitResult = unitRepository.getById(assignment.unidadId)
        if (unitResult.isFailure) {
            error = mensajeDeError(unitResult.exceptionOrNull() as? ApiError)
            return
        }

        courses = listOf(courseResult.getOrThrow())
        units = listOf(unitResult.getOrThrow())
        cursoId = assignment.cursoId
        unidadId = assignment.unidadId
        fecha = assignment.fecha
        maxIntentos = assignment.maxIntentos?.toString().orEmpty()
        pruebaFinalHabilitada = assignment.pruebaFinalHabilitada

        originalFecha = assignment.fecha
        originalMaxIntentos = assignment.maxIntentos
        originalPruebaFinal = assignment.pruebaFinalHabilitada
    }

    private suspend fun cargarCreacion() {
        val docenciasResult = docenciaRepository.list(teacherId)
        if (docenciasResult.isFailure) {
            error = mensajeDeError(docenciasResult.exceptionOrNull() as? ApiError)
            return
        }

        val cursosResult = courseRepository.list()
        if (cursosResult.isFailure) {
            error = mensajeDeError(cursosResult.exceptionOrNull() as? ApiError)
            return
        }

        val unidadesResult = unitRepository.list()
        if (unidadesResult.isFailure) {
            error = mensajeDeError(unidadesResult.exceptionOrNull() as? ApiError)
            return
        }

        docencias = docenciasResult.getOrDefault(emptyList())
        if (docencias.isEmpty()) {
            error = "No tienes docencias para crear asignaciones."
            return
        }

        val courseIds = docencias.map { it.cursoId }.toSet()
        val subjectIds = docencias.map { it.asignaturaId }.toSet()
        courses = cursosResult.getOrDefault(emptyList())
            .filter { it.id in courseIds }
            .sortedWith(compareBy({ it.anio }, { it.nombre }))
        todasLasUnidades = unidadesResult.getOrDefault(emptyList())
            .filter { it.asignaturaId in subjectIds }
        fecha = Instant.now()
    }

    /** Ajusta las unidades al curso seleccionado. */
    private fun actualizarUnidades() {
        val courseId = cursoId
        if (courseId == null) {
            units = emptyList()
            return
        }

        val subjectIds = docencias
            .filter { it.cursoId == courseId }
            .map { it.asignaturaId }
            .toSet()
        units = todasLasUnidades
            .filter { it.asignaturaId in subjectIds }
            .sortedWith(compareBy({ it.asignaturaId }, { it.orden }))
    }

    /** Guarda la asignación y avisa cuando termina. */
    fun guardar(onSaved: () -> Unit) {
        val fechaActual = fecha
        if (fechaActual == null) {
            error = "La fecha es obligatoria."
            return
        }

        val maxIntentosTexto = maxIntentos.trim()
        val maxIntentosValor = if (maxIntentosTexto.isEmpty()) {
            null
        } else {
            maxIntentosTexto.toIntOrNull()
        }
        if (maxIntentosTexto.isNotEmpty() &&
            (maxIntentosValor == null || maxIntentosValor <= 0)
        ) {
            error = "El máximo de intentos debe ser mayor que cero."
            return
        }

        if (assignmentId == null) {
            val curso = cursoId
            val unidad = unidadId
            if (curso == null) {
                error = "Selecciona un curso."
                return
            }
            if (unidad == null) {
                error = "Selecciona una unidad."
                return
            }

            viewModelScope.launch(dispatcher) {
                isSaving = true
                error = null

                val result = assignmentRepository.create(
                    CreateAssignmentRequest(
                        cursoId = curso,
                        unidadId = unidad,
                        fecha = fechaActual,
                        maxIntentos = maxIntentosValor,
                        pruebaFinalHabilitada = pruebaFinalHabilitada
                    )
                )

                isSaving = false
                if (result.isFailure) {
                    error = mensajeDeError(result.exceptionOrNull() as? ApiError)
                    return@launch
                }
                onSaved()
            }
            return
        }

        val maxIntentosCambiado = maxIntentosValor
            ?.takeIf { it != originalMaxIntentos }
        val pruebaFinalCambiada = pruebaFinalHabilitada
            .takeIf { it != originalPruebaFinal }
        val fechaCambiada = fechaActual
            .takeIf { it != originalFecha }

        if (maxIntentosCambiado == null &&
            pruebaFinalCambiada == null &&
            fechaCambiada == null
        ) {
            error = "No hay cambios para guardar."
            return
        }

        viewModelScope.launch(dispatcher) {
            isSaving = true
            error = null

            val result = assignmentRepository.update(
                assignmentId,
                UpdateAssignmentRequest(
                    maxIntentos = maxIntentosCambiado,
                    pruebaFinalHabilitada = pruebaFinalCambiada,
                    fecha = fechaCambiada
                )
            )

            isSaving = false
            if (result.isFailure) {
                error = mensajeDeError(result.exceptionOrNull() as? ApiError)
                return@launch
            }
            onSaved()
        }
    }

    /** Traduce el fallo del repositorio a un mensaje visible. */
    private fun mensajeDeError(error: ApiError?): String = when (error) {
        is ApiError.Conflict -> "Ya existe una asignación para ese curso y unidad."
        is ApiError.BadRequest -> error.message
        is ApiError.NotFound -> "La asignación, el curso o la unidad ya no existe."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Unknown -> error.message
        null -> "No se pudo guardar la asignación."
    }

    companion object {

        /** Factoría del formulario con los repositorios y el contexto. */
        fun factory(
            assignmentRepository: AssignmentRepository,
            courseRepository: CourseRepository,
            unitRepository: UnitRepository,
            docenciaRepository: DocenciaRepository,
            assignmentId: Int?,
            teacherId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AssignmentFormViewModel(
                    assignmentRepository = assignmentRepository,
                    courseRepository = courseRepository,
                    unitRepository = unitRepository,
                    docenciaRepository = docenciaRepository,
                    assignmentId = assignmentId,
                    teacherId = teacherId
                )
            }
        }
    }
}
