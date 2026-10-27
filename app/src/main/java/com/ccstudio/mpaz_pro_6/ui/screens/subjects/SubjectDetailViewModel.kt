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
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch

/**
 * Estado del detalle de una asignatura.
 *
 * Carga la asignatura y todas sus unidades con `GET /api/asignaturas/{id}` y
 * `GET /api/unidades?asignaturaId={id}`.
 */
class SubjectDetailViewModel(
    private val subjectRepository: SubjectRepository,
    private val unitRepository: UnitRepository,
    private val subjectId: Int
) : ViewModel() {

    /** Asignatura cargada, o `null` mientras carga o si falló. */
    var subject by mutableStateOf<Subject?>(null)
        private set

    /** Unidades de la asignatura, en el orden del backend. */
    var unidades by mutableStateOf<List<LearningUnit>>(emptyList())
        private set

    /** Indica que el detalle se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga la asignatura y sus unidades. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val subjectResult = subjectRepository.getById(subjectId)

            if (subjectResult.isFailure) {
                error = subjectResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            subject = subjectResult.getOrNull()

            val unitsResult = unitRepository.list(subjectId)

            if (unitsResult.isFailure) {
                error = unitsResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            unidades = unitsResult.getOrDefault(emptyList())
            isLoading = false
        }
    }

    companion object {

        /** Factoría del ViewModel con los repositorios y el id de la asignatura. */
        fun factory(
            subjectRepository: SubjectRepository,
            unitRepository: UnitRepository,
            subjectId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SubjectDetailViewModel(subjectRepository, unitRepository, subjectId)
            }
        }
    }
}
