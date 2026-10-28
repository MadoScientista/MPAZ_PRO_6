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
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import com.ccstudio.mpaz_pro_6.data.repository.PedagogicalReportRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch

/**
 * Estado del listado de informes pedagógicos del docente.
 *
 * El backend limita el listado al docente autenticado. Las unidades se cargan
 * para resolver el título cuando la respuesta no trae `unidadTitulo`; el nombre
 * del estudiante llega en el propio informe.
 */
class PedagogicalReportListViewModel(
    private val pedagogicalReportRepository: PedagogicalReportRepository,
    private val unitRepository: UnitRepository,
    private val teacherId: Int
) : ViewModel() {

    /** Informes del docente. */
    var informes by mutableStateOf<List<PedagogicalReport>>(emptyList())
        private set

    /** Unidades, para resolver el título cuando el informe no lo trae. */
    var unidades by mutableStateOf<List<LearningUnit>>(emptyList())
        private set

    /** Indica que el listado se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga los informes del docente y los catálogos de apoyo. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val informesResult = pedagogicalReportRepository.list()
            if (informesResult.isFailure) {
                error = informesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val unidadesResult = unitRepository.list()
            if (unidadesResult.isFailure) {
                error = unidadesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            informes = informesResult.getOrDefault(emptyList())
                .filter { it.docenteId == teacherId }
            unidades = unidadesResult.getOrDefault(emptyList())
            isLoading = false
        }
    }

    companion object {

        /** Factoría del ViewModel con los repositorios del listado. */
        fun factory(
            pedagogicalReportRepository: PedagogicalReportRepository,
            unitRepository: UnitRepository,
            teacherId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PedagogicalReportListViewModel(
                    pedagogicalReportRepository = pedagogicalReportRepository,
                    unitRepository = unitRepository,
                    teacherId = teacherId
                )
            }
        }
    }
}
