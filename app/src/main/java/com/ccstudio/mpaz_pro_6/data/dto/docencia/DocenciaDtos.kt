package com.ccstudio.mpaz_pro_6.data.dto.docencia

import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AsignaturaResumen
import com.ccstudio.mpaz_pro_6.data.dto.identidad.CursoResumen
import com.ccstudio.mpaz_pro_6.data.dto.identidad.UsuarioResumen
import kotlinx.serialization.Serializable

@Serializable
data class AsignaturaCursoRequest(
    val cursoId: Long,
    val asignaturaId: Long
)

@Serializable
data class AsignaturaCursoResponse(
    val id: Long,
    val curso: CursoResumen,
    val asignatura: AsignaturaResumen
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
