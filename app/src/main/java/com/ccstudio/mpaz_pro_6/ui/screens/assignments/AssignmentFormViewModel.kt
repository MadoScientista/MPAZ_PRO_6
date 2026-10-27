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
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.User
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import com.ccstudio.mpaz_pro_6.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * Estado del formulario de edición de una asignación.
 *
 * Carga la asignación y resuelve curso, unidad y docente para mostrar los
 * selectores bloqueados. El `PUT` es parcial: solo se envían los campos que
 * cambiaron. El curso, la unidad y el docente no se modifican.
 *
 * `maxIntentos` vacío significa "no enviar" y conserva el valor actual. El
 * contrato no permite distinguir entre "no enviar" y "poner en nulo", así que
 * desde la app no se puede quitar un límite ya existente.
 */
class AssignmentFormViewModel(
    private val assignmentRepository: AssignmentRepository,
    private val courseRepository: CourseRepository,
    private val unitRepository: UnitRepository,
    private val userRepository: UserRepository,
    private val assignmentId: Int
) : ViewModel() {

    /** Curso actual, para el selector bloqueado del formulario. */
    var courses by mutableStateOf<List<Course>>(emptyList())
        private set

    /** Unidad actual, para el selector bloqueado del formulario. */
    var units by mutableStateOf<List<LearningUnit>>(emptyList())
        private set

    /** Docente actual, para el selector bloqueado del formulario. */
    var teachers by mutableStateOf<List<User>>(emptyList())
        private set

    /** Curso de la asignación. */
    var cursoId by mutableStateOf<Int?>(null)
        private set

    /** Unidad de la asignación. */
    var unidadId by mutableStateOf<Int?>(null)
        private set

    /** Docente de la asignación. */
    var docenteId by mutableStateOf<Int?>(null)
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

    /** Indica que la asignación se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Indica que se está guardando. */
    var isSaving by mutableStateOf(false)
        private set

    /** Mensaje de error visible en el formulario. */
    var error by mutableStateOf<String?>(null)
        private set

    /** `true` porque este formulario solo edita asignaciones existentes. */
    val isEditMode: Boolean = true

    private var cargado = false
    private var originalFecha: Instant? = null
    private var originalMaxIntentos: Int? = null
    private var originalPruebaFinal = false

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

    /** Carga la asignación y sus entidades relacionadas. */
    fun cargar() {
        if (cargado) return

        viewModelScope.launch {
            isLoading = true
            error = null

            val assignmentResult = assignmentRepository.getById(assignmentId)

            if (assignmentResult.isFailure) {
                error = mensajeDeError(assignmentResult.exceptionOrNull() as? ApiError)
                isLoading = false
                return@launch
            }

            val assignment = assignmentResult.getOrThrow()

            val courseResult = courseRepository.getById(assignment.cursoId)

            if (courseResult.isFailure) {
                error = mensajeDeError(courseResult.exceptionOrNull() as? ApiError)
                isLoading = false
                return@launch
            }

            val unitResult = unitRepository.getById(assignment.unidadId)

            if (unitResult.isFailure) {
                error = mensajeDeError(unitResult.exceptionOrNull() as? ApiError)
                isLoading = false
                return@launch
            }

            val teacherResult = userRepository.getById(assignment.docenteId)

            if (teacherResult.isFailure) {
                error = mensajeDeError(teacherResult.exceptionOrNull() as? ApiError)
                isLoading = false
                return@launch
            }

            courses = listOf(courseResult.getOrThrow())
            units = listOf(unitResult.getOrThrow())
            teachers = listOf(teacherResult.getOrThrow())
            cursoId = assignment.cursoId
            unidadId = assignment.unidadId
            docenteId = assignment.docenteId
            fecha = assignment.fecha
            maxIntentos = assignment.maxIntentos?.toString().orEmpty()
            pruebaFinalHabilitada = assignment.pruebaFinalHabilitada

            originalFecha = assignment.fecha
            originalMaxIntentos = assignment.maxIntentos
            originalPruebaFinal = assignment.pruebaFinalHabilitada
            cargado = true
            isLoading = false
        }
    }

    /** Guarda solo los campos modificados y avisa cuando termina. */
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

        viewModelScope.launch {
            isSaving = true
            error = null

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
                isSaving = false
                return@launch
            }

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

    /** Traduce el fallo del repositorio a un mensaje para el formulario. */
    private fun mensajeDeError(error: ApiError?): String = when (error) {
        is ApiError.Conflict -> "No se pudo guardar: revisá los datos."
        is ApiError.BadRequest -> error.message
        is ApiError.NotFound -> "La asignación ya no existe."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Unknown -> error.message
        null -> "No se pudo guardar la asignación."
    }

    companion object {

        /** Factoría del formulario con los repositorios y el id. */
        fun factory(
            assignmentRepository: AssignmentRepository,
            courseRepository: CourseRepository,
            unitRepository: UnitRepository,
            userRepository: UserRepository,
            assignmentId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AssignmentFormViewModel(
                    assignmentRepository = assignmentRepository,
                    courseRepository = courseRepository,
                    unitRepository = unitRepository,
                    userRepository = userRepository,
                    assignmentId = assignmentId
                )
            }
        }
    }
}
