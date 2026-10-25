package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.ApiError
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pruebas de la política de respaldo con caché. */
class CacheFallbackTest {

    @Test
    fun errorDeRedDevuelveCache() = runBlocking {
        val result = cachedValueOrError(ApiError.Network("sin red")) { "cache" }

        assertEquals("cache", result.getOrThrow())
    }

    @Test
    fun errorDeServidorDesconocidoDevuelveCache() = runBlocking {
        val result = cachedValueOrError(ApiError.Unknown("500")) { "cache" }

        assertEquals("cache", result.getOrThrow())
    }

    @Test
    fun errorDeValidacionNoUsaCache() = runBlocking {
        val error = ApiError.BadRequest("dato inválido")

        val result = cachedValueOrError(error) { "cache" }

        assertTrue(result.exceptionOrNull() === error)
    }

    @Test
    fun sinCachePropagaElErrorOriginal() = runBlocking {
        val error = ApiError.Network("sin red")

        val result = cachedValueOrError<String>(error) { null }

        assertTrue(result.exceptionOrNull() === error)
    }
}
