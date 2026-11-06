package com.ccstudio.mpaz_pro_6.ui.estudiante

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ccstudio.mpaz_pro_6.data.local.MpazDatabase
import com.ccstudio.mpaz_pro_6.data.local.dao.ActividadCursoConDetalles
import com.ccstudio.mpaz_pro_6.data.local.dao.PreguntaConAlternativas
import com.ccstudio.mpaz_pro_6.data.local.dao.UnidadConDetalles
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AsignaturaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.TipoActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.evaluacion.IntentoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.evaluacion.RespuestaEntity
import com.ccstudio.mpaz_pro_6.data.local.preferences.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class UltimoIntentoResumen(
    val intentoId: Long,
    val numeroIntento: Int,
    val cantidadCorrectas: Int,
    val cantidadIncorrectas: Int,
    val totalPreguntas: Int,
    val porcentajeLogro: Int
)

data class ActividadEstudianteItem(
    val actividadCursoConDetalles: ActividadCursoConDetalles,
    val intentos: List<IntentoEntity> = emptyList(),
    val unidad: UnidadConDetalles? = null,
    val asignatura: AsignaturaEntity? = null,
    val ultimoIntentoResumen: UltimoIntentoResumen? = null,
) {
    val intentosRealizados: Int get() = intentos.size
    val maxIntentos: Int? get() = actividadCursoConDetalles.actividadCurso.maxIntentos
    val siguienteIntentoNumero: Int get() = intentosRealizados + 1
    val sinIntentosDisponibles: Boolean get() = (maxIntentos != null && maxIntentos!! > 0 && intentosRealizados >= maxIntentos!!)
}

data class ActividadesPendientesEstudianteState(
    val isLoading: Boolean = true,
    val actividades: List<ActividadEstudianteItem> = emptyList(),
    val errorMessage: String? = null,
)

enum class EstadoResolverActividad {
    LOADING,
    CONFIRMACION,
    RESOLVIENDO,
    RESULTADO,
    ERROR
}

data class ResultadoPreguntaDetalle(
    val preguntaConAlternativas: PreguntaConAlternativas,
    val alternativaSeleccionadaId: Long?,
    val alternativaCorrectaId: Long?,
    val esCorrecta: Boolean
)

data class ResolverActividadState(
    val estado: EstadoResolverActividad = EstadoResolverActividad.LOADING,
    val actividad: ActividadEntity? = null,
    val tipoActividad: TipoActividadEntity? = null,
    val actividadCursoId: Long = 0L,
    val cursoId: Long = 0L,
    val unidadId: Long = 0L,
    val unidadNombre: String = "",
    val asignaturaNombre: String = "",
    val intento: IntentoEntity? = null,
    val numeroIntento: Int = 1,
    val maxIntentos: Int? = null,
    val totalIntentosRealizados: Int = 0,
    val puedeRealizarOtroIntento: Boolean = true,
    val ultimoIntentoResumen: UltimoIntentoResumen? = null,
    val preguntas: List<PreguntaConAlternativas> = emptyList(),
    val respuestasSeleccionadas: Map<Long, Long> = emptyMap(), // preguntaId -> alternativaId
    val resultados: List<ResultadoPreguntaDetalle> = emptyList(),
    val cantidadCorrectas: Int = 0,
    val cantidadIncorrectas: Int = 0,
    val porcentajeLogro: Int = 0,
    val errorMessage: String? = null
)

class EstudianteViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MpazDatabase.getDatabase(application)
    private val docenciaDao = db.docenciaDao()
    private val curriculoDao = db.curriculoDao()
    private val evaluacionDao = db.evaluacionDao()
    private val sessionManager = SessionManager(application)

    private val _actividadesState = MutableStateFlow(ActividadesPendientesEstudianteState())
    val actividadesState: StateFlow<ActividadesPendientesEstudianteState> = _actividadesState.asStateFlow()

    private val _resolverState = MutableStateFlow(ResolverActividadState())
    val resolverState: StateFlow<ResolverActividadState> = _resolverState.asStateFlow()

    fun loadActividadesPendientesEstudiante() {
        viewModelScope.launch {
            _actividadesState.value = _actividadesState.value.copy(isLoading = true)
            val session = sessionManager.userSessionFlow.firstOrNull()
            val userId = session?.userId

            if (userId == null) {
                _actividadesState.value = ActividadesPendientesEstudianteState(
                    isLoading = false,
                    errorMessage = "Sesión no válida"
                )
                return@launch
            }

            val acList = docenciaDao.getActividadesCursoConDetallesByEstudianteId(userId)
            val items = acList.mapNotNull { acDetalle ->
                val intentos = evaluacionDao.getIntentosByUsuarioYActividadCurso(userId, acDetalle.actividadCurso.id)
                var unidad: UnidadConDetalles? = null
                var asignatura: AsignaturaEntity? = null

                val cId = acDetalle.actividad.contenidoId
                if (cId != null) {
                    val contenido = curriculoDao.getContenidoById(cId)
                    if (contenido != null) {
                        unidad = curriculoDao.getUnidadConDetallesById(contenido.unidadId)
                        if (unidad != null && unidad.estado.id != 3L && !unidad.estado.nombre.equals("AUTORIZADO", ignoreCase = true)) {
                            // Si la unidad no está autorizada, no se expone la actividad al estudiante
                            return@mapNotNull null
                        }
                        unidad?.let { u ->
                            asignatura = curriculoDao.getAsignaturaById(u.unidad.asignaturaId)
                        }
                    }
                }

                // Calcular el resumen del último intento completado (si existe)
                val ultimoCompletado = intentos.lastOrNull { it.fechaFin != null }
                val ultimoIntentoResumen = if (ultimoCompletado != null) {
                    val preguntas = curriculoDao.getPreguntasConAlternativasByActividadId(acDetalle.actividad.id)
                    val respuestas = evaluacionDao.getRespuestasByIntentoId(ultimoCompletado.id)
                    val respuestasMap = respuestas.associateBy({ it.preguntaId }, { it.alternativaId })
                    val correctas = preguntas.count { p ->
                        val altCorr = p.alternativas.firstOrNull { it.correcta }?.id
                        val altSel = respuestasMap[p.pregunta.id]
                        altSel != null && altSel == altCorr
                    }
                    val total = preguntas.size
                    val incorrectas = total - correctas
                    val porcentaje = if (total > 0) (correctas * 100) / total else 0
                    val idx = intentos.indexOfFirst { it.id == ultimoCompletado.id }
                    val numIntento = if (idx >= 0) idx + 1 else intentos.size

                    UltimoIntentoResumen(
                        intentoId = ultimoCompletado.id,
                        numeroIntento = numIntento,
                        cantidadCorrectas = correctas,
                        cantidadIncorrectas = incorrectas,
                        totalPreguntas = total,
                        porcentajeLogro = porcentaje
                    )
                } else null

                ActividadEstudianteItem(
                    actividadCursoConDetalles = acDetalle,
                    intentos = intentos,
                    unidad = unidad,
                    asignatura = asignatura,
                    ultimoIntentoResumen = ultimoIntentoResumen
                )
            }

            _actividadesState.value = ActividadesPendientesEstudianteState(
                isLoading = false,
                actividades = items
            )
        }
    }

    fun iniciarIntento(actividadId: Long, cursoId: Long, onIntentoListo: (Long) -> Unit) {
        viewModelScope.launch {
            val session = sessionManager.userSessionFlow.firstOrNull()
            val userId = session?.userId ?: return@launch

            val ac = if (cursoId > 0L) {
                docenciaDao.getActividadCursoByActividadYCurso(actividadId, cursoId)
            } else {
                docenciaDao.getActividadesCursoByActividadId(actividadId).firstOrNull()
            } ?: run {
                // Return if no activity_curso record exists
                return@launch
            }

            val previos = evaluacionDao.getIntentosByUsuarioYActividadCurso(userId, ac.id)
            val abierto = previos.firstOrNull { it.fechaFin == null }

            val intentoId = if (abierto != null) {
                abierto.id
            } else {
                val nowStr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
                val nuevo = IntentoEntity(
                    usuarioId = userId,
                    actividadCursoId = ac.id,
                    fechaInicio = nowStr,
                    fechaFin = null
                )
                evaluacionDao.insertIntento(nuevo)
            }
            onIntentoListo(intentoId)
        }
    }

    fun iniciarNuevoIntento(actividadId: Long, cursoId: Long, onIntentoCreado: ((Long) -> Unit)? = null) {
        viewModelScope.launch {
            _resolverState.value = ResolverActividadState(estado = EstadoResolverActividad.LOADING)
            val session = sessionManager.userSessionFlow.firstOrNull()
            val userId = session?.userId ?: return@launch

            val ac = if (cursoId > 0L) {
                docenciaDao.getActividadCursoByActividadYCurso(actividadId, cursoId)
            } else {
                docenciaDao.getActividadesCursoByActividadId(actividadId).firstOrNull()
            } ?: return@launch

            val nowStr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
            val nuevoIntento = IntentoEntity(
                usuarioId = userId,
                actividadCursoId = ac.id,
                fechaInicio = nowStr,
                fechaFin = null
            )
            val nuevoId = evaluacionDao.insertIntento(nuevoIntento)
            onIntentoCreado?.invoke(nuevoId)

            loadResolverActividad(actividadId, cursoId, nuevoId)
        }
    }

    fun loadResolverActividad(actividadId: Long, cursoId: Long, intentoId: Long) {
        viewModelScope.launch {
            _resolverState.value = ResolverActividadState(estado = EstadoResolverActividad.LOADING)
            val session = sessionManager.userSessionFlow.firstOrNull()
            val userId = session?.userId ?: 0L

            val actividadConTipo = curriculoDao.getActividadConTipoById(actividadId)
            if (actividadConTipo == null) {
                _resolverState.value = ResolverActividadState(
                    estado = EstadoResolverActividad.ERROR,
                    errorMessage = "Actividad no encontrada"
                )
                return@launch
            }

            val ac = if (cursoId > 0L) {
                docenciaDao.getActividadCursoByActividadYCurso(actividadId, cursoId)
            } else {
                docenciaDao.getActividadesCursoByActividadId(actividadId).firstOrNull()
            }

            var unidadId = 0L
            var unidadNombre = ""
            var asignaturaNombre = ""

            if (actividadConTipo.actividad.contenidoId != null) {
                val contenido = curriculoDao.getContenidoById(actividadConTipo.actividad.contenidoId)
                if (contenido != null) {
                    val uDetalle = curriculoDao.getUnidadConDetallesById(contenido.unidadId)
                    if (uDetalle != null) {
                        unidadId = uDetalle.unidad.id
                        unidadNombre = uDetalle.unidad.nombre
                        asignaturaNombre = uDetalle.asignatura.nombre
                    }
                }
            }

            val preguntas = curriculoDao.getPreguntasConAlternativasByActividadId(actividadId)
            val previos = if (ac != null) evaluacionDao.getIntentosByUsuarioYActividadCurso(userId, ac.id) else emptyList()
            val totalIntentosRealizados = previos.size
            val maxIntentos = ac?.maxIntentos
            val puedeRealizarOtro = (maxIntentos == null || maxIntentos <= 0 || totalIntentosRealizados < maxIntentos)

            // Resumen del último intento completado
            val ultimoCompletado = previos.lastOrNull { it.fechaFin != null }
            val ultimoIntentoResumen = if (ultimoCompletado != null) {
                val respuestas = evaluacionDao.getRespuestasByIntentoId(ultimoCompletado.id)
                val respuestasMap = respuestas.associateBy({ it.preguntaId }, { it.alternativaId })
                val correctas = preguntas.count { p ->
                    val altCorr = p.alternativas.firstOrNull { it.correcta }?.id
                    val altSel = respuestasMap[p.pregunta.id]
                    altSel != null && altSel == altCorr
                }
                val total = preguntas.size
                val incorrectas = total - correctas
                val porcentaje = if (total > 0) (correctas * 100) / total else 0
                val idx = previos.indexOfFirst { it.id == ultimoCompletado.id }
                val numIntento = if (idx >= 0) idx + 1 else previos.size

                UltimoIntentoResumen(
                    intentoId = ultimoCompletado.id,
                    numeroIntento = numIntento,
                    cantidadCorrectas = correctas,
                    cantidadIncorrectas = incorrectas,
                    totalPreguntas = total,
                    porcentajeLogro = porcentaje
                )
            } else null

            if (intentoId > 0L) {
                val intento = evaluacionDao.getIntentoById(intentoId)
                val numeroIntento = if (intento != null) {
                    val idx = previos.indexOfFirst { it.id == intento.id }
                    if (idx >= 0) idx + 1 else previos.size.coerceAtLeast(1)
                } else previos.size.coerceAtLeast(1)

                val respuestasExistentes = if (intento != null) {
                    evaluacionDao.getRespuestasByIntentoId(intento.id)
                } else emptyList()

                val respuestasMap = respuestasExistentes.associateBy({ it.preguntaId }, { it.alternativaId })

                if (intento?.fechaFin != null) {
                    // Completed attempt -> Show results mode
                    val state = ResolverActividadState(
                        estado = EstadoResolverActividad.RESULTADO,
                        actividad = actividadConTipo.actividad,
                        tipoActividad = actividadConTipo.tipoActividad,
                        actividadCursoId = ac?.id ?: 0L,
                        cursoId = cursoId,
                        unidadId = unidadId,
                        unidadNombre = unidadNombre,
                        asignaturaNombre = asignaturaNombre,
                        intento = intento,
                        numeroIntento = numeroIntento,
                        maxIntentos = maxIntentos,
                        totalIntentosRealizados = totalIntentosRealizados,
                        puedeRealizarOtroIntento = puedeRealizarOtro,
                        ultimoIntentoResumen = ultimoIntentoResumen,
                        preguntas = preguntas,
                        respuestasSeleccionadas = respuestasMap
                    )
                    _resolverState.value = calcularResultadosInterno(state, respuestasMap)
                } else {
                    // In-progress attempt -> Show solving mode
                    _resolverState.value = ResolverActividadState(
                        estado = EstadoResolverActividad.RESOLVIENDO,
                        actividad = actividadConTipo.actividad,
                        tipoActividad = actividadConTipo.tipoActividad,
                        actividadCursoId = ac?.id ?: 0L,
                        cursoId = cursoId,
                        unidadId = unidadId,
                        unidadNombre = unidadNombre,
                        asignaturaNombre = asignaturaNombre,
                        intento = intento,
                        numeroIntento = numeroIntento,
                        maxIntentos = maxIntentos,
                        totalIntentosRealizados = totalIntentosRealizados,
                        puedeRealizarOtroIntento = puedeRealizarOtro,
                        ultimoIntentoResumen = ultimoIntentoResumen,
                        preguntas = preguntas,
                        respuestasSeleccionadas = respuestasMap
                    )
                }
            } else {
                // intentoId == 0L: Clickeado desde el detalle del contenido o unidad
                val abierto = previos.firstOrNull { it.fechaFin == null }
                if (abierto != null) {
                    // Reanudar intento abierto
                    val idx = previos.indexOfFirst { it.id == abierto.id }
                    val numIntento = if (idx >= 0) idx + 1 else previos.size
                    val respuestasExistentes = evaluacionDao.getRespuestasByIntentoId(abierto.id)
                    val respuestasMap = respuestasExistentes.associateBy({ it.preguntaId }, { it.alternativaId })

                    _resolverState.value = ResolverActividadState(
                        estado = EstadoResolverActividad.RESOLVIENDO,
                        actividad = actividadConTipo.actividad,
                        tipoActividad = actividadConTipo.tipoActividad,
                        actividadCursoId = ac?.id ?: 0L,
                        cursoId = cursoId,
                        unidadId = unidadId,
                        unidadNombre = unidadNombre,
                        asignaturaNombre = asignaturaNombre,
                        intento = abierto,
                        numeroIntento = numIntento,
                        maxIntentos = maxIntentos,
                        totalIntentosRealizados = totalIntentosRealizados,
                        puedeRealizarOtroIntento = puedeRealizarOtro,
                        ultimoIntentoResumen = ultimoIntentoResumen,
                        preguntas = preguntas,
                        respuestasSeleccionadas = respuestasMap
                    )
                } else {
                    // Mostrar Estado de Confirmación previo a iniciar o ver resultados
                    _resolverState.value = ResolverActividadState(
                        estado = EstadoResolverActividad.CONFIRMACION,
                        actividad = actividadConTipo.actividad,
                        tipoActividad = actividadConTipo.tipoActividad,
                        actividadCursoId = ac?.id ?: 0L,
                        cursoId = cursoId,
                        unidadId = unidadId,
                        unidadNombre = unidadNombre,
                        asignaturaNombre = asignaturaNombre,
                        intento = null,
                        numeroIntento = totalIntentosRealizados + 1,
                        maxIntentos = maxIntentos,
                        totalIntentosRealizados = totalIntentosRealizados,
                        puedeRealizarOtroIntento = puedeRealizarOtro,
                        ultimoIntentoResumen = ultimoIntentoResumen,
                        preguntas = preguntas
                    )
                }
            }
        }
    }

    fun seleccionarAlternativa(preguntaId: Long, alternativaId: Long) {
        val currentState = _resolverState.value
        if (currentState.estado != EstadoResolverActividad.RESOLVIENDO) return

        val mapActual = currentState.respuestasSeleccionadas.toMutableMap()
        mapActual[preguntaId] = alternativaId
        _resolverState.value = currentState.copy(respuestasSeleccionadas = mapActual)
    }

    fun finalizarIntento(intentoId: Long, onFinalizado: () -> Unit) {
        viewModelScope.launch {
            val currentState = _resolverState.value
            var intento = currentState.intento ?: evaluacionDao.getIntentoById(intentoId)

            if (intento == null) {
                // If attempt record didn't exist yet, create one
                val session = sessionManager.userSessionFlow.firstOrNull()
                val userId = session?.userId ?: 0L
                val nowStr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
                val nuevo = IntentoEntity(
                    usuarioId = userId,
                    actividadCursoId = currentState.actividadCursoId,
                    fechaInicio = nowStr,
                    fechaFin = nowStr
                )
                val idGenerado = evaluacionDao.insertIntento(nuevo)
                intento = nuevo.copy(id = idGenerado)
            } else {
                val nowStr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())
                intento = intento.copy(fechaFin = nowStr)
                evaluacionDao.updateIntento(intento)
            }

            val respuestasList = currentState.respuestasSeleccionadas.map { (pId, altId) ->
                RespuestaEntity(
                    intentoId = intento.id,
                    preguntaId = pId,
                    alternativaId = altId
                )
            }
            if (respuestasList.isNotEmpty()) {
                evaluacionDao.insertRespuestas(respuestasList)
            }

            val newState = currentState.copy(
                intento = intento,
                estado = EstadoResolverActividad.RESULTADO
            )
            _resolverState.value = calcularResultadosInterno(newState, currentState.respuestasSeleccionadas)
            onFinalizado()
        }
    }

    private fun calcularResultadosInterno(
        baseState: ResolverActividadState,
        respuestasMap: Map<Long, Long>
    ): ResolverActividadState {
        val detalles = baseState.preguntas.map { pConAlt ->
            val altCorrecta = pConAlt.alternativas.firstOrNull { it.correcta }?.id
            val altSeleccionada = respuestasMap[pConAlt.pregunta.id]
            val esCorrecta = altSeleccionada != null && altSeleccionada == altCorrecta

            ResultadoPreguntaDetalle(
                preguntaConAlternativas = pConAlt,
                alternativaSeleccionadaId = altSeleccionada,
                alternativaCorrectaId = altCorrecta,
                esCorrecta = esCorrecta
            )
        }

        val correctas = detalles.count { it.esCorrecta }
        val total = detalles.size
        val incorrectas = total - correctas
        val porcentaje = if (total > 0) (correctas * 100) / total else 0

        return baseState.copy(
            estado = EstadoResolverActividad.RESULTADO,
            resultados = detalles,
            cantidadCorrectas = correctas,
            cantidadIncorrectas = incorrectas,
            porcentajeLogro = porcentaje
        )
    }
}
