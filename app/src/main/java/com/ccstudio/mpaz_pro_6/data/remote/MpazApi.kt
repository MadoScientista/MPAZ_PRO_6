package com.ccstudio.mpaz_pro_6.data.remote

import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ActividadRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ActividadResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ActividadUpdateRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaPreguntaRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaPreguntaResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaUpdateRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AsignaturaRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AsignaturaResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ContenidoRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ContenidoResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.EstadoAutorizacionResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ObjetivoAprendizajeContenidoRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ObjetivoAprendizajeContenidoResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ObjetivoAprendizajeResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ObjetivoAprendizajeUnidadResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaActividadRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaActividadResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaUpdateRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.RecursoRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.RecursoResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.TipoActividadResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.TipoRecursoResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.UnidadRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.UnidadResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.UnidadUpdateRequest
import com.ccstudio.mpaz_pro_6.data.dto.docencia.ActividadCursoRequest
import com.ccstudio.mpaz_pro_6.data.dto.docencia.ActividadCursoResponse
import com.ccstudio.mpaz_pro_6.data.dto.docencia.ActividadCursoUpdateRequest
import com.ccstudio.mpaz_pro_6.data.dto.docencia.AsignaturaCursoRequest
import com.ccstudio.mpaz_pro_6.data.dto.docencia.AsignaturaCursoResponse
import com.ccstudio.mpaz_pro_6.data.dto.docencia.AsignaturaCursoUpdateRequest
import com.ccstudio.mpaz_pro_6.data.dto.docencia.UsuarioCursoRequest
import com.ccstudio.mpaz_pro_6.data.dto.docencia.UsuarioCursoResponse
import com.ccstudio.mpaz_pro_6.data.dto.evaluacion.IntentoRequest
import com.ccstudio.mpaz_pro_6.data.dto.evaluacion.IntentoResponse
import com.ccstudio.mpaz_pro_6.data.dto.evaluacion.RespuestaRequest
import com.ccstudio.mpaz_pro_6.data.dto.evaluacion.RespuestaResponse
import com.ccstudio.mpaz_pro_6.data.dto.identidad.CursoRequest
import com.ccstudio.mpaz_pro_6.data.dto.identidad.CursoResponse
import com.ccstudio.mpaz_pro_6.data.dto.identidad.NivelResponse
import com.ccstudio.mpaz_pro_6.data.dto.identidad.RolResponse
import com.ccstudio.mpaz_pro_6.data.dto.identidad.SeccionResponse
import com.ccstudio.mpaz_pro_6.data.dto.identidad.UsuarioRequest
import com.ccstudio.mpaz_pro_6.data.dto.identidad.UsuarioResponse
import com.ccstudio.mpaz_pro_6.data.dto.informe.DetalleInformePedagogicoResponse
import com.ccstudio.mpaz_pro_6.data.dto.informe.GenerarInformeRequest
import com.ccstudio.mpaz_pro_6.data.dto.informe.InformePedagogicoDetalladoResponse
import com.ccstudio.mpaz_pro_6.data.dto.informe.InformePedagogicoResponse
import com.ccstudio.mpaz_pro_6.data.dto.informe.InformePreliminarResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Contrato HTTP de MPAZ contra el backend de 27 tablas.
 *
 * <p>Las lecturas alimentan la sincronizacion inicial de Room. Las escrituras
 * se agregan por flujo (estudiante, docente, UTP).</p>
 */
interface MpazApi {

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    @GET("api/auth/me")
    suspend fun me(): UsuarioResponse

    @POST("api/auth/logout")
    suspend fun logout(): Response<Unit>

    // Identidad
    @GET("api/roles")
    suspend fun getRoles(): List<RolResponse>

    @GET("api/niveles")
    suspend fun getNiveles(): List<NivelResponse>

    @GET("api/secciones")
    suspend fun getSecciones(): List<SeccionResponse>

    @GET("api/cursos")
    suspend fun getCursos(@Query("annio") annio: Int? = null): List<CursoResponse>

    @GET("api/usuarios")
    suspend fun getUsuarios(@Query("rolId") rolId: Long? = null): List<UsuarioResponse>

    // Curriculo
    @GET("api/asignaturas")
    suspend fun getAsignaturas(
        @Query("nivelId") nivelId: Long? = null
    ): List<AsignaturaResponse>

    @GET("api/estados-autorizacion")
    suspend fun getEstadosAutorizacion(): List<EstadoAutorizacionResponse>

    @GET("api/tipos-actividad")
    suspend fun getTiposActividad(): List<TipoActividadResponse>

    @GET("api/tipos-recurso")
    suspend fun getTiposRecurso(): List<TipoRecursoResponse>

    @GET("api/unidades")
    suspend fun getUnidades(
        @Query("asignaturaId") asignaturaId: Long? = null,
        @Query("estadoId") estadoId: Long? = null
    ): List<UnidadResponse>

    @GET("api/objetivos-aprendizaje")
    suspend fun getObjetivosAprendizaje(
        @Query("codigo") codigo: String? = null
    ): List<ObjetivoAprendizajeResponse>

