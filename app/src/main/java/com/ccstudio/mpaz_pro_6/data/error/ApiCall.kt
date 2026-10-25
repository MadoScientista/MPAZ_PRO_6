package com.ccstudio.mpaz_pro_6.data.error

import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/** Ejecuta una llamada remota y convierte sus fallos en [ApiError]. */
suspend fun <T> apiCall(block: suspend () -> T): Result<T> =
    try {
        Result.success(block())
    } catch (error: CancellationException) {
        throw error
    } catch (error: Throwable) {
        Result.failure(error.toApiError())
    }

/** Exige que una respuesta sin cuerpo haya sido exitosa. */
fun Response<Unit>.requireSuccess() {
    if (!isSuccessful) {
        throw HttpException(this)
    }
}

/** Traduce una excepción de red o HTTP al error de dominio. */
fun Throwable.toApiError(): ApiError = when (this) {
    is ApiError -> this
    is HttpException -> when (code()) {
        400 -> ApiError.BadRequest(serverMessage())
        401 -> ApiError.Unauthorized(serverMessage("La sesión expiró. Inicia sesión nuevamente."))
        403 -> ApiError.Unauthorized(serverMessage("No tienes permiso para realizar esta acción."))
        404 -> ApiError.NotFound(serverMessage())
        409 -> ApiError.Conflict(serverMessage())
        else -> ApiError.Unknown(serverMessage("Error del servidor (${code()})."))
    }

    is IOException -> ApiError.Network("No se pudo conectar con el servidor.")
    else -> ApiError.Unknown(message ?: "Ocurrió un error inesperado.")
}

private fun HttpException.serverMessage(fallback: String = "La operación no se pudo completar."): String {
    val body = response()?.errorBody()?.string()?.takeIf(String::isNotBlank) ?: return fallback
    val parsed = runCatching {
        val objectBody = Json.parseToJsonElement(body).jsonObject
        objectBody["message"]?.jsonPrimitive?.content
            ?: objectBody["error"]?.jsonPrimitive?.content
    }.getOrNull()

    return parsed?.takeIf(String::isNotBlank) ?: fallback
}
