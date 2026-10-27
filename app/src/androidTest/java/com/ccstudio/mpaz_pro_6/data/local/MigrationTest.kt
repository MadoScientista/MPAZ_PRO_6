package com.ccstudio.mpaz_pro_6.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas instrumentadas de la migración 1 → 2.
 *
 * Crea una base con el esquema exportado de la versión 1, inserta filas a mano
 * (la semilla actual ya usa el esquema 2 y no sirve aquí) y valida que la
 * migración derive `contenido.unidad_id`, deje `actividad.contenido_id` nulo y
 * conserve las tablas hijas.
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
    fun migracion1a2DerivaLaUnidadYConservaLasTablasHijas() {
        helper.createDatabase(databaseName, 1).use { db ->
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
                INSERT INTO objetivo_aprendizaje (
                    id, unidad_id, codigo, descripcion, eje, texto_referencia
                ) VALUES (1, 1, 'OA1', 'Objetivo', 'Números', NULL)
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO contenido (
                    id, oa_id, titulo, explicacion, ejemplos, instrucciones,
                    orden, origen, estado
                ) VALUES (
                    1, 1, 'Contenido', 'Explicación', NULL, NULL,
                    1, 'PROPIO', 'BORRADOR'
                )
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO actividad (id, unidad_id, titulo, tipo, orden)
                VALUES (1, 1, 'Actividad', 'ACTIVIDAD', 1)
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO recurso_contenido (
                    id, contenido_id, tipo, url, descripcion, origen, estado
                ) VALUES (1, 1, 'IMAGEN', 'https://ejemplo.cl/a.png', NULL, 'PROPIO', 'BORRADOR')
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO pregunta (
                    id, actividad_id, unidad_id, oa_id, enunciado,
                    criterio_respuesta, dificultad, orden
                ) VALUES (1, 1, 1, 1, '¿Cuánto es 1/2?', NULL, 'BAJA', 1)
                """.trimIndent()
            )
            db.execSQL(
                """
                INSERT INTO alternativa (id, pregunta_id, texto, es_correcta)
                VALUES (1, 1, 'La mitad', 1)
                """.trimIndent()
            )
            db.close()
        }

        helper.runMigrationsAndValidate(databaseName, 2, true, MIGRATION_1_2).use { db ->
            db.query("SELECT oa_id, unidad_id FROM contenido WHERE id = 1").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(1, cursor.getInt(0))
                assertEquals(1, cursor.getInt(1))
            }
            db.query("SELECT contenido_id FROM actividad WHERE id = 1").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertNull(if (cursor.isNull(0)) null else cursor.getInt(0))
            }
            assertEquals(1, count(db, "recurso_contenido"))
            assertEquals(1, count(db, "pregunta"))
            assertEquals(1, count(db, "alternativa"))
            assertEquals(0, count(db, "intento"))
            assertEquals(0, count(db, "asignacion_actividad"))
            assertEquals(0, count(db, "respuesta_estudiante"))
        }
    }

    private fun count(db: androidx.sqlite.db.SupportSQLiteDatabase, table: String): Int =
        db.query("SELECT COUNT(*) FROM $table").use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
}
