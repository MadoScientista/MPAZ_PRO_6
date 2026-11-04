package com.ccstudio.mpaz_pro_6.ui.utp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ccstudio.mpaz_pro_6.data.local.MpazDatabase
import com.ccstudio.mpaz_pro_6.data.local.dao.ActividadConTipo
import com.ccstudio.mpaz_pro_6.data.local.dao.AsignaturaConDocente
import com.ccstudio.mpaz_pro_6.data.local.dao.AsignaturaCursoDetalle
import com.ccstudio.mpaz_pro_6.data.local.dao.CursoConDetalles
import com.ccstudio.mpaz_pro_6.data.local.dao.PreguntaConAlternativas
import com.ccstudio.mpaz_pro_6.data.local.dao.UnidadConDetalles
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AsignaturaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ContenidoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.UnidadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.AsignaturaCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.UsuarioCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity
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

class UtpViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MpazDatabase.getDatabase(application)
    private val curriculoDao = db.curriculoDao()
    private val identidadDao = db.identidadDao()
    private val docenciaDao = db.docenciaDao()

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

    init {
        loadPendingUnits()
        loadCursos()
        loadDocentes()
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

    // --- CURSOS ---

    fun loadCursos() {
        viewModelScope.launch {
            _cursosState.value = CursosUiState(isLoading = true)
            val cursos = identidadDao.getCursosConDetalles()
            _cursosState.value = CursosUiState(
                isLoading = false,
                cursos = cursos
            )
        }
    }

    fun loadCursoDetail(cursoId: Long) {
        viewModelScope.launch {
            _cursoDetailState.value = CursoDetailUiState(isLoading = true)
            val cursoConDetalles = identidadDao.getCursoConDetallesById(cursoId)
            val asignaturas = docenciaDao.getAsignaturasEntityByCursoId(cursoId)
            val asignaturasConDocente = docenciaDao.getAsignaturasConDocenteByCursoId(cursoId)
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
            docenciaDao.deleteAsignaturaCursoByCursoYAsignatura(cursoId, asignaturaId)
            loadCursoDetail(cursoId)
        }
    }

    fun addDocenteToCurso(cursoId: Long, usuarioId: Long) {
        viewModelScope.launch {
            docenciaDao.insertUsuarioCurso(
                UsuarioCursoEntity(usuarioId = usuarioId, cursoId = cursoId)
            )
            loadCursoDetail(cursoId)
        }
    }

    fun removeDocenteFromCurso(cursoId: Long, usuarioId: Long) {
        viewModelScope.launch {
            docenciaDao.deleteUsuarioCursoByCursoYUsuario(cursoId, usuarioId)
            loadCursoDetail(cursoId)
        }
    }

    fun addEstudianteToCurso(cursoId: Long, usuarioId: Long) {
        viewModelScope.launch {
            docenciaDao.insertUsuarioCurso(
                UsuarioCursoEntity(usuarioId = usuarioId, cursoId = cursoId)
            )
            loadCursoDetail(cursoId)
        }
    }

    fun removeEstudianteFromCurso(cursoId: Long, usuarioId: Long) {
        viewModelScope.launch {
            docenciaDao.deleteUsuarioCursoByCursoYUsuario(cursoId, usuarioId)
            loadCursoDetail(cursoId)
        }
    }

    // --- DETALLE ASIGNATURA EN CURSO ---

    fun loadAsignaturaDetail(cursoId: Long, asignaturaId: Long) {
        viewModelScope.launch {
            _asignaturaDetailState.value = AsignaturaDetailUiState(isLoading = true)
            val asignatura = curriculoDao.getAsignaturaById(asignaturaId)
            val cursoConDetalles = identidadDao.getCursoConDetallesById(cursoId)
            val unidades = curriculoDao.getUnidadesConDetallesByAsignaturaId(asignaturaId)
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
            docenciaDao.updateDocenteInAsignaturaCurso(cursoId, asignaturaId, null)

            val remainingInCurso = docenciaDao.countAsignaturasByDocenteYCurso(docenteId, cursoId)
            if (remainingInCurso == 0) {
                docenciaDao.deleteUsuarioCursoByCursoYUsuario(cursoId, docenteId)
            }

            loadDocenteDetail(docenteId)
            loadDocentes()
        }
    }
}
