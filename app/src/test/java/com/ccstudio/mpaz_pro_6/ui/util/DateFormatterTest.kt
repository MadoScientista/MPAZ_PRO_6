package com.ccstudio.mpaz_pro_6.ui.util

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class DateFormatterTest {

    @Test
    fun formateaUnInstantIsoDelBackend() {
        val resultado = formatFecha("2026-11-14T10:15:30Z")

        assertTrue(resultado.contains("/"))
        assertTrue(resultado.contains(":"))
    }

    @Test
    fun parseaUnInstantIsoDelBackend() {
        assertNotNull(parseFechaHora("2026-11-14T10:15:30Z"))
        assertNotNull(parseFechaHora("2026-11-14T10:15:30.123456Z"))
    }

    @Test
    fun elFormatoDeEntradaDelBackendEsIso() {
        val instante = Instant.parse("2026-11-14T10:15:30Z")

        assertTrue(formatInstantInput(instante).endsWith("Z"))
    }
}
