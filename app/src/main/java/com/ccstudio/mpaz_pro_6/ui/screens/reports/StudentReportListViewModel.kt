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
import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import com.ccstudio.mpaz_pro_6.data.repository.PedagogicalReportRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch

/**
 * Estado del listado de informes del estudiante.
 *
 * Consulta `GET /api/informes-pedagogicos?estudianteId=` y resuelve el nombre de
 * la unidad. Los informes son de solo lectura para el estudiante.
 */
class StudentReportListViewModel(
    private val pedagogicalReportRepository: PedagogicalReportRepository,
    private val unitRepository: UnitRepository,
    private val studentId: Int
) : ViewModel() {

    /** Informes del estudiante. */
    var informes by mutableStateOf<List<PedagogicalReport>>(emptyList())
        private set

    /** Títulos de las unidades, indexados por id. */
    var unitTitles by mutableStateOf<Map<Int, String>>(emptyMap())
        private set

    /** Indica que el listado se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Carga los informes y los títulos de las unidades. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val informesResult = pedagogicalReportRepository.list(studentId = studentId)

            if (informesResult.isFailure) {
                error = informesResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            informes = informesResult.getOrDefault(emptyList())

            // El título de la unidad es un dato de apoyo: si falla, se usa el id.
            unitTitles = unitRepository.list()
                .getOrDefault(emptyList())
                .associate { it.id to it.titulo }

            isLoading = false
        }
    }

    companion object {

        /** Factoría del listado con los repositorios y el estudiante. */
        fun factory(
            pedagogicalReportRepository: PedagogicalReportRepository,
            unitRepository: UnitRepository,
            studentId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                StudentReportListViewModel(
                    pedagogicalReportRepository = pedagogicalReportRepository,
                    unitRepository = unitRepository,
                    studentId = studentId
                )
            }
        }
    }
}
