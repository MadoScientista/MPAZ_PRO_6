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
