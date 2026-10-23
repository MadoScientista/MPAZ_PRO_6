package com.ccstudio.mpaz_pro_6.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

/** Pruebas del separador de sentencias de la semilla SQL. */
class SeedLoaderTest {

    @Test
    fun conservaPuntoYComaDentroDeUnaCadena() {
        val script = "INSERT INTO contenido (ejemplos) VALUES ('permeable; uno descuidado');"

        assertEquals(
            listOf("INSERT INTO contenido (ejemplos) VALUES ('permeable; uno descuidado')"),
            SeedLoader.splitStatements(script)
        )
    }

    @Test
    fun respetaComillasEscapadas() {
        val script = "INSERT INTO tabla (texto) VALUES ('no es ''un; error''');"

        assertEquals(
            listOf("INSERT INTO tabla (texto) VALUES ('no es ''un; error''')"),
            SeedLoader.splitStatements(script)
        )
    }

    @Test
    fun descartaComentariosFueraDeLasCadenas() {
        val script = """
            -- comentario; con punto y coma
            SELECT '-- esto no es comentario'; -- otro comentario
            /* bloque; con punto y coma */
            SELECT 2;
        """.trimIndent()

        assertEquals(
            listOf("SELECT '-- esto no es comentario'", "SELECT 2"),
            SeedLoader.splitStatements(script)
        )
    }

    @Test
    fun separaVariasSentencias() {
        val script = "UPDATE contenido SET estado = 'AUTORIZADO';\nUPDATE recurso_contenido SET estado = 'AUTORIZADO';"

        assertEquals(
            listOf(
                "UPDATE contenido SET estado = 'AUTORIZADO'",
                "UPDATE recurso_contenido SET estado = 'AUTORIZADO'"
            ),
            SeedLoader.splitStatements(script)
        )
    }
}
