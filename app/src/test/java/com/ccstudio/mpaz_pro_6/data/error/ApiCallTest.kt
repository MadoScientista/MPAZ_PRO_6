package com.ccstudio.mpaz_pro_6.data.error

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/** Pruebas de traducción de errores HTTP y de red. */
class ApiCallTest {

    @Test
    fun badRequestUsaMensajeDelBackend() {
        val error = httpError(400, """{"message":"El título es obligatorio"}""").toApiError()

        assertTrue(error is ApiError.BadRequest)
        assertEquals("El título es obligatorio", (error as ApiError.BadRequest).message)
    }

    @Test
    fun unauthorizedTieneMensajePorDefecto() {
        val error = httpError(401, "").toApiError()

        assertTrue(error is ApiError.Unauthorized)
        assertEquals(
            "La sesión expiró. Inicia sesión nuevamente.",
            (error as ApiError.Unauthorized).message
        )
    }

    @Test
    fun forbiddenSeTrataComoNoAutorizado() {
        val error = httpError(403, """{"error":"Sin permiso"}""").toApiError()

        assertTrue(error is ApiError.Unauthorized)
        assertEquals("Sin permiso", (error as ApiError.Unauthorized).message)
    }

    @Test
    fun notFoundYConflictConservanElMensaje() {
        val notFound = httpError(404, """{"message":"No existe"}""").toApiError()
        val conflict = httpError(409, """{"message":"Duplicado"}""").toApiError()

        assertTrue(notFound is ApiError.NotFound)
        assertTrue(conflict is ApiError.Conflict)
        assertEquals("No existe", (notFound as ApiError.NotFound).message)
        assertEquals("Duplicado", (conflict as ApiError.Conflict).message)
    }

    @Test
    fun errorDelServidorSeMapeaADesconocido() {
        val error = httpError(500, "").toApiError()

        assertTrue(error is ApiError.Unknown)
        assertEquals("Error del servidor (500).", (error as ApiError.Unknown).message)
    }

    @Test
    fun excepcionDeRedSeMapeaANetwork() {
        val error = IOException("timeout").toApiError()

        assertTrue(error is ApiError.Network)
    }

    @Test
    fun apiCallPropagaCancelacion() {
        assertThrows(CancellationException::class.java) {
            runBlocking {
                apiCall<Unit> { throw CancellationException("cancelado") }
            }
        }
    }

    private fun httpError(code: Int, body: String): HttpException =
        HttpException(
            Response.error<Unit>(
                code,
                body.toResponseBody("application/json".toMediaType())
            )
        )
}
