package com.ccstudio.mpaz_pro_6.data.remote

import com.ccstudio.mpaz_pro_6.data.dto.evaluacion.IntentoRequest
import com.ccstudio.mpaz_pro_6.data.dto.evaluacion.IntentoResponse
import com.ccstudio.mpaz_pro_6.data.dto.evaluacion.RespuestaRequest
import com.ccstudio.mpaz_pro_6.data.dto.evaluacion.RespuestaResponse

/**
 * Escrituras del flujo de evaluacion del estudiante.
 */
class RemoteEvaluationRepository(private val api: MpazApi) {

    suspend fun iniciarIntento(
        usuarioId: Long,
        actividadCursoId: Long
    ): IntentoResponse = api.crearIntento(
        IntentoRequest(
            usuarioId = usuarioId,
            actividadCursoId = actividadCursoId,
            fechaInicio = java.time.Instant.now().toString(),
            fechaFin = null
        )
    )

    suspend fun cerrarIntento(intentoId: Long): IntentoResponse =
        api.cerrarIntento(intentoId)

    suspend fun responder(
        intentoId: Long,
        preguntaId: Long,
        alternativaId: Long
    ): RespuestaResponse = api.crearRespuesta(
        RespuestaRequest(
            intentoId = intentoId,
            preguntaId = preguntaId,
            alternativaId = alternativaId
        )
    )
}
