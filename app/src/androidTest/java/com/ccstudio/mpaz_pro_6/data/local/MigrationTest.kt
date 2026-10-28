package com.ccstudio.mpaz_pro_6.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba instrumentada de la migración 3 → 4.
 *
 * Verifica que las actividades existentes conserven su unidad y queden
 * transversales al agregar `actividad.contenido_id`.
 */
@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val databaseName = "migration-test"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MpazDatabase::class.java
    )

    @Test
    fun migracion3a4AgregaContenidoSinPerderActividades() {
        helper.createDatabase(databaseName, 3).use { db ->
            db.execSQL("INSERT INTO asignatura (id, nombre) VALUES (1, 'Matemática')")
            db.execSQL(
                """
                INSERT INTO unidad (
                    id, asignatura_id, nivel_educativo, titulo, orden, estado,
                    autorizada_por_id, fecha_autorizacion
                ) VALUES (1, 1, 'Sexto Basico', 'Fracciones', 1, 'BORRADOR', NULL, NULL)
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO actividad (id, unidad_id, titulo, tipo, orden)
                VALUES (1, 1, 'Actividad', 'ACTIVIDAD', 1)
                """.trimIndent()
            )
            db.close()
        }

        helper.runMigrationsAndValidate(databaseName, 4, true, MIGRATION_3_4).use { db ->
            db.query("SELECT contenido_id FROM actividad WHERE id = 1").use { cursor ->
                assertEquals(true, cursor.moveToFirst())
                assertNull(if (cursor.isNull(0)) null else cursor.getInt(0))
            }
        }
    }
}
