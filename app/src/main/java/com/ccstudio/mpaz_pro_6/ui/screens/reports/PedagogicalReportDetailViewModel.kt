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
import com.ccstudio.mpaz_pro_6.data.enums.ReportStatus
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import com.ccstudio.mpaz_pro_6.data.model.ReportObjectiveDetail
import com.ccstudio.mpaz_pro_6.data.repository.LearningObjectiveRepository
import com.ccstudio.mpaz_pro_6.data.repository.PedagogicalReportRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import kotlinx.coroutines.launch

/**
 * Estado del detalle de un informe pedagógico.
 *
 * Carga el informe, su detalle por objetivo de aprendizaje y los OA de la unidad
 * para mostrar el código y la descripción. El nivel de cada objetivo llega
 * calculado por el backend.
 */
class PedagogicalReportDetailViewModel(
    private val pedagogicalReportRepository: PedagogicalReportRepository,
    private val unitRepository: UnitRepository,
    private val learningObjectiveRepository: LearningObjectiveRepository,
    private val reportId: Int
) : ViewModel() {

    /** Informe consultado. */
    var report by mutableStateOf<PedagogicalReport?>(null)
        private set

    /** Título de la unidad del informe. */
    var unitTitle by mutableStateOf<String?>(null)
        private set

    /** Detalle por objetivo de aprendizaje. */
    var details by mutableStateOf<List<ReportObjectiveDetail>>(emptyList())
        private set

    /** Objetivos de la unidad, indexados por id. */
    var objectives by mutableStateOf<Map<Int, LearningObjective>>(emptyMap())
        private set

    /** Indica que el detalle se está cargando. */
    var isLoading by mutableStateOf(false)
        private set

    /** Error de la última carga, si lo hubo. */
    var error by mutableStateOf<ApiError?>(null)
        private set

    /** Indica que se está validando el informe. */
    var isValidating by mutableStateOf(false)
        private set

    /** Mensaje de una validación fallida. */
    var mensaje by mutableStateOf<String?>(null)
        private set

    /** Carga el informe, su detalle y los objetivos de la unidad. */
    fun cargar() {
        viewModelScope.launch {
            isLoading = true
            error = null

            val reportResult = pedagogicalReportRepository.getById(reportId)

            if (reportResult.isFailure) {
                error = reportResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            val loadedReport = reportResult.getOrNull()

            if (loadedReport == null) {
                error = ApiError.NotFound("El informe ya no existe.")
                isLoading = false
                return@launch
            }

            report = loadedReport

            // Título y objetivos son datos de apoyo: si fallan, se usa el id.
            unitTitle = unitRepository.getById(loadedReport.unidadId)
                .getOrNull()
                ?.titulo
            objectives = learningObjectiveRepository.list(unitId = loadedReport.unidadId)
                .getOrDefault(emptyList())
                .associateBy { it.id }

            val detailsResult = pedagogicalReportRepository
                .listObjectiveDetails(loadedReport.id)

            if (detailsResult.isFailure) {
                error = detailsResult.exceptionOrNull() as? ApiError
                isLoading = false
                return@launch
            }

            details = detailsResult.getOrDefault(emptyList())
            isLoading = false
        }
    }

    /** Valida el informe. Solo se usa desde la vista del docente. */
    fun validar() {
        val current = report ?: return

        if (isValidating || current.estado == ReportStatus.VALIDATED) return

        viewModelScope.launch {
            isValidating = true
            mensaje = null

            val result = pedagogicalReportRepository.validate(current.id)

            isValidating = false

            if (result.isFailure) {
                val fallo = result.exceptionOrNull() as? ApiError
                mensaje = when (fallo) {
                    is ApiError.Conflict -> "El informe ya estaba validado."
                    is ApiError.NotFound -> "El informe ya no existe."
                    is ApiError.Network -> "Sin conexión. Revisa tu red e intenta de nuevo."
                    is ApiError.Unauthorized -> "Tu sesión venció. Vuelve a iniciar sesión."
                    is ApiError.BadRequest -> fallo.message
                    is ApiError.Unknown -> fallo.message
                    null -> "No se pudo validar el informe."
                }
                return@launch
            }

            report = result.getOrNull() ?: current
        }
    }

    /** Oculta el mensaje de validación. */
    fun limpiarMensaje() {
        mensaje = null
    }

    companion object {

        /** Factoría del detalle con los repositorios y el id del informe. */
        fun factory(
            pedagogicalReportRepository: PedagogicalReportRepository,
            unitRepository: UnitRepository,
            learningObjectiveRepository: LearningObjectiveRepository,
            reportId: Int
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PedagogicalReportDetailViewModel(
                    pedagogicalReportRepository = pedagogicalReportRepository,
                    unitRepository = unitRepository,
                    learningObjectiveRepository = learningObjectiveRepository,
                    reportId = reportId
                )
            }
        }
    }
}
