package com.ccstudio.mpaz_pro_6.data.dto.informe

import com.ccstudio.mpaz_pro_6.data.dto.curriculo.ObjetivoAprendizajeResumen
import com.ccstudio.mpaz_pro_6.data.dto.curriculo.UnidadResumen
import com.ccstudio.mpaz_pro_6.data.dto.identidad.UsuarioResumen
import kotlinx.serialization.Serializable

@Serializable
data class InformePedagogicoResumen(
    val id: Long,
    val fecha: String,
    val fechaCorte: String = "",
    val origen: String = "MANUAL"
)

@Serializable
data class InformePedagogicoRequest(
    val usuarioId: Long,
    val unidadId: Long,
    val fecha: String,
    val descripcion: String? = null,
    val fechaCorte: String = "",
    val origen: String = "MANUAL"
)

@Serializable
data class InformePedagogicoUpdateRequest(
    val fecha: String? = null,
    val descripcion: String? = null
)

@Serializable
data class InformePedagogicoResponse(
    val id: Long,
    val usuario: UsuarioResumen,
    val unidad: UnidadResumen,
    val fecha: String,
    val descripcion: String? = null,
    val fechaCorte: String = "",
    val origen: String = "MANUAL"
)

@Serializable
data class DetalleInformePedagogicoRequest(
    val informePedagogicoId: Long,
    val objetivoAprendizajeId: Long,
    val correctas: Int = 0,
    val incorrectas: Int = 0
)

@Serializable
data class DetalleInformePedagogicoUpdateRequest(
    val correctas: Int? = null,
    val incorrectas: Int? = null
)

@Serializable
data class DetalleInformePedagogicoResponse(
    val id: Long,
    val informePedagogico: InformePedagogicoResumen,
    val objetivoAprendizaje: ObjetivoAprendizajeResumen,
    val correctas: Int,
    val incorrectas: Int
)

@Serializable
data class GenerarInformeRequest(
    val usuarioId: Long,
    val unidadId: Long,
    val fecha: String,
    val descripcion: String? = null,
    val fechaCorte: String
)

@Serializable
data class ActividadEvaluadaResumen(
    val actividadCursoId: Long,
    val actividadId: Long,
    val instrucciones: String,
    val intentoId: Long,
    val fechaFin: String
)

@Serializable
data class DetallePreliminarResponse(
    val objetivoAprendizaje: ObjetivoAprendizajeResumen,
    val correctas: Int,
    val incorrectas: Int,
    val total: Int,
    val porcentaje: Double
)

@Serializable
data class InformePreliminarResponse(
    val usuario: UsuarioResumen,
    val unidad: UnidadResumen,
    val fechaCorte: String,
    val actividades: List<ActividadEvaluadaResumen>,
    val detalles: List<DetallePreliminarResponse>,
    val totalCorrectas: Int,
    val totalIncorrectas: Int
)

@Serializable
data class InformePedagogicoDetalladoResponse(
    val informe: InformePedagogicoResponse,
    val detalles: List<DetalleInformePedagogicoResponse>
)
