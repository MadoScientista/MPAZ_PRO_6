package com.ccstudio.mpaz_pro_6.data.remote.dto

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Serialización de la relación actividad ↔ contenido.
 *
 * La app usa `explicitNulls = false`: un campo nulo no viaja en el JSON. Por eso
 * `contenidoId = null` significa "no cambiar" y la desasociación explícita viaja
 * con `desasociarContenido = true`.
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
