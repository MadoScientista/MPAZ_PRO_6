package com.ccstudio.mpaz_pro_6.data.mapper

import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ActividadResumen
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaResumen
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AsignaturaResumen
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ContenidoResumen
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.EstadoAutorizacionResumen
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ObjetivoAprendizajeResumen
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaResumen
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.RecursoResumen
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.TipoActividadResumen
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.TipoRecursoResumen
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.UnidadResumen
import com.ccstudio.mpaz_pro_6.data.dto.docencia.ActividadCursoResumen
import com.ccstudio.mpaz_pro_6.data.dto.evaluacion.IntentoResumen
import com.ccstudio.mpaz_pro_6.data.dto.identidad.CursoResumen
import com.ccstudio.mpaz_pro_6.data.dto.identidad.NivelResumen
import com.ccstudio.mpaz_pro_6.data.dto.identidad.RolResumen
import com.ccstudio.mpaz_pro_6.data.dto.identidad.SeccionResumen
import com.ccstudio.mpaz_pro_6.data.dto.identidad.UsuarioResumen
import com.ccstudio.mpaz_pro_6.data.dto.informe.InformePedagogicoResumen
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AlternativaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AsignaturaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ContenidoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.EstadoAutorizacionEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ObjetivoAprendizajeEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.PreguntaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.RecursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.TipoActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.TipoRecursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.UnidadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.ActividadCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.AsignaturaCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.evaluacion.IntentoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.CursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.NivelEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.RolEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.SeccionEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.informe.InformePedagogicoEntity

object ResumenMapper {

    fun RolEntity.toResumen() = RolResumen(
        id = id,
        nombre = nombre
    )

    fun NivelEntity.toResumen() = NivelResumen(
        id = id,
        nombre = nombre
    )

    fun SeccionEntity.toResumen() = SeccionResumen(
        id = id,
        letra = letra
    )

    fun CursoEntity.toResumen(nivel: NivelEntity, seccion: SeccionEntity) = CursoResumen(
        id = id,
        annio = annio,
        nivel = nivel.toResumen(),
        seccion = seccion.toResumen()
    )

    fun UsuarioEntity.toResumen() = UsuarioResumen(
        id = id,
        nombreUsuario = nombreUsuario,
        nombreCompleto = nombreCompleto
    )

    fun AsignaturaEntity.toResumen(nivel: NivelEntity? = null) = AsignaturaResumen(
        id = id,
        nombre = nombre,
        nivel = nivel?.toResumen()
    )

    fun EstadoAutorizacionEntity.toResumen() = EstadoAutorizacionResumen(
        id = id,
        nombre = nombre
    )

    fun TipoActividadEntity.toResumen() = TipoActividadResumen(
        id = id,
        nombre = nombre
    )

    fun TipoRecursoEntity.toResumen() = TipoRecursoResumen(
        id = id,
        nombre = nombre
    )

    fun UnidadEntity.toResumen(asignatura: AsignaturaEntity) = UnidadResumen(
        id = id,
        nombre = nombre,
        asignatura = asignatura.toResumen()
    )

    fun ObjetivoAprendizajeEntity.toResumen() = ObjetivoAprendizajeResumen(
        id = id,
        codigo = codigo,
        descripcion = descripcion
    )

    fun ContenidoEntity.toResumen() = ContenidoResumen(
        id = id,
        descripcion = descripcion
    )

    fun RecursoEntity.toResumen(tipoRecurso: TipoRecursoEntity) = RecursoResumen(
        id = id,
        nombre = nombre,
        tipoRecurso = tipoRecurso.toResumen()
    )

    fun ActividadEntity.toResumen() = ActividadResumen(
        id = id,
        instrucciones = instrucciones
    )

    fun ActividadCursoEntity.toResumen(actividad: ActividadEntity? = null, curso: CursoResumen? = null) = ActividadCursoResumen(
        id = id,
        actividad = actividad?.toResumen(),
        curso = curso,
        fechaInicio = fechaInicio,
        fechaFin = fechaFin,
        habilitada = habilitada,
        visible = visible,
        maxIntentos = maxIntentos
    )

    fun AsignaturaCursoEntity.toResponse(
        curso: CursoResumen,
        asignatura: AsignaturaResumen,
        docente: UsuarioResumen? = null
    ) = com.ccstudio.mpaz_pro_6.data.dto.docencia.AsignaturaCursoResponse(
        id = id,
        curso = curso,
        asignatura = asignatura,
        docente = docente
    )

    fun PreguntaEntity.toResumen() = PreguntaResumen(
        id = id,
        enunciado = enunciado
    )

    fun AlternativaEntity.toResumen() = AlternativaResumen(
        id = id,
        enunciado = enunciado,
        correcta = correcta
    )

    fun IntentoEntity.toResumen() = IntentoResumen(
        id = id,
        fechaInicio = fechaInicio,
        fechaFin = fechaFin
    )

    fun InformePedagogicoEntity.toResumen() = InformePedagogicoResumen(
        id = id,
        fecha = fecha
    )
}
