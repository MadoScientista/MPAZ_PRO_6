package com.ccstudio.mpaz_pro_6.data.remote

import androidx.room.withTransaction
import com.ccstudio.mpaz_pro_6.data.dto.identidad.UsuarioResponse
import com.ccstudio.mpaz_pro_6.data.dto.identidad.UsuarioResumen
import com.ccstudio.mpaz_pro_6.data.local.MpazDatabase
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Sincronizacion inicial del catalogo remoto hacia Room.
 *
 * <p>Descarga todo antes de tocar la base local. Si una llamada falla, la
 * cache anterior queda intacta. La escritura se hace en una transaccion y en
 * orden de dependencias de claves foraneas.</p>
 */
class InitialSync(
    private val api: MpazApi,
    private val database: MpazDatabase
) {

    suspend fun sync(currentUser: UsuarioResponse): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
        val rol = currentUser.rol.nombre.uppercase()
        val esUtp = rol == "UTP"

        val roles = api.getRoles()
        val niveles = api.getNiveles()
        val secciones = api.getSecciones()
        val cursos = api.getCursos()
        val asignaturas = api.getAsignaturas()
        val estados = api.getEstadosAutorizacion()
        val tiposActividad = api.getTiposActividad()
        val tiposRecurso = api.getTiposRecurso()
        val unidades = api.getUnidades()
        val objetivos = api.getObjetivosAprendizaje()
        val objetivosUnidad = unidades.flatMap { unidad ->
            api.getObjetivosAprendizajeUnidad(unidadId = unidad.id)
        }
        val contenidos = api.getContenidos()
        val objetivosContenido = contenidos.flatMap { contenido ->
            api.getObjetivosAprendizajeContenido(contenidoId = contenido.id)
        }
        val recursos = api.getRecursos()
        val actividades = api.getActividades()
        val actividadesCurso = api.getActividadesCurso()
        val preguntas = api.getPreguntas()
        val alternativas = api.getAlternativas()
        val alternativasPregunta = api.getAlternativasPregunta()
        val preguntasActividad = api.getPreguntasActividad()
        val asignaturasCurso = api.getAsignaturasCurso()
        val usuariosCurso = api.getUsuariosCurso()

        val esEstudiante = rol == "ESTUDIANTE"
        val intentos = if (esEstudiante) {
            api.getIntentos(usuarioId = currentUser.id)
        } else {
            emptyList()
        }
        val respuestas = intentos.flatMap { api.getRespuestas(it.id) }
        val informes = if (esEstudiante) {
            api.getInformes(usuarioId = currentUser.id)
        } else {
            api.getInformes()
        }
        val detallesInforme = informes.flatMap { api.getDetallesInforme(it.id) }

        val usuarios = construirUsuarios(
            currentUser = currentUser,
            esUtp = esUtp,
            usuariosRemotos = if (esUtp) api.getUsuarios() else emptyList(),
            asignaturasCurso = asignaturasCurso,
            usuariosCurso = usuariosCurso
        )

        val cursoIds = cursos.map { it.id }.toSet()
        val unidadIds = unidades.map { it.id }.toSet()
        val contenidoIds = contenidos.map { it.id }.toSet()
        val recursoIds = recursos.filter { it.contenido.id in contenidoIds }
            .map { it.id }
            .toSet()

        val actividadesValidas = actividades
            .mapNotNull { it.toEntityOrNull() }
            .filter { it.contenidoId in contenidoIds }
        val actividadIds = actividadesValidas.map { it.id }.toSet()

        val preguntasValidas = preguntas.filter { pregunta ->
            val recursoId = pregunta.recurso?.id
            recursoId == null || recursoId in recursoIds
        }
        val preguntaIds = preguntasValidas.map { it.id }.toSet()

        val alternativasValidas = alternativas.filter { alternativa ->
            val recursoId = alternativa.recurso?.id
            recursoId == null || recursoId in recursoIds
        }
        val alternativaIds = alternativasValidas.map { it.id }.toSet()

        val usuarioIds = usuarios.map { it.id }.toSet()
        val actividadesCursoValidas = actividadesCurso
            .mapNotNull { it.toEntityOrNull() }
            .filter {
                it.actividadId in actividadIds
                        && it.cursoId in cursoIds
            }
        val actividadCursoIds = actividadesCursoValidas.map { it.id }.toSet()

        val intentosValidos = intentos
            .mapNotNull { it.toEntityOrNull() }
            .filter {
                it.usuarioId in usuarioIds
                        && it.actividadCursoId in actividadCursoIds
            }
        val intentoIds = intentosValidos.map { it.id }.toSet()

        val respuestasValidas = respuestas.filter {
            it.intento.id in intentoIds
                    && it.pregunta.id in preguntaIds
                    && it.alternativa.id in alternativaIds
        }

        val informesValidos = informes.filter {
            it.usuario.id in usuarioIds
                    && it.unidad.id in unidadIds
        }
        val informeIds = informesValidos.map { it.id }.toSet()
        val objetivoIds = objetivos.map { it.id }.toSet()
        val detallesInformeValidos = detallesInforme.filter {
            it.informePedagogico.id in informeIds
                    && it.objetivoAprendizaje.id in objetivoIds
        }

        database.clearAllTables()

        database.withTransaction {
            val identidad = database.identidadDao()
            val curriculo = database.curriculoDao()
            val docencia = database.docenciaDao()
            val evaluacion = database.evaluacionDao()
            val informe = database.informeDao()

            roles.forEach { identidad.insertRol(it.toEntity()) }
            niveles.forEach { identidad.insertNivel(it.toEntity()) }
            secciones.forEach { identidad.insertSeccion(it.toEntity()) }
            cursos.forEach { identidad.insertCurso(it.toEntity()) }
            usuarios.forEach { identidad.insertUsuario(it) }

            asignaturas.forEach { curriculo.insertAsignatura(it.toEntity()) }
            estados.forEach { curriculo.insertEstadoAutorizacion(it.toEntity()) }
            tiposActividad.forEach { curriculo.insertTipoActividad(it.toEntity()) }
            tiposRecurso.forEach { curriculo.insertTipoRecurso(it.toEntity()) }

            unidades.forEach { curriculo.insertUnidad(it.toEntity()) }
            objetivos.forEach { curriculo.insertObjetivoAprendizaje(it.toEntity()) }
            objetivosUnidad
                .filter { it.unidad.id in unidadIds }
                .forEach { curriculo.insertObjetivoAprendizajeUnidad(it.toEntity()) }

            contenidos.forEach { curriculo.insertContenido(it.toEntity()) }
            objetivosContenido
                .filter {
                    it.contenido.id in contenidoIds
                            && it.unidad.id in unidadIds
                }
                .forEach { curriculo.insertObjetivoAprendizajeContenido(it.toEntity()) }

            recursos
                .filter { it.contenido.id in contenidoIds }
                .forEach { curriculo.insertRecurso(it.toEntity()) }

            actividadesValidas.forEach { curriculo.insertActividad(it) }
            preguntasValidas.forEach { curriculo.insertPregunta(it.toEntity()) }
            alternativasValidas.forEach { curriculo.insertAlternativa(it.toEntity()) }

            alternativasPregunta
                .filter {
                    it.pregunta.id in preguntaIds
                            && it.alternativa.id in alternativaIds
                }
                .forEach { curriculo.insertAlternativaPregunta(it.toEntity()) }

            preguntasActividad
                .filter {
                    it.actividad.id in actividadIds
                            && it.pregunta.id in preguntaIds
                }
                .forEach { curriculo.insertPreguntaActividad(it.toEntity()) }

            actividadesCursoValidas.forEach { docencia.insertActividadCurso(it) }

            asignaturasCurso.forEach { docencia.insertAsignaturaCurso(it.toEntity()) }
            usuariosCurso.forEach { docencia.insertUsuarioCurso(it.toEntity()) }

            intentosValidos.forEach { evaluacion.insertIntento(it) }
            respuestasValidas.forEach { evaluacion.insertRespuesta(it.toEntity()) }

            informesValidos.forEach { informe.insertInformePedagogico(it.toEntity()) }
            if (detallesInformeValidos.isNotEmpty()) {
                informe.insertDetallesInforme(
                    detallesInformeValidos.map { it.toEntity() }
                )
            }
        }
    }
    }

    private suspend fun construirUsuarios(
        currentUser: UsuarioResponse,
        esUtp: Boolean,
        usuariosRemotos: List<UsuarioResponse>,
        asignaturasCurso: List<com.ccstudio.mpaz_pro_6.data.dto.docencia.AsignaturaCursoResponse>,
        usuariosCurso: List<com.ccstudio.mpaz_pro_6.data.dto.docencia.UsuarioCursoResponse>
    ): List<UsuarioEntity> {
        val usuarios = linkedMapOf<Long, UsuarioEntity>()
        usuarios[currentUser.id] = currentUser.toEntity()

        if (esUtp) {
            usuariosRemotos.forEach { usuarios[it.id] = it.toEntity() }
        }

        asignaturasCurso.mapNotNull { it.docente }.forEach { docente ->
            usuarios.putIfAbsent(
                docente.id,
                docente.toEntity(rolId = 2L)
            )
        }

        usuariosCurso.forEach { participacion ->
            usuarios.putIfAbsent(
                participacion.usuario.id,
                participacion.usuario.toEntity(rolId = 3L)
            )
        }
        return usuarios.values.toList()
    }

    private fun UsuarioResumen.toEntity(rolId: Long) = UsuarioEntity(
        id = id,
        nombreUsuario = nombreUsuario,
        passwordHash = "",
        pnombre = nombreCompleto,
        apaterno = "",
        rolId = rolId,
        activo = true
    )
}
