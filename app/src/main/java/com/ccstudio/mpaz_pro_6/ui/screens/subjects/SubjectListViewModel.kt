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
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import kotlinx.coroutines.launch

/**
 * Estado del listado de asignaturas.
 *
 * Carga una sola vez `GET /api/asignaturas`; la búsqueda por nombre es
 * client-side, tal como exige el contrato.
 */
class SubjectListViewModel(
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    /** Asignaturas ordenadas por nombre. */
    var asignaturas by mutableStateOf<List<Subject>>(emptyList())
        private set

    /** Indica que el listado se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Mensaje de una operación de borrado, para mostrarlo en el listado. */
    var mensajeOperacion by mutableStateOf<String?>(null)
        private set

    /** Carga las asignaturas. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val result = subjectRepository.list()

            if (result.isFailure) {
                error = result.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            asignaturas = result.getOrDefault(emptyList())
            isLoading = false
        }
    }

    /**
     * Elimina una asignatura y recarga el listado.
     *
     * Un `409 Conflict` significa que todavía tiene unidades asociadas; el
     * mensaje lo explica en la pantalla.
     */
    fun eliminar(id: Int) {
        viewModelScope.launch {
            mensajeOperacion = null

            val result = subjectRepository.delete(id)

            if (result.isFailure) {
                mensajeOperacion = when (val fallo = result.exceptionOrNull() as? ApiError) {
                    is ApiError.Conflict -> "No se puede eliminar: tiene unidades asociadas."
                    is ApiError.NotFound -> "La asignatura ya no existe."
                    is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
                    is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
                    is ApiError.BadRequest -> fallo.message
                    is ApiError.Unknown -> fallo.message
                    null -> "No se pudo eliminar la asignatura."
                }
                return@launch
            }

            cargar()
        }
    }

    /** Oculta el mensaje de la última operación. */
    fun limpiarMensaje() {
        mensajeOperacion = null
    }

    companion object {

        /** Factoría del ViewModel con el repositorio de asignaturas. */
        fun factory(
            subjectRepository: SubjectRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SubjectListViewModel(subjectRepository = subjectRepository)
            }
        }
    }
}
