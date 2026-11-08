package com.ccstudio.mpaz_pro_6.data.dto.docencia

import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ActividadResumen
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AsignaturaResumen
import com.ccstudio.mpaz_pro_6.data.dto.identidad.CursoResumen
import com.ccstudio.mpaz_pro_6.data.dto.identidad.UsuarioResumen
import kotlinx.serialization.Serializable

@Serializable
data class AsignaturaCursoRequest(
    val cursoId: Long,
    val asignaturaId: Long,
    val docenteId: Long? = null
)

@Serializable
data class AsignaturaCursoResponse(
    val id: Long,
    val curso: CursoResumen,
    val asignatura: AsignaturaResumen,
    val docente: UsuarioResumen? = null
)

@Serializable
data class UsuarioCursoRequest(
    val usuarioId: Long,
    val cursoId: Long
)

@Serializable
data class UsuarioCursoResponse(
    val id: Long,
    val usuario: UsuarioResumen,
    val curso: CursoResumen
)

@Serializable
data class ActividadCursoResumen(
    val id: Long,
    val actividad: ActividadResumen? = null,
    val curso: CursoResumen? = null,
    val fechaInicio: String? = null,
    val fechaFin: String? = null,
    val habilitada: Boolean = false,
    val visible: Boolean = false,
    val maxIntentos: Int? = null
)

@Serializable
data class ActividadCursoRequest(
    val actividadId: Long,
    val cursoId: Long,
    val fechaInicio: String? = null,
    val fechaFin: String? = null,
    val habilitada: Boolean = false,
    val visible: Boolean = false,
    val maxIntentos: Int? = null
)

@Serializable
data class ActividadCursoUpdateRequest(
    val fechaInicio: String? = null,
    val fechaFin: String? = null,
    val maxIntentos: Int? = null
)

@Serializable
data class ActividadCursoResponse(
    val id: Long,
    val actividad: ActividadResumen? = null,
    val curso: CursoResumen? = null,
    val fechaInicio: String? = null,
    val fechaFin: String? = null,
    val habilitada: Boolean = false,
    val visible: Boolean = false,
    val maxIntentos: Int? = null
)
