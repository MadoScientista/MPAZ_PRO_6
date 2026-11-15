package com.ccstudio.mpaz_pro_6.data.remote

import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ActividadResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaPreguntaResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AsignaturaResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ContenidoResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.EstadoAutorizacionResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ObjetivoAprendizajeContenidoResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ObjetivoAprendizajeResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ObjetivoAprendizajeUnidadResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaActividadResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.RecursoResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.TipoActividadResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.TipoRecursoResponse
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.UnidadResponse
import com.ccstudio.mpaz_pro_6.data.dto.docencia.ActividadCursoResponse
import com.ccstudio.mpaz_pro_6.data.dto.docencia.AsignaturaCursoResponse
import com.ccstudio.mpaz_pro_6.data.dto.docencia.UsuarioCursoResponse
import com.ccstudio.mpaz_pro_6.data.dto.evaluacion.IntentoResponse
import com.ccstudio.mpaz_pro_6.data.dto.evaluacion.RespuestaResponse
import com.ccstudio.mpaz_pro_6.data.dto.identidad.CursoResponse
import com.ccstudio.mpaz_pro_6.data.dto.identidad.NivelResponse
import com.ccstudio.mpaz_pro_6.data.dto.identidad.RolResponse
import com.ccstudio.mpaz_pro_6.data.dto.identidad.SeccionResponse
import com.ccstudio.mpaz_pro_6.data.dto.identidad.UsuarioResponse
import com.ccstudio.mpaz_pro_6.data.dto.informe.DetalleInformePedagogicoResponse
import com.ccstudio.mpaz_pro_6.data.dto.informe.InformePedagogicoResponse
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AlternativaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AlternativaPreguntaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AsignaturaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ContenidoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.EstadoAutorizacionEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ObjetivoAprendizajeContenidoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ObjetivoAprendizajeEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ObjetivoAprendizajeUnidadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.PreguntaActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.PreguntaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.RecursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.TipoActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.TipoRecursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.UnidadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.ActividadCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.AsignaturaCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.UsuarioCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.evaluacion.IntentoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.evaluacion.RespuestaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.CursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.NivelEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.RolEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.SeccionEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.informe.DetalleInformePedagogicoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.informe.InformePedagogicoEntity

fun RolResponse.toEntity() = RolEntity(id = id, nombre = nombre)

fun NivelResponse.toEntity() = NivelEntity(id = id, nombre = nombre)

fun SeccionResponse.toEntity() = SeccionEntity(id = id, letra = letra)

fun CursoResponse.toEntity() = CursoEntity(
    id = id,
    annio = annio,
    nivelId = nivel.id,
    seccionId = seccion.id
)

fun UsuarioResponse.toEntity() = UsuarioEntity(
    id = id,
    nombreUsuario = nombreUsuario,
    passwordHash = "",
    pnombre = pnombre ?: "",
    snombre = snombre,
    apaterno = apaterno ?: "",
    amaterno = amaterno,
    rolId = rol.id,
    activo = activo
)

fun AsignaturaResponse.toEntity() = AsignaturaEntity(
    id = id,
    nombre = nombre,
    nivelId = nivel?.id ?: 1L
)

fun EstadoAutorizacionResponse.toEntity() =
    EstadoAutorizacionEntity(id = id, nombre = nombre)

fun TipoActividadResponse.toEntity() =
    TipoActividadEntity(id = id, nombre = nombre)

fun TipoRecursoResponse.toEntity() =
    TipoRecursoEntity(id = id, nombre = nombre)

fun UnidadResponse.toEntity() = UnidadEntity(
    id = id,
    nombre = nombre,
    asignaturaId = asignatura.id,
    estadoId = estado.id
)

fun ObjetivoAprendizajeResponse.toEntity() = ObjetivoAprendizajeEntity(
    id = id,
    codigo = codigo,
    descripcion = descripcion
)

