package com.ccstudio.mpaz_pro_6.data.dto.evaluacion

import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ActividadResumen
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.AlternativaResumen
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.PreguntaResumen
import com.ccstudio.mpaz_pro_6.data.dto.identidad.UsuarioResumen
import kotlinx.serialization.Serializable

@Serializable
data class IntentoResumen(
    val id: Long,
    val fechaInicio: String,
    val fechaFin: String? = null
)

@Serializable
data class IntentoRequest(
    val usuarioId: Long,
    val actividadId: Long,
    val fechaInicio: String? = null,
    val fechaFin: String? = null
)

@Serializable
data class IntentoResponse(
    val id: Long,
    val usuario: UsuarioResumen,
    val actividad: ActividadResumen,
    val fechaInicio: String,
    val fechaFin: String? = null
)

@Serializable
data class RespuestaRequest(
    val intentoId: Long,
    val preguntaId: Long,
    val alternativaId: Long
)

@Serializable
data class RespuestaResponse(
    val id: Long,
    val intento: IntentoResumen,
    val pregunta: PreguntaResumen,
    val alternativa: AlternativaResumen
)