    @GET("api/objetivos-aprendizaje-unidad")
    suspend fun getObjetivosAprendizajeUnidad(
        @Query("unidadId") unidadId: Long? = null,
        @Query("objetivoAprendizajeId") objetivoAprendizajeId: Long? = null
    ): List<ObjetivoAprendizajeUnidadResponse>

    @GET("api/contenidos")
    suspend fun getContenidos(
        @Query("unidadId") unidadId: Long? = null,
        @Query("estadoId") estadoId: Long? = null
    ): List<ContenidoResponse>

    @GET("api/objetivos-aprendizaje-contenido")
    suspend fun getObjetivosAprendizajeContenido(
        @Query("contenidoId") contenidoId: Long? = null,
        @Query("objetivoAprendizajeId") objetivoAprendizajeId: Long? = null,
        @Query("unidadId") unidadId: Long? = null
    ): List<ObjetivoAprendizajeContenidoResponse>

    @GET("api/recursos")
    suspend fun getRecursos(
        @Query("contenidoId") contenidoId: Long? = null,
        @Query("tipoRecursoId") tipoRecursoId: Long? = null
    ): List<RecursoResponse>

    @GET("api/actividades")
    suspend fun getActividades(
        @Query("contenidoId") contenidoId: Long? = null,
        @Query("unidadId") unidadId: Long? = null,
        @Query("estadoId") estadoId: Long? = null
    ): List<ActividadResponse>

    @GET("api/preguntas")
    suspend fun getPreguntas(
        @Query("recursoId") recursoId: Long? = null,
        @Query("contenidoId") contenidoId: Long? = null
    ): List<PreguntaResponse>

    @GET("api/alternativas")
    suspend fun getAlternativas(
        @Query("recursoId") recursoId: Long? = null
    ): List<AlternativaResponse>

    @GET("api/alternativas-pregunta")
    suspend fun getAlternativasPregunta(
        @Query("preguntaId") preguntaId: Long? = null,
        @Query("alternativaId") alternativaId: Long? = null
    ): List<AlternativaPreguntaResponse>

    @GET("api/preguntas-actividad")
    suspend fun getPreguntasActividad(
        @Query("actividadId") actividadId: Long? = null,
        @Query("preguntaId") preguntaId: Long? = null
    ): List<PreguntaActividadResponse>

    // Docencia
    @GET("api/asignaturas-curso")
    suspend fun getAsignaturasCurso(
        @Query("cursoId") cursoId: Long? = null,
        @Query("asignaturaId") asignaturaId: Long? = null,
        @Query("docenteId") docenteId: Long? = null
    ): List<AsignaturaCursoResponse>

    @GET("api/usuarios-curso")
    suspend fun getUsuariosCurso(
        @Query("usuarioId") usuarioId: Long? = null,
        @Query("cursoId") cursoId: Long? = null
    ): List<UsuarioCursoResponse>

    @GET("api/actividades-curso")
    suspend fun getActividadesCurso(
        @Query("cursoId") cursoId: Long? = null,
        @Query("actividadId") actividadId: Long? = null
    ): List<ActividadCursoResponse>

    // Evaluacion del estudiante
    @POST("api/intentos")
    suspend fun crearIntento(@Body body: IntentoRequest): IntentoResponse

    @POST("api/intentos/{id}/cerrar")
    suspend fun cerrarIntento(@Path("id") intentoId: Long): IntentoResponse

    @POST("api/respuestas")
    suspend fun crearRespuesta(@Body body: RespuestaRequest): RespuestaResponse

    @GET("api/intentos")
    suspend fun getIntentos(
        @Query("usuarioId") usuarioId: Long? = null,
        @Query("actividadCursoId") actividadCursoId: Long? = null
    ): List<IntentoResponse>

    @GET("api/respuestas")
    suspend fun getRespuestas(
        @Query("intentoId") intentoId: Long
    ): List<RespuestaResponse>

    @GET("api/informes-pedagogicos")
    suspend fun getInformes(
        @Query("usuarioId") usuarioId: Long? = null,
        @Query("unidadId") unidadId: Long? = null
    ): List<InformePedagogicoResponse>

    @GET("api/detalles-informe-pedagogico")
    suspend fun getDetallesInforme(
        @Query("informePedagogicoId") informeId: Long
    ): List<DetalleInformePedagogicoResponse>

    // Escrituras de curriculo y docencia
    @POST("api/asignaturas")
    suspend fun crearAsignatura(@Body body: AsignaturaRequest): AsignaturaResponse

    @POST("api/cursos")
    suspend fun crearCurso(@Body body: CursoRequest): CursoResponse

    @POST("api/unidades")
    suspend fun crearUnidad(@Body body: UnidadRequest): UnidadResponse

    @PUT("api/unidades/{id}")
    suspend fun actualizarUnidad(
        @Path("id") unidadId: Long,
        @Body body: UnidadUpdateRequest
    ): UnidadResponse

    @POST("api/contenidos")
    suspend fun crearContenido(@Body body: ContenidoRequest): ContenidoResponse