fun ObjetivoAprendizajeUnidadResponse.toEntity() = ObjetivoAprendizajeUnidadEntity(
    id = id,
    objetivoAprendizajeId = objetivoAprendizaje.id,
    unidadId = unidad.id
)

fun ContenidoResponse.toEntity() = ContenidoEntity(
    id = id,
    descripcion = descripcion ?: "",
    unidadId = unidad.id,
    estadoId = estado.id
)

fun ObjetivoAprendizajeContenidoResponse.toEntity() =
    ObjetivoAprendizajeContenidoEntity(
        id = id,
        contenidoId = contenido.id,
        objetivoAprendizajeId = objetivoAprendizaje.id,
        unidadId = unidad.id
    )

fun RecursoResponse.toEntity() = RecursoEntity(
    id = id,
    nombre = nombre,
    url = url,
    contenidoId = contenido.id,
    tipoRecursoId = tipoRecurso.id
)

fun ActividadResponse.toEntityOrNull(): ActividadEntity? {
    val contenidoId = contenido?.id ?: return null
    val tipoActividadId = tipoActividad?.id ?: return null
    val estadoId = estado?.id ?: return null
    return ActividadEntity(
        id = id,
        instrucciones = instrucciones,
        contenidoId = contenidoId,
        tipoActividadId = tipoActividadId,
        estadoId = estadoId
    )
}

fun PreguntaResponse.toEntity() = PreguntaEntity(
    id = id,
    enunciado = enunciado,
    recursoId = recurso?.id
)

fun AlternativaResponse.toEntity() = AlternativaEntity(
    id = id,
    enunciado = enunciado,
    correcta = correcta,
    recursoId = recurso?.id
)

fun AlternativaPreguntaResponse.toEntity() = AlternativaPreguntaEntity(
    id = id,
    preguntaId = pregunta.id,
    alternativaId = alternativa.id
)

fun PreguntaActividadResponse.toEntity() = PreguntaActividadEntity(
    id = id,
    actividadId = actividad.id,
    preguntaId = pregunta.id
)

fun AsignaturaCursoResponse.toEntity() = AsignaturaCursoEntity(
    id = id,
    cursoId = curso.id,
    asignaturaId = asignatura.id,
    docenteId = docente?.id
)

fun UsuarioCursoResponse.toEntity() = UsuarioCursoEntity(
    id = id,
    usuarioId = usuario.id,
    cursoId = curso.id
)

fun ActividadCursoResponse.toEntityOrNull(): ActividadCursoEntity? {
    val actividadId = actividad?.id ?: return null
    val cursoId = curso?.id ?: return null
    return ActividadCursoEntity(
        id = id,
        actividadId = actividadId,
        cursoId = cursoId,
        fechaInicio = fechaInicio,
        fechaFin = fechaFin,
        habilitada = habilitada,
        visible = visible,
        maxIntentos = maxIntentos
    )
}

fun IntentoResponse.toEntityOrNull(): IntentoEntity? {
    val actividadCursoId = actividadCurso?.id ?: return null
    return IntentoEntity(
        id = id,
        usuarioId = usuario.id,
        actividadCursoId = actividadCursoId,
        fechaInicio = fechaInicio,
        fechaFin = fechaFin
    )
}

fun RespuestaResponse.toEntity() = RespuestaEntity(
    id = id,
    intentoId = intento.id,
    preguntaId = pregunta.id,
    alternativaId = alternativa.id
)

fun InformePedagogicoResponse.toEntity() = InformePedagogicoEntity(
    id = id,
    usuarioId = usuario.id,
    unidadId = unidad.id,
    fecha = fecha,
    descripcion = descripcion,
    fechaCorte = fechaCorte,
    origen = origen
)

fun DetalleInformePedagogicoResponse.toEntity() = DetalleInformePedagogicoEntity(
    id = id,
    informePedagogicoId = informePedagogico.id,
    objetivoAprendizajeId = objetivoAprendizaje.id,
    correctas = correctas,
    incorrectas = incorrectas
)
