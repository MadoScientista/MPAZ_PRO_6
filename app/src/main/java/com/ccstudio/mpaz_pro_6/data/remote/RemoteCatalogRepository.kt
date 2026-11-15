package com.ccstudio.mpaz_pro_6.data.remote

import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ActividadRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ActividadResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ActividadUpdateRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaPreguntaRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaUpdateRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AsignaturaRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AsignaturaResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ContenidoRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ContenidoResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ObjetivoAprendizajeContenidoRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaActividadRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaUpdateRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.RecursoRequest
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.RecursoResponse
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
import com.ccstudio.mpaz_pro_6.data.dto.identidad.CursoRequest
import com.ccstudio.mpaz_pro_6.data.dto.identidad.CursoResponse
import com.ccstudio.mpaz_pro_6.data.dto.identidad.UsuarioRequest
import com.ccstudio.mpaz_pro_6.data.dto.identidad.UsuarioResponse
import com.ccstudio.mpaz_pro_6.data.dto.informe.GenerarInformeRequest
import com.ccstudio.mpaz_pro_6.data.dto.informe.InformePedagogicoDetalladoResponse
import com.ccstudio.mpaz_pro_6.data.dto.informe.InformePreliminarResponse

/**
 * Escrituras del catalogo contra el backend.
 *
 * <p>Despues de cada escritura el ViewModel correspondiente llama a
 * {@code AppContainer.refresh()} para reconstruir la cache Room.</p>
 */
class RemoteCatalogRepository(private val api: MpazApi) {

    suspend fun crearContenido(body: ContenidoRequest): ContenidoResponse =
        api.crearContenido(body)

    suspend fun crearRecurso(body: RecursoRequest): RecursoResponse =
        api.crearRecurso(body)

    suspend fun crearObjetivoAprendizajeContenido(
        body: ObjetivoAprendizajeContenidoRequest
    ) = api.crearObjetivoAprendizajeContenido(body)

    suspend fun crearActividad(body: ActividadRequest): ActividadResponse =
        api.crearActividad(body)

    suspend fun crearPregunta(body: PreguntaRequest): PreguntaResponse =
        api.crearPregunta(body)

    suspend fun crearAlternativa(body: AlternativaRequest): AlternativaResponse =
        api.crearAlternativa(body)

    suspend fun crearPreguntaActividad(body: PreguntaActividadRequest) =
        api.crearPreguntaActividad(body)

    suspend fun crearAlternativaPregunta(body: AlternativaPreguntaRequest) =
        api.crearAlternativaPregunta(body)

    suspend fun crearActividadCurso(body: ActividadCursoRequest): ActividadCursoResponse =
        api.crearActividadCurso(body)

    suspend fun crearAsignatura(body: AsignaturaRequest): AsignaturaResponse =
        api.crearAsignatura(body)

    suspend fun crearCurso(body: CursoRequest): CursoResponse =
        api.crearCurso(body)

    suspend fun crearUnidad(body: UnidadRequest): UnidadResponse =
        api.crearUnidad(body)

    suspend fun actualizarUnidad(
        unidadId: Long,
        body: UnidadUpdateRequest
    ): UnidadResponse = api.actualizarUnidad(unidadId, body)

    suspend fun crearUsuario(body: UsuarioRequest): UsuarioResponse =
        api.crearUsuario(body)

    suspend fun cambiarUsuarioActivo(usuarioId: Long, activo: Boolean): UsuarioResponse =
        api.cambiarUsuarioActivo(usuarioId, activo)

    suspend fun actualizarActividad(
        actividadId: Long,
        body: ActividadUpdateRequest
    ): ActividadResponse = api.actualizarActividad(actividadId, body)

    suspend fun actualizarPregunta(
        preguntaId: Long,
        body: PreguntaUpdateRequest
    ): PreguntaResponse = api.actualizarPregunta(preguntaId, body)

    suspend fun actualizarAlternativa(
        alternativaId: Long,
        body: AlternativaUpdateRequest
    ): AlternativaResponse = api.actualizarAlternativa(alternativaId, body)

    suspend fun cambiarAlternativaCorrecta(
        alternativaId: Long,
        correcta: Boolean
    ): AlternativaResponse = api.cambiarAlternativaCorrecta(alternativaId, correcta)

    suspend fun actualizarActividadCurso(
        actividadCursoId: Long,
        body: ActividadCursoUpdateRequest
    ): ActividadCursoResponse = api.actualizarActividadCurso(actividadCursoId, body)

    suspend fun cambiarActividadCursoHabilitada(
        actividadCursoId: Long,
        habilitada: Boolean
    ): ActividadCursoResponse =
        api.cambiarActividadCursoHabilitada(actividadCursoId, habilitada)

    suspend fun cambiarActividadCursoVisible(
        actividadCursoId: Long,
        visible: Boolean
    ): ActividadCursoResponse =
        api.cambiarActividadCursoVisible(actividadCursoId, visible)

    suspend fun eliminarPregunta(preguntaId: Long) {
        api.eliminarPregunta(preguntaId).requireOk("eliminar pregunta")
    }

    suspend fun eliminarAlternativa(alternativaId: Long) {
        api.eliminarAlternativa(alternativaId).requireOk("eliminar alternativa")
    }

    suspend fun eliminarPreguntaActividad(id: Long) {
        api.eliminarPreguntaActividad(id).requireOk("eliminar asociación pregunta-actividad")
    }

    suspend fun eliminarAlternativaPregunta(id: Long) {
        api.eliminarAlternativaPregunta(id).requireOk("eliminar asociación alternativa-pregunta")
    }

    suspend fun crearAsignaturaCurso(
        body: AsignaturaCursoRequest
    ): AsignaturaCursoResponse = api.crearAsignaturaCurso(body)

    suspend fun actualizarAsignaturaCurso(
        id: Long,
        body: AsignaturaCursoUpdateRequest
    ): AsignaturaCursoResponse = api.actualizarAsignaturaCurso(id, body)

    suspend fun crearUsuarioCurso(
        body: UsuarioCursoRequest
    ): UsuarioCursoResponse = api.crearUsuarioCurso(body)

    suspend fun eliminarAsignaturaCurso(id: Long) {
        val response = api.eliminarAsignaturaCurso(id)
        if (!response.isSuccessful) {
            throw IllegalStateException("No se pudo eliminar la asignatura del curso (${response.code()})")
        }
    }

    suspend fun eliminarUsuarioCurso(id: Long) {
        val response = api.eliminarUsuarioCurso(id)
        if (!response.isSuccessful) {
            throw IllegalStateException("No se pudo eliminar la participación del curso (${response.code()})")
        }
    }

    suspend fun previsualizarInforme(
        usuarioId: Long,
        unidadId: Long,
        fechaCorte: String?
    ): InformePreliminarResponse =
        api.previsualizarInforme(usuarioId, unidadId, fechaCorte)

    suspend fun generarInforme(
        body: GenerarInformeRequest
    ): InformePedagogicoDetalladoResponse =
        api.generarInformeDesdeResultados(body)

    suspend fun eliminarInforme(informeId: Long) {
        api.eliminarInforme(informeId).requireOk("eliminar informe")
    }

    private fun retrofit2.Response<Unit>.requireOk(action: String) {
        if (!isSuccessful) {
            throw IllegalStateException("No se pudo $action (${code()})")
        }
    }
}
