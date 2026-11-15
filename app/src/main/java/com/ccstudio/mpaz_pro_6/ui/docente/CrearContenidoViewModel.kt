package com.ccstudio.mpaz_pro_6.ui.docente

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ccstudio.mpaz_pro_6.MpazApplication
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ActividadRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ActividadUpdateRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaPreguntaRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ContenidoRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ObjetivoAprendizajeContenidoRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaActividadRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.RecursoRequest
import com.ccstudio.mpaz_pro_6.data.dto.docencia.ActividadCursoRequest
import com.ccstudio.mpaz_pro_6.data.local.MpazDatabase
import com.ccstudio.mpaz_pro_6.data.local.dao.ActividadConTipo
import com.ccstudio.mpaz_pro_6.data.local.dao.AsignaturaCursoDetalle
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AlternativaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AlternativaPreguntaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ContenidoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ObjetivoAprendizajeContenidoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ObjetivoAprendizajeEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.PreguntaActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.PreguntaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.TipoActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.UnidadEntity
import com.ccstudio.mpaz_pro_6.data.local.preferences.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class ActividadSeleccionableItem(
    val actividadConTipo: ActividadConTipo,
    val nombreUnidad: String,
    val descripcionContenido: String,
    val totalPreguntas: Int,
    val primerEnunciado: String?, // null -> "sin preguntas"
)

data class AlternativaCreacion(
    val idTemp: Long = System.currentTimeMillis(),
    val enunciado: String = "",
    val correcta: Boolean = false,
)

data class PreguntaCreacion(
    val idTemp: Long = System.currentTimeMillis(),
    val enunciado: String = "",
    val alternativas: List<AlternativaCreacion> = emptyList(),
)

data class ActividadCreacionTemp(
    val idTemp: Long = System.currentTimeMillis(),
    val idExistente: Long? = null,
    val tipoActividadId: Long = 1,
    val tipoActividadNombre: String = "ACTIVIDAD",
    val instrucciones: String = "",
    val preguntas: List<PreguntaCreacion> = emptyList(),
    val resumenVisual: String = "",
)

data class CrearContenidoUiState(
    val isLoading: Boolean = false,
    val asignaturasDocente: List<AsignaturaCursoDetalle> = emptyList(),
    val unidadesDisponibles: List<UnidadEntity> = emptyList(),
    val unidadSeleccionada: UnidadEntity? = null,
    val objetivosUnidad: List<ObjetivoAprendizajeEntity> = emptyList(),
    val objetivosSeleccionadosIds: Set<Long> = emptySet(),
    val descripcionMarkdown: String = "",
    val isPreviewMode: Boolean = false,
    val actividadesAsociadas: List<ActividadCreacionTemp> = emptyList(),
    val actividadesExistentesDisponibles: List<ActividadSeleccionableItem> = emptyList(),
    val tiposActividad: List<TipoActividadEntity> = emptyList(),
    val isAsociarDialogVisible: Boolean = false,
    val isCrearActividadDialogVisible: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
)

class CrearContenidoViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as MpazApplication).container
    private val db = container.database
    private val curriculoDao = db.curriculoDao()
    private val docenciaDao = db.docenciaDao()
    private val sessionManager = container.sessionManager
    private val localOnly = container.localOnly

    private val _uiState = MutableStateFlow(CrearContenidoUiState())
    val uiState: StateFlow<CrearContenidoUiState> = _uiState.asStateFlow()

    fun initForm() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val session = sessionManager.userSessionFlow.firstOrNull()
            val userId = session?.userId
            val rol = session?.rol

            val asignaturasDocente = if (userId != null && rol == "DOCENTE") {
                docenciaDao.getAsignaturasCursoByDocenteId(userId)
            } else {
                emptyList()
            }

            val unidades: List<UnidadEntity> = if (asignaturasDocente.isNotEmpty()) {
                val asignaturasIds = asignaturasDocente.map { it.asignatura.id }.distinct()
                val list = mutableListOf<UnidadEntity>()
                for (asigId in asignaturasIds) {
                    val unidadesDet = curriculoDao.getUnidadesConDetallesByAsignaturaId(asigId)
                    list.addAll(unidadesDet.map { it.unidad })
                }
                list.distinctBy { it.id }
            } else if (rol == "DOCENTE") {
                // Un docente sin asignaturas no puede crear contenido en
                // unidades de asignaturas que no imparte.
                emptyList()
            } else {
                curriculoDao.getAllUnidades()
            }
            val unidadesPermitidas = unidades.map { it.id }.toSet()

            val tiposActividad = curriculoDao.getAllTiposActividad()

            // Cargar actividades existentes válidas (con contenido_id != null y > 0)
            val actividadesValidasConTipo = curriculoDao.getAllActividadesConTipoValidas()
            val actividadesSeleccionables = mutableListOf<ActividadSeleccionableItem>()

            for (actConTipo in actividadesValidasConTipo) {
                val cId = actConTipo.actividad.contenidoId
                if (cId > 0) {
                    val contenido = curriculoDao.getContenidoById(cId)
                    if (contenido != null && contenido.unidadId in unidadesPermitidas) {
                        val unidad = curriculoDao.getUnidadById(contenido.unidadId)
                        val preguntas = curriculoDao.getPreguntasConAlternativasByActividadId(actConTipo.actividad.id)
                        val primerEnunciado = preguntas.firstOrNull()?.pregunta?.enunciado
                        actividadesSeleccionables.add(
                            ActividadSeleccionableItem(
                                actividadConTipo = actConTipo,
                                nombreUnidad = unidad?.nombre ?: "Sin unidad",
                                descripcionContenido = contenido.descripcion,
                                totalPreguntas = preguntas.size,
                                primerEnunciado = primerEnunciado,
                            ),
                        )
                    }
                }
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                asignaturasDocente = asignaturasDocente,
                unidadesDisponibles = unidades,
                tiposActividad = tiposActividad,
                actividadesExistentesDisponibles = actividadesSeleccionables,
            )
        }
    }

    fun onUnidadSelected(unidad: UnidadEntity) {
        viewModelScope.launch {
            val oas = curriculoDao.getObjetivosAprendizajeByUnidadId(unidad.id)
            _uiState.value = _uiState.value.copy(
                unidadSeleccionada = unidad,
                objetivosUnidad = oas,
                objetivosSeleccionadosIds = emptySet(),
            )
        }
    }

    fun toggleObjetivoSeleccionado(oaId: Long) {
        val current = _uiState.value.objetivosSeleccionadosIds.toMutableSet()
        if (current.contains(oaId)) {
            current.remove(oaId)
        } else {
            current.add(oaId)
        }
        _uiState.value = _uiState.value.copy(objetivosSeleccionadosIds = current)
    }

    fun updateDescripcionMarkdown(markdown: String) {
        _uiState.value = _uiState.value.copy(descripcionMarkdown = markdown, errorMessage = null)
    }

    fun togglePreviewMode() {
        _uiState.value = _uiState.value.copy(isPreviewMode = !_uiState.value.isPreviewMode)
    }

    fun openAsociarDialog() {
        _uiState.value = _uiState.value.copy(isAsociarDialogVisible = true)
    }

    fun closeAsociarDialog() {
        _uiState.value = _uiState.value.copy(isAsociarDialogVisible = false)
    }

    fun asociarActividadExistente(item: ActividadSeleccionableItem) {
        val resumen = "[${item.actividadConTipo.tipoActividad.nombre}] - ${item.nombreUnidad} (${item.totalPreguntas} preguntas)"
        val temp = ActividadCreacionTemp(
            idExistente = item.actividadConTipo.actividad.id,
            tipoActividadId = item.actividadConTipo.tipoActividad.id,
            tipoActividadNombre = item.actividadConTipo.tipoActividad.nombre,
            instrucciones = item.actividadConTipo.actividad.instrucciones,
            resumenVisual = resumen,
        )
        val list = _uiState.value.actividadesAsociadas.toMutableList()
        // Evitar duplicados
        if (list.none { it.idExistente == item.actividadConTipo.actividad.id }) {
            list.add(temp)
        }
        _uiState.value = _uiState.value.copy(
            actividadesAsociadas = list,
            isAsociarDialogVisible = false,
        )
    }

    fun openCrearActividadDialog() {
        _uiState.value = _uiState.value.copy(isCrearActividadDialogVisible = true)
    }

    fun closeCrearActividadDialog() {
        _uiState.value = _uiState.value.copy(isCrearActividadDialogVisible = false)
    }

    fun agregarNuevaActividad(actividadTemp: ActividadCreacionTemp) {
        val list = _uiState.value.actividadesAsociadas.toMutableList()
        list.add(actividadTemp)
        _uiState.value = _uiState.value.copy(
            actividadesAsociadas = list,
            isCrearActividadDialogVisible = false,
        )
    }

    fun eliminarActividadAsociada(idTemp: Long) {
        val list = _uiState.value.actividadesAsociadas.filterNot { it.idTemp == idTemp }
        _uiState.value = _uiState.value.copy(actividadesAsociadas = list)
    }

    fun guardarContenido(onSuccess: () -> Unit) {
        val state = _uiState.value
        val unidad = state.unidadSeleccionada
        if (unidad == null) {
            _uiState.value = state.copy(errorMessage = "Debe seleccionar una unidad.")
            return
        }
        if (state.descripcionMarkdown.isBlank()) {
            _uiState.value = state.copy(errorMessage = "La descripción del contenido no puede estar vacía.")
            return
        }

        if (!localOnly) {
            guardarContenidoRemoto(state, onSuccess)
            return
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            try {
                // 1. Crear Contenido
                val nuevoContenido = ContenidoEntity(
                    descripcion = state.descripcionMarkdown,
                    unidadId = unidad.id,
                    estadoId = 4, // APROBADO
                )
                val nuevoContenidoId = curriculoDao.insertContenido(nuevoContenido)

                // 2. Vincular Objetivos de Aprendizaje
                for (oaId in state.objetivosSeleccionadosIds) {
                    curriculoDao.insertObjetivoAprendizajeContenido(
                        ObjetivoAprendizajeContenidoEntity(
                            contenidoId = nuevoContenidoId,
                            objetivoAprendizajeId = oaId,
                            unidadId = unidad.id,
                        ),
                    )
                }

                // 3. Procesar Actividades (Asociar existentes o insertar nuevas)
                for (actTemp in state.actividadesAsociadas) {
                    if (actTemp.idExistente != null) {
                        val existente = curriculoDao.getActividadById(actTemp.idExistente)
                        if (existente != null) {
                            curriculoDao.updateActividad(existente.copy(contenidoId = nuevoContenidoId))
                        }
                    } else {
                        // Nueva actividad
                        val nuevaActividad = ActividadEntity(
                            instrucciones = actTemp.instrucciones.ifBlank { "Lee atentamente cada pregunta." },
                            contenidoId = nuevoContenidoId,
                            tipoActividadId = actTemp.tipoActividadId,
                            estadoId = 4,
                        )
                        val nuevaActividadId = curriculoDao.insertActividad(nuevaActividad)

                        // Insertar preguntas y alternativas
                        for (pTemp in actTemp.preguntas) {
                            if (pTemp.enunciado.isNotBlank()) {
                                val nuevaPregunta = PreguntaEntity(enunciado = pTemp.enunciado)
                                val nuevaPreguntaId = curriculoDao.insertPregunta(nuevaPregunta)

                                curriculoDao.insertPreguntaActividad(
                                    PreguntaActividadEntity(
                                        actividadId = nuevaActividadId,
                                        preguntaId = nuevaPreguntaId,
                                    ),
                                )

                                for (altTemp in pTemp.alternativas) {
                                    if (altTemp.enunciado.isNotBlank()) {
                                        val nuevaAlt = AlternativaEntity(
                                            enunciado = altTemp.enunciado,
                                            correcta = altTemp.correcta,
                                        )
                                        val nuevaAltId = curriculoDao.insertAlternativa(nuevaAlt)

                                        curriculoDao.insertAlternativaPregunta(
                                            AlternativaPreguntaEntity(
                                                preguntaId = nuevaPreguntaId,
                                                alternativaId = nuevaAltId,
                                            ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "¡Contenido creado exitosamente!",
                )
                limpiarFormulario()
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al guardar el contenido: ${e.localizedMessage}",
                )
            }
        }
    }

    private fun guardarContenidoRemoto(
        state: CrearContenidoUiState,
        onSuccess: () -> Unit
    ) {
        val unidad = state.unidadSeleccionada ?: return
        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true, errorMessage = null)
            try {
                val contenido = container.remoteCatalog.crearContenido(
                    ContenidoRequest(
                        descripcion = state.descripcionMarkdown,
                        unidadId = unidad.id,
                        estadoId = 4
                    )
                )

                // El backend exige un recurso para preguntas y alternativas.
                val recurso = container.remoteCatalog.crearRecurso(
                    RecursoRequest(
                        nombre = "Material de la actividad",
                        url = null,
                        contenidoId = contenido.id,
                        tipoRecursoId = 3
                    )
                )

                state.objetivosSeleccionadosIds.forEach { oaId ->
                    container.remoteCatalog.crearObjetivoAprendizajeContenido(
                        ObjetivoAprendizajeContenidoRequest(
                            contenidoId = contenido.id,
                            objetivoAprendizajeId = oaId,
                            unidadId = unidad.id
                        )
                    )
                }

                val cursosDocente = state.asignaturasDocente
                    .filter { it.asignatura.id == unidad.asignaturaId }
                    .map { it.curso.id }
                    .distinct()

                state.actividadesAsociadas.forEach { actTemp ->
                    if (actTemp.idExistente != null) {
                        container.remoteCatalog.actualizarActividad(
                            actTemp.idExistente,
                            ActividadUpdateRequest(contenidoId = contenido.id)
                        )
                    } else {
                        val actividad = container.remoteCatalog.crearActividad(
                            ActividadRequest(
                                instrucciones = actTemp.instrucciones
                                    .ifBlank { "Lee atentamente cada pregunta." },
                                contenidoId = contenido.id,
                                tipoActividadId = actTemp.tipoActividadId,
                                estadoId = 4
                            )
                        )

                        actTemp.preguntas.forEach { pTemp ->
                            if (pTemp.enunciado.isNotBlank()) {
                                val pregunta = container.remoteCatalog.crearPregunta(
                                    PreguntaRequest(
                                        enunciado = pTemp.enunciado,
                                        recursoId = recurso.id
                                    )
                                )
                                container.remoteCatalog.crearPreguntaActividad(
                                    PreguntaActividadRequest(
                                        actividadId = actividad.id,
                                        preguntaId = pregunta.id
                                    )
                                )

                                pTemp.alternativas.forEach { altTemp ->
                                    if (altTemp.enunciado.isNotBlank()) {
                                        val alternativa = container.remoteCatalog.crearAlternativa(
                                            AlternativaRequest(
                                                enunciado = altTemp.enunciado,
                                                correcta = altTemp.correcta,
                                                recursoId = recurso.id
                                            )
                                        )
                                        container.remoteCatalog.crearAlternativaPregunta(
                                            AlternativaPreguntaRequest(
                                                preguntaId = pregunta.id,
                                                alternativaId = alternativa.id
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        cursosDocente.forEach { cursoId ->
                            runCatching {
                                container.remoteCatalog.crearActividadCurso(
                                    ActividadCursoRequest(
                                        actividadId = actividad.id,
                                        cursoId = cursoId,
                                        fechaInicio = null,
                                        fechaFin = null,
                                        habilitada = true,
                                        visible = true,
                                        maxIntentos = 3
                                    )
                                )
                            }
                        }
                    }
                }

                val refresh = container.refresh()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = null,
                    successMessage = if (refresh.isFailure) {
                        "Contenido creado, pero no se pudo refrescar la caché local."
                    } else {
                        "¡Contenido creado exitosamente!"
                    }
                )
                limpiarFormulario()
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al guardar el contenido: ${e.message}"
                )
            }
        }
    }

    fun limpiarFormulario() {
        _uiState.value = _uiState.value.copy(
            unidadSeleccionada = null,
            objetivosUnidad = emptyList(),
            objetivosSeleccionadosIds = emptySet(),
            descripcionMarkdown = "",
            isPreviewMode = false,
            actividadesAsociadas = emptyList(),
            errorMessage = null,
        )
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
