package com.ccstudio.mpaz_pro_6.ui.utp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ccstudio.mpaz_pro_6.data.local.MpazDatabase
import com.ccstudio.mpaz_pro_6.data.local.dao.ActividadConTipo
import com.ccstudio.mpaz_pro_6.data.local.dao.PreguntaConAlternativas
import com.ccstudio.mpaz_pro_6.data.local.dao.UnidadConDetalles
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ContenidoEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UnitDetailUiState(
    val isLoading: Boolean = true,
    val unidadConDetalles: UnidadConDetalles? = null,
    val contenidos: List<ContenidoEntity> = emptyList(),
    val pruebaFinal: ActividadConTipo? = null,
    val isAuthorizedSuccess: Boolean = false
)

data class ContentDetailUiState(
    val isLoading: Boolean = true,
    val contenido: ContenidoEntity? = null,
    val actividades: List<ActividadConTipo> = emptyList()
)

data class ActivityPreviewUiState(
    val isLoading: Boolean = true,
    val actividadConTipo: ActividadConTipo? = null,
    val preguntas: List<PreguntaConAlternativas> = emptyList()
)

class UtpViewModel(application: Application) : AndroidViewModel(application) {

    private val curriculoDao = MpazDatabase.getDatabase(application).curriculoDao()

    private val _pendingUnits = MutableStateFlow<List<UnidadConDetalles>>(emptyList())
    val pendingUnits: StateFlow<List<UnidadConDetalles>> = _pendingUnits.asStateFlow()

    private val _unitDetailState = MutableStateFlow(UnitDetailUiState())
    val unitDetailState: StateFlow<UnitDetailUiState> = _unitDetailState.asStateFlow()

    private val _contentDetailState = MutableStateFlow(ContentDetailUiState())
    val contentDetailState: StateFlow<ContentDetailUiState> = _contentDetailState.asStateFlow()

    private val _activityPreviewState = MutableStateFlow(ActivityPreviewUiState())
    val activityPreviewState: StateFlow<ActivityPreviewUiState> = _activityPreviewState.asStateFlow()

    init {
        loadPendingUnits()
    }

    fun loadPendingUnits() {
        viewModelScope.launch {
            // estado_id = 2 es 'EN_REVISION'
            val units = curriculoDao.getUnidadesConDetallesByEstadoId(2L)
            _pendingUnits.value = units
        }
    }

    fun loadUnitDetail(unidadId: Long) {
        viewModelScope.launch {
            _unitDetailState.value = UnitDetailUiState(isLoading = true)
            val unidad = curriculoDao.getUnidadConDetallesById(unidadId)
            val contenidos = curriculoDao.getContenidosByUnidadId(unidadId)
            val pruebaFinal = curriculoDao.getPruebaFinalConTipoByUnidadId(unidadId)
            _unitDetailState.value = UnitDetailUiState(
                isLoading = false,
                unidadConDetalles = unidad,
                contenidos = contenidos,
                pruebaFinal = pruebaFinal,
                isAuthorizedSuccess = unidad?.unidad?.estadoId == 3L // 3L = AUTORIZADO
            )
        }
    }

    fun autorizarUnidad(unidadId: Long) {
        viewModelScope.launch {
            // 3L = AUTORIZADO
            curriculoDao.updateUnidadEstado(unidadId, 3L)
            loadPendingUnits()
            loadUnitDetail(unidadId)
        }
    }

    fun loadContentDetail(contenidoId: Long) {
        viewModelScope.launch {
            _contentDetailState.value = ContentDetailUiState(isLoading = true)
            val contenido = curriculoDao.getContenidoById(contenidoId)
            val actividades = curriculoDao.getActividadesConTipoByContenidoId(contenidoId)
            _contentDetailState.value = ContentDetailUiState(
                isLoading = false,
                contenido = contenido,
                actividades = actividades
            )
        }
    }

    fun loadActivityPreview(actividadId: Long) {
        viewModelScope.launch {
            _activityPreviewState.value = ActivityPreviewUiState(isLoading = true)
            val actividad = curriculoDao.getActividadConTipoById(actividadId)
            val preguntas = curriculoDao.getPreguntasConAlternativasByActividadId(actividadId)
            _activityPreviewState.value = ActivityPreviewUiState(
                isLoading = false,
                actividadConTipo = actividad,
                preguntas = preguntas
            )
        }
    }
}
