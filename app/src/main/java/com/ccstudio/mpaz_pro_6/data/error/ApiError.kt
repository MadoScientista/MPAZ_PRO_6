package com.ccstudio.mpaz_pro_6.data.error

/**
 * Errores que puede devolver el backend.
 *
 * Mapea los códigos HTTP documentados en `specs/endpoints`: `400 Bad Request` a
 * `BadRequest`, `404 Not Found` a `NotFound` y `409 Conflict` a `Conflict`.
 *
 * `Conflict` es el caso más frecuente en la UI: ocurre al eliminar una unidad, un
 * objetivo o una actividad que todavía tienen elementos asociados, y al duplicar
 * nombres o códigos. El mensaje permite que la pantalla explique el motivo del
 * rechazo y no un error genérico.
 *
 * `Network` y `Unknown` cubren lo que no es una respuesta HTTP (sin conexión,
 * tiempo de espera agotado) y quedan reservados para la implementación de
 * `data/remote/`.
 */
sealed class ApiError(override val message: String) : Exception(message) {

    /** `400 Bad Request`: validación o regla de negocio violada. */
    data class BadRequest(override val message: String) : ApiError(message)

    /** `404 Not Found`: la referencia indicada no existe. */
    data class NotFound(override val message: String) : ApiError(message)

    /** `401 Unauthorized`: credenciales inválidas o token vencido. */
    data class Unauthorized(override val message: String) : ApiError(message)

    /** `409 Conflict`: duplicado o dependencia que impide la operación. */
    data class Conflict(override val message: String) : ApiError(message)

    /** Fallo de red: no hubo respuesta del backend. */
    data class Network(override val message: String) : ApiError(message)

    /** Fallo no clasificado. */
    data class Unknown(override val message: String) : ApiError(message)
}