    @POST("api/recursos")
    suspend fun crearRecurso(@Body body: RecursoRequest): RecursoResponse

    @POST("api/actividades")
    suspend fun crearActividad(@Body body: ActividadRequest): ActividadResponse

    @POST("api/preguntas")
    suspend fun crearPregunta(@Body body: PreguntaRequest): PreguntaResponse

    @POST("api/alternativas")
    suspend fun crearAlternativa(@Body body: AlternativaRequest): AlternativaResponse

    @POST("api/preguntas-actividad")
    suspend fun crearPreguntaActividad(
        @Body body: PreguntaActividadRequest
    ): PreguntaActividadResponse

    @POST("api/alternativas-pregunta")
    suspend fun crearAlternativaPregunta(
        @Body body: AlternativaPreguntaRequest
    ): AlternativaPreguntaResponse

    @POST("api/objetivos-aprendizaje-contenido")
    suspend fun crearObjetivoAprendizajeContenido(
        @Body body: ObjetivoAprendizajeContenidoRequest
    ): ObjetivoAprendizajeContenidoResponse

    @POST("api/actividades-curso")
    suspend fun crearActividadCurso(
        @Body body: ActividadCursoRequest
    ): ActividadCursoResponse

    @POST("api/usuarios")
    suspend fun crearUsuario(@Body body: UsuarioRequest): UsuarioResponse

    @PUT("api/usuarios/{id}/activo")
    suspend fun cambiarUsuarioActivo(
        @Path("id") usuarioId: Long,
        @Query("activo") activo: Boolean
    ): UsuarioResponse

    @POST("api/asignaturas-curso")
    suspend fun crearAsignaturaCurso(
        @Body body: AsignaturaCursoRequest
    ): AsignaturaCursoResponse

    @PUT("api/asignaturas-curso/{id}")
    suspend fun actualizarAsignaturaCurso(
        @Path("id") id: Long,
        @Body body: AsignaturaCursoUpdateRequest
    ): AsignaturaCursoResponse

    @POST("api/usuarios-curso")
    suspend fun crearUsuarioCurso(@Body body: UsuarioCursoRequest): UsuarioCursoResponse

    @DELETE("api/asignaturas-curso/{id}")
    suspend fun eliminarAsignaturaCurso(@Path("id") id: Long): Response<Unit>

    @DELETE("api/usuarios-curso/{id}")
    suspend fun eliminarUsuarioCurso(@Path("id") id: Long): Response<Unit>

    @PUT("api/actividades/{id}")
    suspend fun actualizarActividad(
        @Path("id") id: Long,
        @Body body: ActividadUpdateRequest
    ): ActividadResponse

    @PUT("api/preguntas/{id}")
    suspend fun actualizarPregunta(
        @Path("id") id: Long,
        @Body body: PreguntaUpdateRequest
    ): PreguntaResponse

    @PUT("api/alternativas/{id}")
    suspend fun actualizarAlternativa(
        @Path("id") id: Long,
        @Body body: AlternativaUpdateRequest
    ): AlternativaResponse

    @PUT("api/alternativas/{id}/correcta")
    suspend fun cambiarAlternativaCorrecta(
        @Path("id") id: Long,
        @Query("correcta") correcta: Boolean
    ): AlternativaResponse

    @PUT("api/actividades-curso/{id}")
    suspend fun actualizarActividadCurso(
        @Path("id") id: Long,
        @Body body: ActividadCursoUpdateRequest
    ): ActividadCursoResponse

    @PUT("api/actividades-curso/{id}/habilitada")
    suspend fun cambiarActividadCursoHabilitada(
        @Path("id") id: Long,
        @Query("habilitada") habilitada: Boolean
    ): ActividadCursoResponse

    @PUT("api/actividades-curso/{id}/visible")
    suspend fun cambiarActividadCursoVisible(
        @Path("id") id: Long,
        @Query("visible") visible: Boolean
    ): ActividadCursoResponse

    @DELETE("api/preguntas/{id}")
    suspend fun eliminarPregunta(@Path("id") id: Long): Response<Unit>

    @DELETE("api/alternativas/{id}")
    suspend fun eliminarAlternativa(@Path("id") id: Long): Response<Unit>

    @DELETE("api/preguntas-actividad/{id}")
    suspend fun eliminarPreguntaActividad(@Path("id") id: Long): Response<Unit>

    @DELETE("api/alternativas-pregunta/{id}")
    suspend fun eliminarAlternativaPregunta(@Path("id") id: Long): Response<Unit>

    // Informes
    @GET("api/informes-pedagogicos/preliminar")
    suspend fun previsualizarInforme(
        @Query("usuarioId") usuarioId: Long,
        @Query("unidadId") unidadId: Long,
        @Query("fechaCorte") fechaCorte: String? = null
    ): InformePreliminarResponse

    @POST("api/informes-pedagogicos/desde-resultados")
    suspend fun generarInformeDesdeResultados(
        @Body body: GenerarInformeRequest
    ): InformePedagogicoDetalladoResponse

    @DELETE("api/informes-pedagogicos/{id}")
    suspend fun eliminarInforme(@Path("id") id: Long): Response<Unit>
}
