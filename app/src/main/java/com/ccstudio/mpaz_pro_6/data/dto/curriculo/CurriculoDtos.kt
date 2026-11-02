package com.ccstudio.mpaz_pro_6.data.dto.curriculo

import kotlinx.serialization.Serializable

// Resúmenes
@Serializable
data class AsignaturaResumen(
    val id: Long,
    val nombre: String
)

@Serializable
data class EstadoAutorizacionResumen(
    val id: Long,
    val nombre: String
)

@Serializable
data class TipoActividadResumen(
    val id: Long,
    val nombre: String
)

@Serializable
data class TipoRecursoResumen(
    val id: Long,
    val nombre: String
)

@Serializable
data class UnidadResumen(
    val id: Long,
    val nombre: String,
    val asignatura: AsignaturaResumen
)

@Serializable
data class ObjetivoAprendizajeResumen(
    val id: Long,
    val codigo: String,
    val descripcion: String
)

@Serializable
data class ContenidoResumen(
    val id: Long,
    val descripcion: String
)

@Serializable
data class RecursoResumen(
    val id: Long,
    val nombre: String,
    val tipoRecurso: TipoRecursoResumen
)

@Serializable
data class ActividadResumen(
    val id: Long,
    val instrucciones: String,
    val habilitada: Boolean
)

@Serializable
data class PreguntaResumen(
    val id: Long,
    val enunciado: String
)

@Serializable
data class AlternativaResumen(
    val id: Long,
    val enunciado: String,
    val correcta: Boolean
)

// Asignatura
@Serializable
data class AsignaturaRequest(val nombre: String)
@Serializable
data class AsignaturaUpdateRequest(val nombre: String? = null)
@Serializable
data class AsignaturaResponse(val id: Long, val nombre: String)

// EstadoAutorizacion
@Serializable
data class EstadoAutorizacionRequest(val nombre: String)
@Serializable
data class EstadoAutorizacionUpdateRequest(val nombre: String? = null)
@Serializable
data class EstadoAutorizacionResponse(val id: Long, val nombre: String)

// TipoActividad
@Serializable
data class TipoActividadRequest(val nombre: String)
@Serializable
data class TipoActividadUpdateRequest(val nombre: String? = null)
@Serializable
data class TipoActividadResponse(val id: Long, val nombre: String)

// TipoRecurso
@Serializable
data class TipoRecursoRequest(val nombre: String)
@Serializable
data class TipoRecursoUpdateRequest(val nombre: String? = null)
@Serializable
data class TipoRecursoResponse(val id: Long, val nombre: String)

// Unidad
@Serializable
data class UnidadRequest(
    val nombre: String,
    val asignaturaId: Long,
    val estadoId: Long
)
@Serializable
data class UnidadUpdateRequest(
    val nombre: String? = null,
    val asignaturaId: Long? = null,
    val estadoId: Long? = null
)
@Serializable
data class UnidadResponse(
    val id: Long,
    val nombre: String,
    val asignatura: AsignaturaResumen,
    val estado: EstadoAutorizacionResumen
)

// ObjetivoAprendizaje
@Serializable
data class ObjetivoAprendizajeRequest(
    val codigo: String,
    val descripcion: String
)
@Serializable
data class ObjetivoAprendizajeUpdateRequest(
    val codigo: String? = null,
    val descripcion: String? = null
)
@Serializable
data class ObjetivoAprendizajeResponse(
    val id: Long,
    val codigo: String,
    val descripcion: String
)

// ObjetivoAprendizajeUnidad (Junction)
@Serializable
data class ObjetivoAprendizajeUnidadRequest(
    val objetivoAprendizajeId: Long,
    val unidadId: Long
)
@Serializable
data class ObjetivoAprendizajeUnidadResponse(
    val id: Long,
    val objetivoAprendizaje: ObjetivoAprendizajeResumen,
    val unidad: UnidadResumen
)

// Contenido
@Serializable
data class ContenidoRequest(
    val descripcion: String,
    val unidadId: Long,
    val estadoId: Long
)
@Serializable
data class ContenidoUpdateRequest(
    val descripcion: String? = null,
    val unidadId: Long? = null, // Inmutable: para validación en servicio
    val estadoId: Long? = null
)
@Serializable
data class ContenidoResponse(
    val id: Long,
    val descripcion: String,
    val unidad: UnidadResumen,
    val estado: EstadoAutorizacionResumen
)

// Recurso
@Serializable
data class RecursoRequest(
    val nombre: String,
    val url: String? = null,
    val contenidoId: Long,
    val tipoRecursoId: Long
)
@Serializable
data class RecursoUpdateRequest(
    val nombre: String? = null,
    val url: String? = null,
    val contenidoId: Long? = null,
    val tipoRecursoId: Long? = null
)
@Serializable
data class RecursoResponse(
    val id: Long,
    val nombre: String,
    val url: String? = null,
    val contenido: ContenidoResumen,
    val tipoRecurso: TipoRecursoResumen
)

// Actividad
@Serializable
data class ActividadRequest(
    val instrucciones: String,
    val habilitada: Boolean = true,
    val contenidoId: Long,
    val tipoActividadId: Long,
    val estadoId: Long
)
@Serializable
data class ActividadUpdateRequest(
    val instrucciones: String? = null,
    val habilitada: Boolean? = null,
    val tipoActividadId: Long? = null,
    val estadoId: Long? = null
)
@Serializable
data class ActividadResponse(
    val id: Long,
    val instrucciones: String,
    val habilitada: Boolean,
    val contenido: ContenidoResumen,
    val tipoActividad: TipoActividadResumen,
    val estado: EstadoAutorizacionResumen
)

// Pregunta
@Serializable
data class PreguntaRequest(
    val enunciado: String,
    val recursoId: Long? = null
)
@Serializable
data class PreguntaUpdateRequest(
    val enunciado: String? = null,
    val recursoId: Long? = null
)
@Serializable
data class PreguntaResponse(
    val id: Long,
    val enunciado: String,
    val recurso: RecursoResumen? = null
)

// Alternativa
@Serializable
data class AlternativaRequest(
    val enunciado: String,
    val correcta: Boolean = false,
    val recursoId: Long? = null
)
@Serializable
data class AlternativaUpdateRequest(
    val enunciado: String? = null,
    val correcta: Boolean? = null,
    val recursoId: Long? = null
)
@Serializable
data class AlternativaResponse(
    val id: Long,
    val enunciado: String,
    val correcta: Boolean,
    val recurso: RecursoResumen? = null
)

// AlternativaPregunta (Junction)
@Serializable
data class AlternativaPreguntaRequest(
    val preguntaId: Long,
    val alternativaId: Long
)
@Serializable
data class AlternativaPreguntaResponse(
    val id: Long,
    val pregunta: PreguntaResumen,
    val alternativa: AlternativaResumen
)

// PreguntaActividad (Junction)
@Serializable
data class PreguntaActividadRequest(
    val actividadId: Long,
    val preguntaId: Long
)
@Serializable
data class PreguntaActividadResponse(
    val id: Long,
    val actividad: ActividadResumen,
    val pregunta: PreguntaResumen
)

// ObjetivoAprendizajeContenido (Junction)
@Serializable
data class ObjetivoAprendizajeContenidoRequest(
    val contenidoId: Long,
    val objetivoAprendizajeId: Long,
    val unidadId: Long
)
@Serializable
data class ObjetivoAprendizajeContenidoResponse(
    val id: Long,
    val contenido: ContenidoResumen,
    val objetivoAprendizaje: ObjetivoAprendizajeResumen,
    val unidad: UnidadResumen
)
