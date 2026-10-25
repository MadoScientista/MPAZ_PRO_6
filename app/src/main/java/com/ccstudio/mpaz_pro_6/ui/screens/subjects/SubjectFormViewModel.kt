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
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateSubjectRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateSubjectRequest
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import kotlinx.coroutines.launch

/**
 * Estado del formulario de asignatura.
 *
 * Sin `subjectId` crea; con id carga la asignatura y edita. El nombre es
 * obligatorio y el `PUT` envía el valor actual porque el contrato lo exige.
 */
class SubjectFormViewModel(
    private val subjectRepository: SubjectRepository,
    private val subjectId: Int?
) : ViewModel() {

    /** Nombre de la asignatura. */
    var nombre by mutableStateOf("")
        private set

    /** Indica si se está cargando la asignatura a editar. */
    var isLoading by mutableStateOf(false)
        private set

    /** Indica que se está guardando. */
    var isSaving by mutableStateOf(false)
        private set

    /** Mensaje de error visible en el formulario. */
    var error by mutableStateOf<String?>(null)
        private set

    /** `true` cuando se edita una asignatura existente. */
    val isEditMode: Boolean = subjectId != null

    private var cargado = false

    /** Cambia el nombre. */
    fun onNombreChange(value: String) {
        nombre = value
        error = null
    }

    /** Carga la asignatura cuando se edita. En creación no hace nada. */
    fun cargar() {
        if (subjectId == null || cargado) return

        viewModelScope.launch {
            isLoading = true
            error = null

            val result = subjectRepository.getById(subjectId)

            if (result.isFailure) {
                error = mensajeDeError(result.exceptionOrNull() as? ApiError)
                isLoading = false
                return@launch
            }

            nombre = result.getOrNull()?.nombre.orEmpty()
            cargado = true
            isLoading = false
        }
    }

    /** Guarda la asignatura y avisa a la pantalla cuando termina. */
    fun guardar(onSaved: () -> Unit) {
        val nombreLimpio = nombre.trim()

        if (nombreLimpio.isBlank()) {
            error = "El nombre es obligatorio."
            return
        }

        viewModelScope.launch {
            isSaving = true
            error = null

            val result = if (subjectId == null) {
                subjectRepository.create(CreateSubjectRequest(nombre = nombreLimpio))
            } else {
                subjectRepository.update(
                    subjectId,
                    UpdateSubjectRequest(nombre = nombreLimpio)
                )
            }

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
        is ApiError.Conflict -> "Ya existe una asignatura con ese nombre."
        is ApiError.BadRequest -> error.message
        is ApiError.NotFound -> "La asignatura ya no existe."
        is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
        is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
        is ApiError.Unknown -> error.message
        null -> "No se pudo guardar la asignatura."
    }

    companion object {

        /** Factoría del formulario con el repositorio y el id opcional. */
        fun factory(
            subjectRepository: SubjectRepository,
            subjectId: Int?
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SubjectFormViewModel(
                    subjectRepository = subjectRepository,
                    subjectId = subjectId
                )
            }
        }
    }
}
