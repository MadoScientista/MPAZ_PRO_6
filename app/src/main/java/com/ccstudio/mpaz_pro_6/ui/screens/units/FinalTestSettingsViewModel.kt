package com.ccstudio.mpaz_pro_6.ui.screens.units

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import kotlinx.coroutines.launch
import java.time.Instant

/**
 * Estado de la configuración de la prueba final de una unidad.
 *
 * La fecha de aplicación, el máximo de intentos y la habilitación viven en la
 * asignación de la unidad al curso. El `PUT` es parcial: solo se envían los
 * campos que cambiaron.
 *
 * `maxIntentos` vacío significa "no enviar" y conserva el valor actual. El
 * contrato no permite distinguir entre "no enviar" y "poner en nulo", así que
 * desde la app no se puede quitar un límite ya existente.
 */
class FinalTestSettingsViewModel(
    private val assignmentRepository: AssignmentRepository,
    private val assignmentId: Int
) : ViewModel() {

    /** Fecha de aplicación configurada. */
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

    private var cargado = false
    private var originalFecha: Instant? = null
    private var originalMaxIntentos: Int? = null
    private var originalPruebaFinal = false

    /** Cambia el máximo de intentos, solo con dígitos. */
    fun onMaxIntentosChange(value: String) {
        maxIntentos = value.filter { it.isDigit() }
        error = null
    }

    /** Cambia la fecha de aplicación. */
    fun onFechaChange(value: Instant?) {
        fecha = value
        error = null
    }

    /** Cambia la habilitación de la prueba final. */
    fun onPruebaFinalHabilitadaChange(value: Boolean) {
        pruebaFinalHabilitada = value
        error = null
    }

    /** Carga la asignación de la prueba final. */
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
            error = "La fecha de aplicación es obligatoria."
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
        is ApiError.NotFound -> "La asignación de la prueba final ya no existe."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Unknown -> error.message
        null -> "No se pudo guardar la configuración de la prueba final."
    }

    companion object {

        /** Factoría de la configuración con el repositorio y el id de asignación. */
        fun factory(
            assignmentRepository: AssignmentRepository,
            assignmentId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FinalTestSettingsViewModel(
                    assignmentRepository = assignmentRepository,
                    assignmentId = assignmentId
                )
            }
        }
    }
}
