package com.ccstudio.mpaz_pro_6.data.remote.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas de serialización de la relación actividad ↔ contenido.
 *
 * Usa la misma configuración que `ApiClient`: `null` no se serializa, así que
 * `contenidoId = null` significa "no cambiar" y la desasociación viaja con
 * `desasociarContenido = true`.
 */
class ActivityRelationSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }

    @Test
    fun actualizacionSinCambiosNoIncluyeCamposDeContenido() {
        val body = json.encodeToString(UpdateActivityRequestDto())

        assertFalse(body.contains("contenidoId"))
        assertFalse(body.contains("desasociarContenido"))
    }

    @Test
    fun desasociarContenidoViajaExplicitamente() {
        val body = json.encodeToString(
            UpdateActivityRequestDto(desasociarContenido = true)
        )

        assertTrue(body.contains("\"desasociarContenido\":true"))
        assertFalse(body.contains("contenidoId"))
    }

    @Test
    fun crearActividadIncluyeElContenidoElegido() {
        val body = json.encodeToString(
            CreateActivityRequestDto(
                unidadId = 1,
                titulo = "Actividad",
                tipo = "ACTIVIDAD",
                contenidoId = 3
            )
        )

        assertTrue(body.contains("\"contenidoId\":3"))
    }

    @Test
    fun actualizarContenidoNoEnviaDesasociacion() {
        val body = json.encodeToString(
            UpdateActivityRequestDto(contenidoId = 3)
        )

        assertTrue(body.contains("\"contenidoId\":3"))
        assertEquals(false, body.contains("desasociarContenido"))
    }
}
