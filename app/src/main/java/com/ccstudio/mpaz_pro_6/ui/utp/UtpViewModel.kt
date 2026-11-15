package com.ccstudio.mpaz_pro_6.ui.utp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ccstudio.mpaz_pro_6.MpazApplication
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ActividadUpdateRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaPreguntaRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaUpdateRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaActividadRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaUpdateRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.RecursoRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.UnidadRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.UnidadUpdateRequest
import com.ccstudio.mpaz_pro_6.data.dto.docencia.ActividadCursoUpdateRequest
import com.ccstudio.mpaz_pro_6.data.dto.docencia.AsignaturaCursoRequest
import com.ccstudio.mpaz_pro_6.data.dto.docencia.AsignaturaCursoUpdateRequest
import com.ccstudio.mpaz_pro_6.data.dto.docencia.UsuarioCursoRequest
import com.ccstudio.mpaz_pro_6.data.dto.identidad.UsuarioRequest
import com.ccstudio.mpaz_pro_6.data.local.MpazDatabase
import com.ccstudio.mpaz_pro_6.data.local.dao.ActividadConTipo
import com.ccstudio.mpaz_pro_6.data.local.dao.ActividadCursoConDetalles
import com.ccstudio.mpaz_pro_6.data.local.dao.AsignaturaConDocente
import com.ccstudio.mpaz_pro_6.data.local.dao.AsignaturaCursoDetalle
import com.ccstudio.mpaz_pro_6.data.local.dao.CursoConDetalles
import com.ccstudio.mpaz_pro_6.data.local.dao.PreguntaConAlternativas
import com.ccstudio.mpaz_pro_6.data.local.dao.UnidadConDetalles
import com.ccstudio.mpaz_pro_6.data.local.dao.UsuarioConRol
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AlternativaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AlternativaPreguntaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AsignaturaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ContenidoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.PreguntaActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.PreguntaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.UnidadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.ActividadCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.AsignaturaCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.UsuarioCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.RolEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity
import com.ccstudio.mpaz_pro_6.data.local.preferences.SessionManager
import com.ccstudio.mpaz_pro_6.data.remote.toEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.ZoneId
import com.ccstudio.mpaz_pro_6.ui.util.parseFechaHora

data class UnitDetailUiState(
    val isLoading: Boolean = true,
    val unidadConDetalles: UnidadConDetalles? = null,
    val cursoConDetalles: CursoConDetalles? = null,
    val contenidos: List<ContenidoEntity> = emptyList(),
    val pruebaFinal: ActividadConTipo? = null,
    val isAuthorizedSuccess: Boolean = false
)

data class ContentDetailUiState(
    val isLoading: Boolean = true,
    val contenido: ContenidoEntity? = null,
    val unidadConDetalles: UnidadConDetalles? = null,
    val cursoConDetalles: CursoConDetalles? = null,
    val actividades: List<ActividadConTipo> = emptyList()
)

data class ActivityPreviewUiState(
    val isLoading: Boolean = true,
    val actividadConTipo: ActividadConTipo? = null,
    val unidadConDetalles: UnidadConDetalles? = null,
    val cursoConDetalles: CursoConDetalles? = null,
    val preguntas: List<PreguntaConAlternativas> = emptyList(),
    val actividadesCurso: List<ActividadCursoConDetalles> = emptyList(),
    val allCursos: List<CursoConDetalles> = emptyList(),
    val isEditing: Boolean = false,
    val actionMessage: String? = null
)

data class CursosUiState(
    val isLoading: Boolean = true,
    val cursos: List<CursoConDetalles> = emptyList()
)

data class CursoDetailUiState(
    val isLoading: Boolean = true,
    val cursoConDetalles: CursoConDetalles? = null,
    val asignaturas: List<AsignaturaEntity> = emptyList(),
    val asignaturasConDocente: List<AsignaturaConDocente> = emptyList(),
    val docentes: List<UsuarioEntity> = emptyList(),
    val estudiantes: List<UsuarioEntity> = emptyList(),
    val availableAsignaturas: List<AsignaturaEntity> = emptyList(),
    val availableDocentes: List<UsuarioEntity> = emptyList(),
    val availableEstudiantes: List<UsuarioEntity> = emptyList(),
    val allDocentes: List<UsuarioEntity> = emptyList()
)

data class AsignaturaDetailUiState(
    val isLoading: Boolean = true,
    val asignatura: AsignaturaEntity? = null,
    val cursoConDetalles: CursoConDetalles? = null,
    val unidades: List<UnidadConDetalles> = emptyList()
)

data class DocenteWithAssignments(
    val docente: UsuarioEntity,
    val asignaturasCurso: List<AsignaturaCursoDetalle> = emptyList()
)

data class DocentesUiState(
    val isLoading: Boolean = true,
    val docentes: List<DocenteWithAssignments> = emptyList(),
    val searchQuery: String = ""
)

data class DocenteDetailUiState(
    val isLoading: Boolean = true,
    val docente: UsuarioEntity? = null,
    val asignaturasAsignadas: List<AsignaturaCursoDetalle> = emptyList(),
    val availableAsignaturasCurso: List<AsignaturaCursoDetalle> = emptyList()
)

data class UsuariosUiState(
    val isLoading: Boolean = true,
    val usuarios: List<UsuarioConRol> = emptyList(),
    val searchQuery: String = "",
    val selectedRolFilter: Long? = null,
    val errorMessage: String? = null
)
    val currentUserId: Long? = null,

