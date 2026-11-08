package com.ccstudio.mpaz_pro_6.ui.docente

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ccstudio.mpaz_pro_6.data.dto.informe.ActividadEvaluadaResumen
import com.ccstudio.mpaz_pro_6.data.dto.informe.DetallePreliminarResponse
import com.ccstudio.mpaz_pro_6.data.dto.informe.InformePreliminarResponse
import com.ccstudio.mpaz_pro_6.data.local.MpazDatabase
import com.ccstudio.mpaz_pro_6.data.local.dao.AsignaturaCursoDetalle
import com.ccstudio.mpaz_pro_6.data.local.dao.DetalleInformeConObjetivo
import com.ccstudio.mpaz_pro_6.data.local.dao.InformePedagogicoDetalle
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.UnidadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.informe.DetalleInformePedagogicoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.informe.InformePedagogicoEntity
import com.ccstudio.mpaz_pro_6.data.local.preferences.SessionManager
import com.ccstudio.mpaz_pro_6.data.mapper.ResumenMapper.toResumen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class InformesUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val selectedTab: Int = 0, // 0: Emitidos, 1: Generar Nuevo

    // Filtros
    val cursosAsignados: List<AsignaturaCursoDetalle> = emptyList(),
    val selectedCursoId: Long? = null,
    val estudiantesCurso: List<UsuarioEntity> = emptyList(),
    val selectedEstudianteId: Long? = null,
    val unidadesCurso: List<UnidadEntity> = emptyList(),
    val selectedUnidadId: Long? = null,

    // Formulario de emisión
    val fechaCorte: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
    val fechaEmision: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
    val descripcion: String = "",

    // Preview
    val isCalculatingPreview: Boolean = false,
    val preliminarResponse: InformePreliminarResponse? = null,

    // Lista de informes emitidos
    val informesEmitidos: List<InformePedagogicoDetalle> = emptyList(),

    // Modal detalle
    val showDetalleDialog: Boolean = false,
    val selectedInformeDetalle: InformePedagogicoDetalle? = null,
    val selectedInformeOAs: List<DetalleInformeConObjetivo> = emptyList()
)

class InformesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MpazDatabase.getDatabase(application)
    private val docenciaDao = db.docenciaDao()
    private val identidadDao = db.identidadDao()
    private val curriculoDao = db.curriculoDao()
    private val evaluacionDao = db.evaluacionDao()
    private val informeDao = db.informeDao()
    private val sessionManager = SessionManager(application)

    private val _uiState = MutableStateFlow(InformesUiState())
    val uiState: StateFlow<InformesUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun setTab(index: Int) {
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val session = sessionManager.userSessionFlow.firstOrNull()
                val userId = session?.userId ?: 2L // Default docente Ana Silva
                val rol = session?.rol ?: "DOCENTE"

                val asignaturasCurso = if (rol.equals("UTP", ignoreCase = true)) {
                    docenciaDao.getAllAsignaturasCursoDetalles()
                } else {
                    docenciaDao.getAsignaturasCursoByDocenteId(userId)
                }

                val emitidos = informeDao.getAllInformesDetallados()

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        cursosAsignados = asignaturasCurso,
                        informesEmitidos = emitidos
                    )
                }

                // Autoseleccionar primer curso si existe
                val primerCursoId = asignaturasCurso.firstOrNull()?.curso?.id
                if (primerCursoId != null && _uiState.value.selectedCursoId == null) {
                    onCursoSelected(primerCursoId)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar datos iniciales: ${e.message}"
                    )
                }
            }
        }
    }

    fun onCursoSelected(cursoId: Long) {
        viewModelScope.launch {
            try {
                val estudiantes = docenciaDao.getEstudiantesByCursoId(cursoId)

                // Obtener asignaturas del curso y sus unidades
                val asignaturas = docenciaDao.getAsignaturasEntityByCursoId(cursoId)
                val unidades = mutableListOf<UnidadEntity>()
                for (asig in asignaturas) {
                    val uList = curriculoDao.getUnidadesConDetallesByAsignaturaId(asig.id).map { it.unidad }
                    unidades.addAll(uList)
                }

                val primerEstudianteId = estudiantes.firstOrNull()?.id
                val primeraUnidadId = unidades.firstOrNull()?.id

                _uiState.update { state ->
                    state.copy(
                        selectedCursoId = cursoId,
                        estudiantesCurso = estudiantes,
                        selectedEstudianteId = primerEstudianteId,
                        unidadesCurso = unidades,
                        selectedUnidadId = primeraUnidadId,
                        preliminarResponse = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al seleccionar curso: ${e.message}") }
            }
        }
    }

    fun onEstudianteSelected(usuarioId: Long) {
        _uiState.update { it.copy(selectedEstudianteId = usuarioId, preliminarResponse = null) }
    }

    fun onUnidadSelected(unidadId: Long) {
        _uiState.update { it.copy(selectedUnidadId = unidadId, preliminarResponse = null) }
    }

    fun setFechaCorte(fecha: String) {
        _uiState.update { it.copy(fechaCorte = fecha) }
    }

    fun setFechaEmision(fecha: String) {
        _uiState.update { it.copy(fechaEmision = fecha) }
    }

    fun setDescripcion(text: String) {
        _uiState.update { it.copy(descripcion = text) }
    }

    fun generarVistaPrevia() {
        val estudianteId = _uiState.value.selectedEstudianteId
        val unidadId = _uiState.value.selectedUnidadId
        val fechaCorte = _uiState.value.fechaCorte

        if (estudianteId == null || unidadId == null) {
            _uiState.update { it.copy(errorMessage = "Seleccione un estudiante y una unidad válida.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCalculatingPreview = true, errorMessage = null) }
            try {
                val preliminar = calcularInformePreliminar(estudianteId, unidadId, fechaCorte)
                _uiState.update {
                    it.copy(
                        isCalculatingPreview = false,
                        preliminarResponse = preliminar
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCalculatingPreview = false,
                        errorMessage = "Error al calcular vista previa: ${e.message}"
                    )
                }
            }
        }
    }

    private suspend fun calcularInformePreliminar(
        estudianteId: Long,
        unidadId: Long,
        fechaCorteStr: String
    ): InformePreliminarResponse {
        val estudiante = identidadDao.getUsuarioById(estudianteId)
            ?: throw IllegalStateException("Estudiante no encontrado")
        val unidad = curriculoDao.getUnidadById(unidadId)
            ?: throw IllegalStateException("Unidad no encontrada")
        val asignatura = curriculoDao.getAsignaturaById(unidad.asignaturaId)
            ?: throw IllegalStateException("Asignatura no encontrada")

        val usuarioResumen = estudiante.toResumen()
        val unidadResumen = unidad.toResumen(asignatura)

        // 1. Obtener contenidos de la unidad
        val contenidos = curriculoDao.getContenidosByUnidadId(unidadId)

        // 2. Obtener OAs de la unidad
        val oasUnidad = curriculoDao.getObjetivosAprendizajeByUnidadId(unidadId)

        val actividadesEvaluadas = mutableListOf<ActividadEvaluadaResumen>()

        // Map para llevar el recuento por OA id: Pair(correctas, incorrectas)
        val oaCounts = mutableMapOf<Long, Pair<Int, Int>>()
        for (oa in oasUnidad) {
            oaCounts[oa.id] = Pair(0, 0)
        }

        for (contenido in contenidos) {
            val actividadesConTipo = curriculoDao.getActividadesConTipoByContenidoId(contenido.id)
            for (actConTipo in actividadesConTipo) {
                val act = actConTipo.actividad
                val actCursoList = docenciaDao.getActividadesCursoByActividadId(act.id)

                // Buscar el último intento cerrado del estudiante en esta actividad
                var ultimoIntentoCerrado: com.ccstudio.mpaz_pro_6.data.local.entity.evaluacion.IntentoEntity? = null
                var acIdUsado: Long = 0L

                for (ac in actCursoList) {
                    val intentos = evaluacionDao.getIntentosByUsuarioYActividadCurso(estudianteId, ac.id)
                    val cerrados = intentos.filter { it.fechaFin != null }
                    val candidato = cerrados.maxByOrNull { it.fechaFin ?: "" }
                    if (candidato != null) {
                        if (ultimoIntentoCerrado == null || (candidato.fechaFin ?: "") > (ultimoIntentoCerrado.fechaFin ?: "")) {
                            ultimoIntentoCerrado = candidato
                            acIdUsado = ac.id
                        }
                    }
                }

                if (ultimoIntentoCerrado != null) {
                    actividadesEvaluadas.add(
                        ActividadEvaluadaResumen(
                            actividadCursoId = acIdUsado,
                            actividadId = act.id,
                            instrucciones = act.instrucciones,
                            intentoId = ultimoIntentoCerrado.id,
                            fechaFin = ultimoIntentoCerrado.fechaFin ?: ""
                        )
                    )

                    // Evaluar respuestas de este intento
                    val respuestas = evaluacionDao.getRespuestasByIntentoId(ultimoIntentoCerrado.id)
                    val preguntasConAlt = curriculoDao.getPreguntasConAlternativasByActividadId(act.id)

                    for (pConAlt in preguntasConAlt) {
                        val pId = pConAlt.pregunta.id
                        val rForPregunta = respuestas.filter { it.preguntaId == pId }
                        if (rForPregunta.isEmpty()) continue // no respondida

                        // Verificar si todas las seleccionadas son correctas
                        val altIdsSeleccionadas = rForPregunta.map { it.alternativaId }.toSet()
                        val altCorrectasMap = pConAlt.alternativas.associate { it.id to it.correcta }

                        val esCorrecta = altIdsSeleccionadas.isNotEmpty() && altIdsSeleccionadas.all { altId ->
                            altCorrectasMap[altId] == true
                        }
                        val esIncorrecta = altIdsSeleccionadas.any { altId ->
                            altCorrectasMap[altId] == false
                        }

                        // Atribuir resultado a los OAs de la unidad
                        for (oa in oasUnidad) {
                            val current = oaCounts[oa.id] ?: Pair(0, 0)
                            if (esCorrecta) {
                                oaCounts[oa.id] = Pair(current.first + 1, current.second)
                            } else if (esIncorrecta) {
                                oaCounts[oa.id] = Pair(current.first, current.second + 1)
                            }
                        }
                    }
                }
            }
        }

        val detallesPreliminares = mutableListOf<DetallePreliminarResponse>()
        var globalCorrectas = 0
        var globalIncorrectas = 0

        for (oa in oasUnidad) {
            val (corr, inc) = oaCounts[oa.id] ?: Pair(0, 0)
            val total = corr + inc
            val porcentaje = if (total > 0) (corr.toDouble() / total) * 100.0 else 0.0
            globalCorrectas += corr
            globalIncorrectas += inc

            detallesPreliminares.add(
                DetallePreliminarResponse(
                    objetivoAprendizaje = oa.toResumen(),
                    correctas = corr,
                    incorrectas = inc,
                    total = total,
                    porcentaje = porcentaje
                )
            )
        }

        return InformePreliminarResponse(
            usuario = usuarioResumen,
            unidad = unidadResumen,
            fechaCorte = fechaCorteStr,
            actividades = actividadesEvaluadas,
            detalles = detallesPreliminares,
            totalCorrectas = globalCorrectas,
            totalIncorrectas = globalIncorrectas
        )
    }

    fun emitirInformeGenerado() {
        val preview = _uiState.value.preliminarResponse
        val estudianteId = _uiState.value.selectedEstudianteId
        val unidadId = _uiState.value.selectedUnidadId
        val fecha = _uiState.value.fechaEmision
        val descripcion = _uiState.value.descripcion
        val fechaCorte = _uiState.value.fechaCorte

        if (preview == null || estudianteId == null || unidadId == null) {
            _uiState.update { it.copy(errorMessage = "Genere una vista previa antes de emitir el informe.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Insertar cabecera
                val informeEntity = InformePedagogicoEntity(
                    usuarioId = estudianteId,
                    unidadId = unidadId,
                    fecha = fecha,
                    descripcion = descripcion.ifBlank { "Informe pedagógico emitido desde resultados" },
                    fechaCorte = fechaCorte,
                    origen = "AUTOMATICO"
                )
                val informeId = informeDao.insertInformePedagogico(informeEntity)

                // Insertar detalles por OA
                val detallesEntities = preview.detalles.map { d ->
                    DetalleInformePedagogicoEntity(
                        informePedagogicoId = informeId,
                        objetivoAprendizajeId = d.objetivoAprendizaje.id,
                        correctas = d.correctas,
                        incorrectas = d.incorrectas
                    )
                }
                informeDao.insertDetallesInforme(detallesEntities)

                // Recargar lista y cambiar a pestaña de emitidos
                val emitidosActualizados = informeDao.getAllInformesDetallados()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Informe pedagógico emitido exitosamente (ID #$informeId).",
                        informesEmitidos = emitidosActualizados,
                        preliminarResponse = null,
                        descripcion = "",
                        selectedTab = 0
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al emitir el informe: ${e.message}"
                    )
                }
            }
        }
    }

    fun verDetalleInforme(informe: InformePedagogicoDetalle) {
        viewModelScope.launch {
            try {
                val oasConDetalle = informeDao.getDetallesConObjetivoByInformeId(informe.informe.id)
                _uiState.update {
                    it.copy(
                        selectedInformeDetalle = informe,
                        selectedInformeOAs = oasConDetalle,
                        showDetalleDialog = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Error al cargar detalle del informe: ${e.message}") }
            }
        }
    }

    fun cerrarDetalleDialog() {
        _uiState.update {
            it.copy(
                showDetalleDialog = false,
                selectedInformeDetalle = null,
                selectedInformeOAs = emptyList()
            )
        }
    }

    fun eliminarInforme(informeId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                informeDao.deleteDetallesByInformeId(informeId)
                informeDao.deleteInformePedagogicoById(informeId)

                val emitidosActualizados = informeDao.getAllInformesDetallados()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Informe #$informeId eliminado correctamente.",
                        informesEmitidos = emitidosActualizados,
                        showDetalleDialog = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al eliminar el informe: ${e.message}"
                    )
                }
            }
        }
    }
}
