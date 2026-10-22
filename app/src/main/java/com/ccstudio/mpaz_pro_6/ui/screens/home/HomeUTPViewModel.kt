package com.ccstudio.mpaz_pro_6.ui.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.repository.CourseRepository
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch

/**
 * Estado del panel de inicio de la jefa de UTP.
 *
 * El backend no expone un listado dedicado de unidades pendientes de revisión:
 * el panel cuenta las que están en `EN_REVISION` sobre `GET /api/unidades`.
 */
class HomeUTPViewModel(
    private val unitRepository: UnitRepository,
    private val courseRepository: CourseRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    /** Indica que el panel se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Unidades pendientes de revisión. */
    var unidadesPendientes by mutableIntStateOf(0)
        private set

    /** Total de cursos registrados. */
    var totalCursos by mutableIntStateOf(0)
        private set

    /** Total de asignaturas registradas. */
    var totalAsignaturas by mutableIntStateOf(0)
        private set

    /** Carga los contadores del panel. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val unidades = unitRepository.list()

            if (unidades.isFailure) {
                error = unidades.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val cursos = courseRepository.list()

            if (cursos.isFailure) {
                error = cursos.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val asignaturas = subjectRepository.list()

            if (asignaturas.isFailure) {
                error = asignaturas.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            unidadesPendientes = unidades.getOrDefault(emptyList())
                .count { it.estado == ContentStatus.IN_REVIEW }

            totalCursos = cursos.getOrDefault(emptyList()).size
            totalAsignaturas = asignaturas.getOrDefault(emptyList()).size

            isLoading = false
        }
    }

    companion object {

        /** Factoría del ViewModel con los repositorios del panel. */
        fun factory(
            unitRepository: UnitRepository,
            courseRepository: CourseRepository,
            subjectRepository: SubjectRepository
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeUTPViewModel(unitRepository, courseRepository, subjectRepository)
            }
        }
    }
}