data class CrearUsuarioUiState(
    val nombreUsuario: String = "",
    val password: String = "mpaz123",
    val pnombre: String = "",
    val snombre: String = "",
    val apaterno: String = "",
    val amaterno: String = "",
    val rolId: Long = 3L,
    val activo: Boolean = true,
    val roles: List<RolEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class UtpViewModel(application: Application) : AndroidViewModel(application) {

    private val container = (application as MpazApplication).container
    private val db = container.database
    private val curriculoDao = db.curriculoDao()
    private val identidadDao = db.identidadDao()
    private val docenciaDao = db.docenciaDao()
    private val sessionManager = container.sessionManager
    private val localOnly = container.localOnly

    private val _pendingUnits = MutableStateFlow<List<UnidadConDetalles>>(emptyList())
    val pendingUnits: StateFlow<List<UnidadConDetalles>> = _pendingUnits.asStateFlow()

    private val _unitDetailState = MutableStateFlow(UnitDetailUiState())
    val unitDetailState: StateFlow<UnitDetailUiState> = _unitDetailState.asStateFlow()

    private val _contentDetailState = MutableStateFlow(ContentDetailUiState())
    val contentDetailState: StateFlow<ContentDetailUiState> = _contentDetailState.asStateFlow()

    private val _activityPreviewState = MutableStateFlow(ActivityPreviewUiState())
    val activityPreviewState: StateFlow<ActivityPreviewUiState> = _activityPreviewState.asStateFlow()

    private val _cursosState = MutableStateFlow(CursosUiState())
    val cursosState: StateFlow<CursosUiState> = _cursosState.asStateFlow()

    private val _cursoDetailState = MutableStateFlow(CursoDetailUiState())
    val cursoDetailState: StateFlow<CursoDetailUiState> = _cursoDetailState.asStateFlow()

    private val _asignaturaDetailState = MutableStateFlow(AsignaturaDetailUiState())
    val asignaturaDetailState: StateFlow<AsignaturaDetailUiState> = _asignaturaDetailState.asStateFlow()

    private val _docentesState = MutableStateFlow(DocentesUiState())
    val docentesState: StateFlow<DocentesUiState> = _docentesState.asStateFlow()

    private val _docenteDetailState = MutableStateFlow(DocenteDetailUiState())
    val docenteDetailState: StateFlow<DocenteDetailUiState> = _docenteDetailState.asStateFlow()

    private val _usuariosState = MutableStateFlow(UsuariosUiState())
    val usuariosState: StateFlow<UsuariosUiState> = _usuariosState.asStateFlow()

    private val _crearUsuarioState = MutableStateFlow(CrearUsuarioUiState())
    val crearUsuarioState: StateFlow<CrearUsuarioUiState> = _crearUsuarioState.asStateFlow()

    private val _docenteAsignaturas = MutableStateFlow<List<AsignaturaCursoDetalle>>(emptyList())
    /**
     * Lista de asignaturas y cursos asignados al docente autenticado en sesión.
     */
    val docenteAsignaturas: StateFlow<List<AsignaturaCursoDetalle>> = _docenteAsignaturas.asStateFlow()

    private val _currentUserName = MutableStateFlow<String>("")
    /**
     * Nombre del usuario autenticado en la sesión activa (primer nombre o nombre de usuario en su defecto).
     */
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    init {
        refreshInitialData()
        viewModelScope.launch {
            // El arranque con sesion guardada muestra Home antes de que
            // termine el sync. Al terminar, se recargan los contadores y
            // nombres con los datos definitivos de Room.
            container.syncCompleted.drop(1).collect {
                refreshInitialData()
            }
        }
    }

    private fun refreshInitialData() {
        loadCurrentUserName()
        loadPendingUnits()
        loadCursos()
        loadDocentes()
        loadUsuarios()
        loadDocenteAsignaturas()
    }

    /**
     * Carga las asignaturas asignadas al docente (o todas las asignaturas para UTP) según el usuario en sesión.
     */
    fun loadDocenteAsignaturas() {
        viewModelScope.launch {
            val session = sessionManager.userSessionFlow.firstOrNull()
            val userId = session?.userId
            val rol = session?.rol ?: ""
            if (userId != null) {
                val asignaturas = if (rol.equals("DOCENTE", ignoreCase = true)) {
                    docenciaDao.getAsignaturasCursoByDocenteId(userId)
                } else if (rol.equals("ESTUDIANTE", ignoreCase = true)) {
                    docenciaDao.getAsignaturasCursoByEstudianteId(userId)
                } else {
                    docenciaDao.getAllAsignaturasCursoDetalles()
                }
                _docenteAsignaturas.value = asignaturas
            }
        }
    }

    /**
     * Carga el primer nombre (`pnombre`) del usuario en sesión desde la base de datos local.
     */
    fun loadCurrentUserName() {
        viewModelScope.launch {
            val session = sessionManager.userSessionFlow.firstOrNull()
            if (session != null) {
                val usuario = identidadDao.getUsuarioByNombre(session.username)
                val nombre = usuario?.pnombre?.ifBlank { null } ?: session.username
                _currentUserName.value = nombre
            }
        }
    }

    fun loadPendingUnits() {
        viewModelScope.launch {
            // estado_id = 2 es 'EN_REVISION'
            val units = curriculoDao.getUnidadesConDetallesByEstadoId(2L)
            _pendingUnits.value = units
        }
    }

    fun loadUnitDetail(unidadId: Long, cursoId: Long? = null) {
        viewModelScope.launch {
            _unitDetailState.value = UnitDetailUiState(isLoading = true)
            val unidad = curriculoDao.getUnidadConDetallesById(unidadId)
            val contenidos = curriculoDao.getContenidosByUnidadId(unidadId)
            val pruebaFinal = curriculoDao.getPruebaFinalConTipoByUnidadId(unidadId)

            val targetCursoId = if (cursoId != null && cursoId != 0L) {
                cursoId
            } else if (unidad != null) {
                docenciaDao.getCursosByAsignaturaId(unidad.unidad.asignaturaId).firstOrNull()?.cursoId
            } else null

            val cursoConDetalles = targetCursoId?.let { identidadDao.getCursoConDetallesById(it) }

            _unitDetailState.value = UnitDetailUiState(
                isLoading = false,
                unidadConDetalles = unidad,
                cursoConDetalles = cursoConDetalles,
                contenidos = contenidos,
                pruebaFinal = pruebaFinal,
                isAuthorizedSuccess = unidad?.unidad?.estadoId == 3L // 3L = AUTORIZADO
            )
        }
    }

    fun autorizarUnidad(unidadId: Long, cursoId: Long? = null) {
        viewModelScope.launch {
            if (!localOnly) {
                container.remoteCatalog.actualizarUnidad(
                    unidadId,
                    UnidadUpdateRequest(estadoId = 3L)
                )
                container.refresh()
                loadPendingUnits()
                loadUnitDetail(unidadId, cursoId ?: _unitDetailState.value.cursoConDetalles?.curso?.id)
                return@launch
            }
            // 3L = AUTORIZADO
            curriculoDao.updateUnidadEstado(unidadId, 3L)
            loadPendingUnits()
            loadUnitDetail(unidadId, cursoId ?: _unitDetailState.value.cursoConDetalles?.curso?.id)
        }
    }

    fun cambiarEstadoUnidadBorrador(unidadId: Long, cursoId: Long? = null) {
        viewModelScope.launch {
            if (!localOnly) {
                container.remoteCatalog.actualizarUnidad(
                    unidadId,
                    UnidadUpdateRequest(estadoId = 1L)
                )
                container.refresh()
                loadPendingUnits()
                loadUnitDetail(unidadId, cursoId ?: _unitDetailState.value.cursoConDetalles?.curso?.id)
                return@launch
            }
            // 1L = BORRADOR
            curriculoDao.updateUnidadEstado(unidadId, 1L)
            loadPendingUnits()
            loadUnitDetail(unidadId, cursoId ?: _unitDetailState.value.cursoConDetalles?.curso?.id)
        }
    }

    fun loadContentDetail(contenidoId: Long, cursoId: Long? = null) {
        viewModelScope.launch {
            _contentDetailState.value = ContentDetailUiState(isLoading = true)
            val contenido = curriculoDao.getContenidoById(contenidoId)
            val unidad = contenido?.let { curriculoDao.getUnidadConDetallesById(it.unidadId) }
            val actividades = curriculoDao.getActividadesConTipoByContenidoId(contenidoId)

            val targetCursoId = if (cursoId != null && cursoId != 0L) {
                cursoId
            } else if (unidad != null) {
                docenciaDao.getCursosByAsignaturaId(unidad.unidad.asignaturaId).firstOrNull()?.cursoId
            } else null

            val cursoConDetalles = targetCursoId?.let { identidadDao.getCursoConDetallesById(it) }

            _contentDetailState.value = ContentDetailUiState(
                isLoading = false,
                contenido = contenido,
                unidadConDetalles = unidad,
                cursoConDetalles = cursoConDetalles,
                actividades = actividades
            )
        }
    }

    fun loadActivityPreview(actividadId: Long, cursoId: Long? = null) {
        viewModelScope.launch {
            _activityPreviewState.value = ActivityPreviewUiState(isLoading = true)
            val actividad = curriculoDao.getActividadConTipoById(actividadId)
            val preguntas = curriculoDao.getPreguntasConAlternativasByActividadId(actividadId)
            var actividadesCurso = docenciaDao.getActividadesCursoConDetallesByActividadId(actividadId)
            val allCursos = identidadDao.getCursosConDetalles()

            if (localOnly && actividadesCurso.isEmpty() && allCursos.isNotEmpty()) {
                allCursos.forEach { cursoDetalle ->
                    docenciaDao.insertActividadCurso(
                        ActividadCursoEntity(
                            actividadId = actividadId,
                            cursoId = cursoDetalle.curso.id,
                            habilitada = true,
                            visible = true,
                            maxIntentos = 3
                        )
                    )
                }
                actividadesCurso = docenciaDao.getActividadesCursoConDetallesByActividadId(actividadId)
            }

            val contenido = actividad?.actividad?.contenidoId?.let { curriculoDao.getContenidoById(it) }
            val unidad = contenido?.let { curriculoDao.getUnidadConDetallesById(it.unidadId) }

            val targetCursoId = if (cursoId != null && cursoId != 0L) {
                cursoId
            } else if (unidad != null) {
                docenciaDao.getCursosByAsignaturaId(unidad.unidad.asignaturaId).firstOrNull()?.cursoId
            } else {
                actividadesCurso.firstOrNull()?.curso?.curso?.id
            }

            val cursoConDetalles = targetCursoId?.let { identidadDao.getCursoConDetallesById(it) }

            _activityPreviewState.value = ActivityPreviewUiState(
                isLoading = false,
                actividadConTipo = actividad,
                unidadConDetalles = unidad,
                cursoConDetalles = cursoConDetalles,
                preguntas = preguntas,
                actividadesCurso = actividadesCurso,
                allCursos = allCursos
            )
        }
    }

    fun updateActividadCursoConfig(
        actividadCursoId: Long,
        visible: Boolean,
        habilitada: Boolean,
        maxIntentos: Int?,
        fechaInicio: String?,
        fechaFin: String?
    ) {
        viewModelScope.launch {
            val current = docenciaDao.getActividadCursoById(actividadCursoId) ?: return@launch

            if (!localOnly) {
                runCatching {
                    container.remoteCatalog.actualizarActividadCurso(
                        actividadCursoId,
                        ActividadCursoUpdateRequest(
                            fechaInicio = fechaIso(fechaInicio),
                            fechaFin = fechaIso(fechaFin),
                            maxIntentos = maxIntentos
                        )
                    )
                    container.remoteCatalog.cambiarActividadCursoHabilitada(
                        actividadCursoId, habilitada
                    )
                    container.remoteCatalog.cambiarActividadCursoVisible(
                        actividadCursoId, visible
                    )
                    container.refresh()
                }
                loadActivityPreview(current.actividadId)
                return@launch
            }

            val updated = current.copy(
                visible = visible,
                habilitada = habilitada,
                maxIntentos = maxIntentos,
                fechaInicio = fechaInicio?.ifBlank { null },
                fechaFin = fechaFin?.ifBlank { null }
            )
            docenciaDao.updateActividadCurso(updated)
            loadActivityPreview(current.actividadId)
        }
    }

    fun updateActividadInstrucciones(actividadId: Long, instrucciones: String) {
        viewModelScope.launch {
            if (!localOnly) {
                runCatching {
                    container.remoteCatalog.actualizarActividad(
                        actividadId,
                        ActividadUpdateRequest(instrucciones = instrucciones)
                    )
                    container.refresh()
                }
                loadActivityPreview(actividadId)
                return@launch
            }
            val actividad = curriculoDao.getActividadById(actividadId)
            if (actividad != null) {
                curriculoDao.updateActividad(actividad.copy(instrucciones = instrucciones))
                loadActivityPreview(actividadId)
            }
        }
    }

    fun updatePreguntaEnunciado(actividadId: Long, preguntaId: Long, nuevoEnunciado: String) {
        viewModelScope.launch {
            val pregunta = curriculoDao.getPreguntaById(preguntaId) ?: return@launch

            if (!localOnly) {
                runCatching {
                    container.remoteCatalog.actualizarPregunta(
                        preguntaId,
                        PreguntaUpdateRequest(
                            enunciado = nuevoEnunciado,
                            recursoId = pregunta.recursoId
                        )
                    )
                    container.refresh()
                }
                loadActivityPreview(actividadId)
                return@launch
            }

            curriculoDao.updatePregunta(pregunta.copy(enunciado = nuevoEnunciado))
            loadActivityPreview(actividadId)
        }
    }

    fun updateAlternativaEnunciado(actividadId: Long, alternativaId: Long, nuevoEnunciado: String) {
        viewModelScope.launch {
            val alternativa = curriculoDao.getAlternativaById(alternativaId) ?: return@launch

            if (!localOnly) {
                runCatching {
                    container.remoteCatalog.actualizarAlternativa(
                        alternativaId,
                        AlternativaUpdateRequest(
                            enunciado = nuevoEnunciado,
                            recursoId = alternativa.recursoId
                        )
                    )
                    container.refresh()
                }
                loadActivityPreview(actividadId)
                return@launch
            }

            curriculoDao.updateAlternativa(alternativa.copy(enunciado = nuevoEnunciado))
            loadActivityPreview(actividadId)
        }
    }

    fun setAlternativaCorrecta(actividadId: Long, preguntaId: Long, alternativaIdCorrecta: Long) {
        viewModelScope.launch {
            val rels = curriculoDao.getAlternativasByPreguntaId(preguntaId)

            if (!localOnly) {
                runCatching {
                    rels.forEach { rel ->
                        container.remoteCatalog.cambiarAlternativaCorrecta(
                            rel.alternativaId,
                            rel.alternativaId == alternativaIdCorrecta
                        )
                    }
                    container.refresh()
                }
                loadActivityPreview(actividadId)
                return@launch
            }

            rels.forEach { rel ->
                val alt = curriculoDao.getAlternativaById(rel.alternativaId)
                if (alt != null) {
                    val shouldBeCorrect = (alt.id == alternativaIdCorrecta)
                    if (alt.correcta != shouldBeCorrect) {
                        curriculoDao.updateAlternativa(alt.copy(correcta = shouldBeCorrect))
                    }
                }
            }
            loadActivityPreview(actividadId)
        }
    }

    fun addPreguntaToActividad(
        actividadId: Long,
        enunciado: String,
        alternativasTexto: List<String>,
        correctaIndex: Int
    ) {
        viewModelScope.launch {
            if (!localOnly) {
                val recursoId = recursoIdParaActividad(actividadId) ?: return@launch
                runCatching {
                    val pregunta = container.remoteCatalog.crearPregunta(
                        PreguntaRequest(enunciado = enunciado, recursoId = recursoId)
                    )
                    container.remoteCatalog.crearPreguntaActividad(
                        PreguntaActividadRequest(
                            actividadId = actividadId,
                            preguntaId = pregunta.id
                        )
                    )
                    alternativasTexto.forEachIndexed { index, texto ->
                        val alternativa = container.remoteCatalog.crearAlternativa(
                            AlternativaRequest(
                                enunciado = texto,
                                correcta = index == correctaIndex,
                                recursoId = recursoId
                            )
                        )
                        container.remoteCatalog.crearAlternativaPregunta(
                            AlternativaPreguntaRequest(
                                preguntaId = pregunta.id,
                                alternativaId = alternativa.id
                            )
                        )
                    }
                    container.refresh()
                }
                loadActivityPreview(actividadId)
                return@launch
            }

            val preguntaId = curriculoDao.insertPregunta(
                PreguntaEntity(enunciado = enunciado)
            )
            curriculoDao.insertPreguntaActividad(
                PreguntaActividadEntity(actividadId = actividadId, preguntaId = preguntaId)
            )
            alternativasTexto.forEachIndexed { index, texto ->
                val altId = curriculoDao.insertAlternativa(
                    AlternativaEntity(enunciado = texto, correcta = (index == correctaIndex))
                )
                curriculoDao.insertAlternativaPregunta(
                    AlternativaPreguntaEntity(preguntaId = preguntaId, alternativaId = altId)
                )
            }
            loadActivityPreview(actividadId)
        }
    }

    fun deletePreguntaFromActividad(actividadId: Long, preguntaId: Long) {
        viewModelScope.launch {
            val altRels = curriculoDao.getAlternativasByPreguntaId(preguntaId)

            if (!localOnly) {
                runCatching {
                    altRels.forEach { rel ->
                        container.remoteCatalog.eliminarAlternativaPregunta(rel.id)
                        container.remoteCatalog.eliminarAlternativa(rel.alternativaId)
                    }
                    curriculoDao.getPreguntaActividadByActividadYPregunta(
                        actividadId, preguntaId
                    )?.let { asociacion ->
                        container.remoteCatalog.eliminarPreguntaActividad(asociacion.id)
                    }
                    container.remoteCatalog.eliminarPregunta(preguntaId)
                    container.refresh()
                }
                loadActivityPreview(actividadId)
                return@launch
            }

            altRels.forEach { rel ->
                curriculoDao.deleteAlternativaPreguntaByAlternativaId(rel.alternativaId)
                val alt = curriculoDao.getAlternativaById(rel.alternativaId)
                if (alt != null) curriculoDao.deleteAlternativa(alt)
            }
            curriculoDao.deletePreguntaActividadByActividadYPregunta(actividadId, preguntaId)
            val pregunta = curriculoDao.getPreguntaById(preguntaId)
            if (pregunta != null) curriculoDao.deletePregunta(pregunta)
            loadActivityPreview(actividadId)
        }
    }

    fun addAlternativaToPregunta(actividadId: Long, preguntaId: Long, enunciado: String, correcta: Boolean) {
        viewModelScope.launch {
            if (!localOnly) {
                val recursoId = recursoIdParaActividad(actividadId) ?: return@launch
                runCatching {
                    val rels = curriculoDao.getAlternativasByPreguntaId(preguntaId)
                    if (correcta) {
                        rels.forEach { rel ->
                            container.remoteCatalog.cambiarAlternativaCorrecta(
                                rel.alternativaId, false
                            )
                        }
                    }
                    val alternativa = container.remoteCatalog.crearAlternativa(
                        AlternativaRequest(
                            enunciado = enunciado,
                            correcta = correcta,
                            recursoId = recursoId
                        )
                    )
                    container.remoteCatalog.crearAlternativaPregunta(
                        AlternativaPreguntaRequest(
                            preguntaId = preguntaId,
                            alternativaId = alternativa.id
                        )
                    )
                    container.refresh()
                }
                loadActivityPreview(actividadId)
                return@launch
            }

            if (correcta) {
                val rels = curriculoDao.getAlternativasByPreguntaId(preguntaId)
                rels.forEach { rel ->
                    val alt = curriculoDao.getAlternativaById(rel.alternativaId)
                    if (alt != null && alt.correcta) {
                        curriculoDao.updateAlternativa(alt.copy(correcta = false))
                    }
                }
            }
            val altId = curriculoDao.insertAlternativa(
                AlternativaEntity(enunciado = enunciado, correcta = correcta)
            )
            curriculoDao.insertAlternativaPregunta(
                AlternativaPreguntaEntity(preguntaId = preguntaId, alternativaId = altId)
            )
            loadActivityPreview(actividadId)
        }
    }

    fun deleteAlternativaFromPregunta(actividadId: Long, preguntaId: Long, alternativaId: Long) {
        viewModelScope.launch {
            val altToDelete = curriculoDao.getAlternativaById(alternativaId)
            val relToDelete = curriculoDao.getAlternativasByPreguntaId(preguntaId)
                .firstOrNull { it.alternativaId == alternativaId }

            if (!localOnly) {
                runCatching {
                    if (relToDelete != null) {
                        container.remoteCatalog.eliminarAlternativaPregunta(relToDelete.id)
                    }
                    container.remoteCatalog.eliminarAlternativa(alternativaId)

                    if (altToDelete?.correcta == true) {
                        val remaining = curriculoDao.getAlternativasByPreguntaId(preguntaId)
                            .filterNot { it.alternativaId == alternativaId }
                        remaining.firstOrNull()?.let { rel ->
                            container.remoteCatalog.cambiarAlternativaCorrecta(
                                rel.alternativaId, true
                            )
                        }
                    }
                    container.refresh()
                }
                loadActivityPreview(actividadId)
                return@launch
            }

            curriculoDao.deleteAlternativaPreguntaByAlternativaId(alternativaId)
            if (altToDelete != null) {
                curriculoDao.deleteAlternativa(altToDelete)
            }
            if (altToDelete?.correcta == true) {
                val remainingRels = curriculoDao.getAlternativasByPreguntaId(preguntaId)
                if (remainingRels.isNotEmpty()) {
                    val firstAlt = curriculoDao.getAlternativaById(remainingRels.first().alternativaId)
                    if (firstAlt != null) {
                        curriculoDao.updateAlternativa(firstAlt.copy(correcta = true))
                    }
                }
            }
            loadActivityPreview(actividadId)
        }
    }

    private fun fechaIso(value: String?): String? {
        if (value.isNullOrBlank()) return null
        return parseFechaHora(value)
            ?.atZone(ZoneId.systemDefault())
            ?.toInstant()
            ?.toString()
    }

    private suspend fun recursoIdParaActividad(actividadId: Long): Long? {
        val actividad = curriculoDao.getActividadById(actividadId) ?: return null
        val recursos = curriculoDao.getAllRecursos()
            .filter { it.contenidoId == actividad.contenidoId }
        if (recursos.isNotEmpty()) {
            return recursos.first().id
        }
        return container.remoteCatalog.crearRecurso(
            RecursoRequest(
                nombre = "Material de la actividad",
                url = null,
                contenidoId = actividad.contenidoId,
                tipoRecursoId = 3
            )
        ).id
    }

    // --- CURSOS ---

    fun loadCursos() {
        viewModelScope.launch {
            _cursosState.value = _cursosState.value.copy(isLoading = true)
            val session = sessionManager.userSessionFlow.firstOrNull()
            val cursos = if (session != null && session.rol.equals("DOCENTE", ignoreCase = true)) {
                identidadDao.getCursosConDetallesByDocenteId(session.userId)
            } else {
                identidadDao.getCursosConDetalles()
            }
            _cursosState.value = _cursosState.value.copy(
                isLoading = false,
                cursos = cursos
            )
        }
    }

    fun loadCursoDetail(cursoId: Long) {
        viewModelScope.launch {
            _cursoDetailState.value = CursoDetailUiState(isLoading = true)
            val session = sessionManager.userSessionFlow.firstOrNull()
            val isDocente = session?.rol?.equals("DOCENTE", ignoreCase = true) == true
            val docenteId = session?.userId

            val cursoConDetalles = identidadDao.getCursoConDetallesById(cursoId)
            var asignaturas = docenciaDao.getAsignaturasEntityByCursoId(cursoId)
            var asignaturasConDocente = docenciaDao.getAsignaturasConDocenteByCursoId(cursoId)

            if (isDocente && docenteId != null) {
                asignaturasConDocente = asignaturasConDocente.filter { it.docente?.id == docenteId }
                val assignedAsigIds = asignaturasConDocente.map { it.asignatura.id }.toSet()
                asignaturas = asignaturas.filter { it.id in assignedAsigIds }
            }

            val docentes = docenciaDao.getDocentesByCursoId(cursoId)
            val estudiantes = docenciaDao.getEstudiantesByCursoId(cursoId)

            val nivelId = cursoConDetalles?.curso?.nivelId ?: 1L
            val allNivelAsignaturas = curriculoDao.getAsignaturasByNivelId(nivelId)
            val availableAsignaturas = allNivelAsignaturas.filter { nasig ->
                asignaturas.none { it.id == nasig.id }
            }

            val allDocentes = identidadDao.getAllDocentes()
            val availableDocentes = allDocentes.filter { doc ->
                docentes.none { it.id == doc.id }
            }

            val allEstudiantes = identidadDao.getAllEstudiantes()
            val availableEstudiantes = allEstudiantes.filter { est ->
                estudiantes.none { it.id == est.id }
            }

            _cursoDetailState.value = CursoDetailUiState(
                isLoading = false,
                cursoConDetalles = cursoConDetalles,
                asignaturas = asignaturas,
                asignaturasConDocente = asignaturasConDocente,
                docentes = docentes,
                estudiantes = estudiantes,
                availableAsignaturas = availableAsignaturas,
                availableDocentes = availableDocentes,
                availableEstudiantes = availableEstudiantes,
                allDocentes = allDocentes
            )
        }
    }

    fun addAsignaturaToCurso(cursoId: Long, asignaturaId: Long, docenteId: Long? = null) {
        viewModelScope.launch {
            if (!localOnly) {
                runCatching { guardarDocencia(cursoId, asignaturaId, docenteId) }
                loadCursoDetail(cursoId)
                return@launch
            }
            docenciaDao.insertAsignaturaCurso(
                AsignaturaCursoEntity(cursoId = cursoId, asignaturaId = asignaturaId, docenteId = docenteId)
            )
            if (docenteId != null) {
                docenciaDao.insertUsuarioCurso(
                    UsuarioCursoEntity(usuarioId = docenteId, cursoId = cursoId)
                )
            }
            loadCursoDetail(cursoId)
        }
    }

    fun createAndAddAsignaturaToCurso(cursoId: Long, nombre: String, nivelId: Long, docenteId: Long? = null) {
        viewModelScope.launch {
            if (!localOnly) {
                runCatching {
                    val asignatura = container.remoteCatalog.crearAsignatura(
                        com.ccstudio.mpaz_pro_6.data.dto.curriculo.AsignaturaRequest(
                            nombre = nombre,
                            nivelId = nivelId
                        )
                    )
                    guardarDocencia(cursoId, asignatura.id, docenteId)
                }
                loadCursoDetail(cursoId)
                return@launch
            }
            val newAsigId = curriculoDao.insertAsignatura(
                AsignaturaEntity(nombre = nombre, nivelId = nivelId)
            )
            docenciaDao.insertAsignaturaCurso(
                AsignaturaCursoEntity(cursoId = cursoId, asignaturaId = newAsigId, docenteId = docenteId)
            )
            if (docenteId != null) {
                docenciaDao.insertUsuarioCurso(
                    UsuarioCursoEntity(usuarioId = docenteId, cursoId = cursoId)
                )
            }
            loadCursoDetail(cursoId)
        }
    }

    fun assignDocenteToAsignaturaCurso(cursoId: Long, asignaturaId: Long, docenteId: Long?) {
        viewModelScope.launch {
            if (!localOnly) {
                runCatching { guardarDocencia(cursoId, asignaturaId, docenteId) }
                loadCursoDetail(cursoId)
                return@launch
            }
            docenciaDao.updateDocenteInAsignaturaCurso(cursoId, asignaturaId, docenteId)
            if (docenteId != null) {
                docenciaDao.insertUsuarioCurso(
                    UsuarioCursoEntity(usuarioId = docenteId, cursoId = cursoId)
                )
            }
            loadCursoDetail(cursoId)
        }
    }

    fun removeAsignaturaFromCurso(cursoId: Long, asignaturaId: Long) {
        viewModelScope.launch {
            if (!localOnly) {
                runCatching {
                    val existente = docenciaDao.getAsignaturasByCursoId(cursoId)
                        .firstOrNull { it.asignaturaId == asignaturaId }
                    if (existente != null) {
                        container.remoteCatalog.eliminarAsignaturaCurso(existente.id)
                        container.refresh()
                    }
                }
                loadCursoDetail(cursoId)
                return@launch
            }
            docenciaDao.deleteAsignaturaCursoByCursoYAsignatura(cursoId, asignaturaId)
            loadCursoDetail(cursoId)
        }
    }

    fun addDocenteToCurso(cursoId: Long, usuarioId: Long) {
        viewModelScope.launch {
            if (!localOnly) {
                runCatching {
                    container.remoteCatalog.crearUsuarioCurso(
                        UsuarioCursoRequest(usuarioId = usuarioId, cursoId = cursoId)
                    )
                    container.refresh()
                }
                loadCursoDetail(cursoId)
                return@launch
            }
            docenciaDao.insertUsuarioCurso(
                UsuarioCursoEntity(usuarioId = usuarioId, cursoId = cursoId)
            )
            loadCursoDetail(cursoId)
        }
    }

    fun removeDocenteFromCurso(cursoId: Long, usuarioId: Long) {
        viewModelScope.launch {
            if (!localOnly) {
                runCatching {
                    val existente = docenciaDao.getUsuariosByCursoId(cursoId)
                        .firstOrNull { it.usuarioId == usuarioId }
                    if (existente != null) {
                        container.remoteCatalog.eliminarUsuarioCurso(existente.id)
                        container.refresh()
                    }
                }
                loadCursoDetail(cursoId)
                return@launch
            }
            docenciaDao.deleteUsuarioCursoByCursoYUsuario(cursoId, usuarioId)
            loadCursoDetail(cursoId)
        }
    }

    fun addEstudianteToCurso(cursoId: Long, usuarioId: Long) {
        viewModelScope.launch {
            if (!localOnly) {
                runCatching {
                    container.remoteCatalog.crearUsuarioCurso(
                        UsuarioCursoRequest(usuarioId = usuarioId, cursoId = cursoId)
                    )
                    container.refresh()
                }
                loadCursoDetail(cursoId)
                return@launch
            }
            docenciaDao.insertUsuarioCurso(
                UsuarioCursoEntity(usuarioId = usuarioId, cursoId = cursoId)
            )
            loadCursoDetail(cursoId)
        }
    }

    fun removeEstudianteFromCurso(cursoId: Long, usuarioId: Long) {
        viewModelScope.launch {
            if (!localOnly) {
                runCatching {
                    val existente = docenciaDao.getUsuariosByCursoId(cursoId)
                        .firstOrNull { it.usuarioId == usuarioId }
                    if (existente != null) {
                        container.remoteCatalog.eliminarUsuarioCurso(existente.id)
                        container.refresh()
                    }
                }
                loadCursoDetail(cursoId)
                return@launch
            }
            docenciaDao.deleteUsuarioCursoByCursoYUsuario(cursoId, usuarioId)
            loadCursoDetail(cursoId)
        }
    }

    // --- DETALLE ASIGNATURA EN CURSO ---

    fun loadAsignaturaDetail(cursoId: Long, asignaturaId: Long, isEstudiante: Boolean = false) {
        viewModelScope.launch {
            _asignaturaDetailState.value = AsignaturaDetailUiState(isLoading = true)
            val asignatura = curriculoDao.getAsignaturaById(asignaturaId)
            val cursoConDetalles = identidadDao.getCursoConDetallesById(cursoId)
            var unidades = curriculoDao.getUnidadesConDetallesByAsignaturaId(asignaturaId)
            if (isEstudiante) {
                unidades = unidades.filter { it.estado.id == 3L || it.estado.nombre.equals("AUTORIZADO", ignoreCase = true) }
            }
            _asignaturaDetailState.value = AsignaturaDetailUiState(
                isLoading = false,
                asignatura = asignatura,
                cursoConDetalles = cursoConDetalles,
                unidades = unidades
            )
        }
    }

    fun addUnidadToAsignatura(cursoId: Long, asignaturaId: Long, nombreUnidad: String) {
        viewModelScope.launch {
            if (!localOnly) {
                runCatching {
                    container.remoteCatalog.crearUnidad(
                        UnidadRequest(
                            nombre = nombreUnidad,
                            asignaturaId = asignaturaId,
                            estadoId = 2L
                        )
                    )
                    container.refresh()
                }
                loadAsignaturaDetail(cursoId, asignaturaId)
                return@launch
            }
            // estado_id = 2 es EN_REVISION
            curriculoDao.insertUnidad(
                UnidadEntity(
                    nombre = nombreUnidad,
                    asignaturaId = asignaturaId,
                    estadoId = 2L
                )
            )
            loadAsignaturaDetail(cursoId, asignaturaId)
        }
    }

    // --- GESTIÓN DE DOCENTES ---

    fun loadDocentes() {
        viewModelScope.launch {
            _docentesState.value = _docentesState.value.copy(isLoading = true)
            val allDocentes = identidadDao.getAllDocentes()
            val allAssignments = docenciaDao.getAllAsignaturasCursoDetalles()

            val docenteWithAssignments = allDocentes.map { doc ->
                val assignments = allAssignments.filter { it.asignaturaCurso.docenteId == doc.id }
                DocenteWithAssignments(
                    docente = doc,
                    asignaturasCurso = assignments
                )
            }

            _docentesState.value = _docentesState.value.copy(
                isLoading = false,
                docentes = docenteWithAssignments
            )
        }
    }

    fun updateDocentesSearchQuery(query: String) {
        _docentesState.value = _docentesState.value.copy(searchQuery = query)
    }

    fun loadDocenteDetail(docenteId: Long) {
        viewModelScope.launch {
            _docenteDetailState.value = DocenteDetailUiState(isLoading = true)
            val docente = identidadDao.getUsuarioById(docenteId)
            val asignaturasAsignadas = docenciaDao.getAsignaturasCursoByDocenteId(docenteId)
            val allAssignments = docenciaDao.getAllAsignaturasCursoDetalles()

            val availableAsignaturasCurso = allAssignments.filter {
                it.asignaturaCurso.docenteId != docenteId
            }

            _docenteDetailState.value = DocenteDetailUiState(
                isLoading = false,
                docente = docente,
                asignaturasAsignadas = asignaturasAsignadas,
                availableAsignaturasCurso = availableAsignaturasCurso
            )
        }
    }

    fun assignDocenteToAsignaturaCursoFromDetail(docenteId: Long, cursoId: Long, asignaturaId: Long) {
        viewModelScope.launch {
            if (!localOnly) {
                runCatching {
                    guardarDocencia(cursoId, asignaturaId, docenteId)
                }
                loadDocenteDetail(docenteId)
                loadDocentes()
                return@launch
            }
            docenciaDao.updateDocenteInAsignaturaCurso(cursoId, asignaturaId, docenteId)
            docenciaDao.insertUsuarioCurso(
                UsuarioCursoEntity(usuarioId = docenteId, cursoId = cursoId)
            )
            loadDocenteDetail(docenteId)
            loadDocentes()
        }
    }

    fun unassignDocenteFromAsignaturaCurso(docenteId: Long, cursoId: Long, asignaturaId: Long) {
        viewModelScope.launch {
            if (!localOnly) {
                runCatching {
                    guardarDocencia(cursoId, asignaturaId, null)
                }
                loadDocenteDetail(docenteId)
                loadDocentes()
                return@launch
            }
            docenciaDao.updateDocenteInAsignaturaCurso(cursoId, asignaturaId, null)

            val remainingInCurso = docenciaDao.countAsignaturasByDocenteYCurso(docenteId, cursoId)
            if (remainingInCurso == 0) {
                docenciaDao.deleteUsuarioCursoByCursoYUsuario(cursoId, docenteId)
            }

            loadDocenteDetail(docenteId)
            loadDocentes()
        }
    }

    // --- GESTIÓN DE USUARIOS ---

    fun loadUsuarios() {
        viewModelScope.launch {
            _usuariosState.value = _usuariosState.value.copy(isLoading = true)
            val usuariosConRol = identidadDao.getAllUsuariosConRol()
            _usuariosState.value = _usuariosState.value.copy(
                isLoading = false,
                usuarios = usuariosConRol,
                currentUserId = session?.userId
            )
        }
    }

    fun updateUsuariosSearchQuery(query: String) {
        _usuariosState.value = _usuariosState.value.copy(searchQuery = query)
    }

    fun updateUsuariosRolFilter(rolId: Long?) {
        _usuariosState.value = _usuariosState.value.copy(selectedRolFilter = rolId)
    }

    fun toggleUsuarioActivo(usuario: UsuarioEntity) {
        viewModelScope.launch {
            if (!localOnly) {
                val result = runCatching {
                    val actualizado = container.remoteCatalog.cambiarUsuarioActivo(
                        usuario.id,
                        !usuario.activo
                    )
                    identidadDao.insertUsuario(actualizado.toEntity())
                }
                _usuariosState.value = _usuariosState.value.copy(
                    errorMessage = if (result.isFailure) {
                        "No se pudo actualizar el usuario. Revisa la conexión."
                    } else {
                        null
                    }
                )
                loadUsuarios()
                return@launch
            }
            identidadDao.updateUsuario(usuario.copy(activo = !usuario.activo))
            _usuariosState.value = _usuariosState.value.copy(errorMessage = null)
            loadUsuarios()
        }
    }

    fun clearUsuariosError() {
        _usuariosState.value = _usuariosState.value.copy(errorMessage = null)
    }

    // --- CREAR USUARIO ---

    fun initCrearUsuarioForm() {
        viewModelScope.launch {
            val roles = identidadDao.getAllRoles()
            val defaultRolId = roles.firstOrNull { it.nombre.uppercase() == "ESTUDIANTE" }?.id
                ?: roles.firstOrNull()?.id
                ?: 3L
            val session = sessionManager.userSessionFlow.firstOrNull()
            _crearUsuarioState.value = CrearUsuarioUiState(
                roles = roles,
                rolId = defaultRolId
            )
        }
    }

    fun updateCrearUsuarioNombreUsuario(value: String) {
        _crearUsuarioState.value = _crearUsuarioState.value.copy(nombreUsuario = value, errorMessage = null)
    }

    fun updateCrearUsuarioPassword(value: String) {
        _crearUsuarioState.value = _crearUsuarioState.value.copy(password = value, errorMessage = null)
    }

    fun updateCrearUsuarioPnombre(value: String) {
        _crearUsuarioState.value = _crearUsuarioState.value.copy(pnombre = value, errorMessage = null)
    }
            val session = sessionManager.userSessionFlow.firstOrNull()
            if (usuario.activo && session?.userId == usuario.id) {
                _usuariosState.value = _usuariosState.value.copy(
                    errorMessage = "No puedes desactivar tu propia cuenta."
                )
                return@launch
            }

    fun updateCrearUsuarioSnombre(value: String) {
        _crearUsuarioState.value = _crearUsuarioState.value.copy(snombre = value, errorMessage = null)
    }

    fun updateCrearUsuarioApaterno(value: String) {
        _crearUsuarioState.value = _crearUsuarioState.value.copy(apaterno = value, errorMessage = null)
    }

    fun updateCrearUsuarioAmaterno(value: String) {
        _crearUsuarioState.value = _crearUsuarioState.value.copy(amaterno = value, errorMessage = null)
    }

    fun updateCrearUsuarioRolId(rolId: Long) {
        _crearUsuarioState.value = _crearUsuarioState.value.copy(rolId = rolId, errorMessage = null)
    }

    fun updateCrearUsuarioActivo(activo: Boolean) {
        _crearUsuarioState.value = _crearUsuarioState.value.copy(activo = activo)
    }

    fun crearUsuario(onSuccess: () -> Unit) {
        val state = _crearUsuarioState.value

        if (state.nombreUsuario.isBlank()) {
            _crearUsuarioState.value = state.copy(errorMessage = "El nombre de usuario es obligatorio.")
            return
        }
        if (state.password.isBlank()) {
            _crearUsuarioState.value = state.copy(errorMessage = "La contraseña es obligatoria.")
            return
        }
        if (state.pnombre.isBlank()) {
            _crearUsuarioState.value = state.copy(errorMessage = "El primer nombre es obligatorio.")
            return
        }
        if (state.apaterno.isBlank()) {
            _crearUsuarioState.value = state.copy(errorMessage = "El apellido paterno es obligatorio.")
            return
        }

        if (!localOnly) {
            viewModelScope.launch {
                _crearUsuarioState.value = _crearUsuarioState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )
                try {
                    container.remoteCatalog.crearUsuario(
                        UsuarioRequest(
                            nombreUsuario = state.nombreUsuario.trim(),
                            password = state.password.trim(),
                            pnombre = state.pnombre.trim(),
                            snombre = state.snombre.trim().ifBlank { null },
                            apaterno = state.apaterno.trim(),
                            amaterno = state.amaterno.trim().ifBlank { null },
                            rolId = state.rolId,
                            activo = state.activo
                        )
                    )
                    container.refresh()
                    loadUsuarios()
                    _crearUsuarioState.value = _crearUsuarioState.value.copy(
                        isLoading = false,
                        errorMessage = null,
                        isSuccess = true
                    )
                    onSuccess()
                } catch (e: Exception) {
                    _crearUsuarioState.value = _crearUsuarioState.value.copy(
                        isLoading = false,
                        errorMessage = "No se pudo crear el usuario: ${e.message ?: "error de red"}"
                    )
                }
            }
            return
        }

        viewModelScope.launch {
            _crearUsuarioState.value = _crearUsuarioState.value.copy(isLoading = true, errorMessage = null)

            val existing = identidadDao.getUsuarioByNombre(state.nombreUsuario.trim())
            if (existing != null) {
                _crearUsuarioState.value = _crearUsuarioState.value.copy(
                    isLoading = false,
                    errorMessage = "El nombre de usuario '@${state.nombreUsuario.trim()}' ya está registrado."
                )
                return@launch
            }

            val newUsuario = UsuarioEntity(
                nombreUsuario = state.nombreUsuario.trim(),
                passwordHash = state.password.trim(),
                pnombre = state.pnombre.trim(),
                snombre = state.snombre.trim().ifBlank { null },
                apaterno = state.apaterno.trim(),
                amaterno = state.amaterno.trim().ifBlank { null },
                rolId = state.rolId,
                activo = state.activo
            )

            identidadDao.insertUsuario(newUsuario)
            loadUsuarios()

            _crearUsuarioState.value = _crearUsuarioState.value.copy(
                isLoading = false,
                isSuccess = true
            )
            onSuccess()
        }
    }

    private suspend fun guardarDocencia(
        cursoId: Long,
        asignaturaId: Long,
        docenteId: Long?
    ) {
        val existente = docenciaDao.getAsignaturasByCursoId(cursoId)
            .firstOrNull { it.asignaturaId == asignaturaId }

        if (existente != null) {
            container.remoteCatalog.actualizarAsignaturaCurso(
                existente.id,
                AsignaturaCursoUpdateRequest(docenteId = docenteId)
            )
        } else {
            container.remoteCatalog.crearAsignaturaCurso(
                AsignaturaCursoRequest(
                    cursoId = cursoId,
                    asignaturaId = asignaturaId,
                    docenteId = docenteId
                )
            )
        }
        container.refresh()
    }
}
