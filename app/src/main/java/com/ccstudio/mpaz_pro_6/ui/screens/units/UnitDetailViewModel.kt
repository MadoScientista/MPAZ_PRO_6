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
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch

/**
 * Estado del detalle de una unidad.
 *
 * Consume `GET /api/unidades/{id}`. El agrupado en objetivos de aprendizaje,
 * contenidos y actividades llega con sus repositorios; por ahora la pantalla
 * muestra los datos propios de la unidad.
 */
class UnitDetailViewModel(
    private val unitRepository: UnitRepository,
    private val subjectRepository: SubjectRepository,
    private val unitId: Int
) : ViewModel() {

    /** Unidad consultada. */
    var unidad by mutableStateOf<LearningUnit?>(null)
        private set

    /** Nombre de la asignatura de la unidad. */
    var asignatura by mutableStateOf<Subject?>(null)
        private set

    /** Indica que el detalle se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga la unidad y el nombre de su asignatura. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val unitResult = unitRepository.getById(unitId)

            if (unitResult.isFailure) {
                error = unitResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val unit = unitResult.getOrThrow()
            unidad = unit

            val subjectsResult = subjectRepository.list()

            if (subjectsResult.isSuccess) {
                asignatura = subjectsResult.getOrDefault(emptyList())
                    .find { it.id == unit.asignaturaId }
            }

            isLoading = false
        }
    }

    companion object {

        /** Factoría del ViewModel con el repositorio y el id de la unidad. */
        fun factory(
            unitRepository: UnitRepository,
            subjectRepository: SubjectRepository,
            unitId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                UnitDetailViewModel(unitRepository, subjectRepository, unitId)
            }
        }
    }
}
