package com.ccstudio.mpaz_pro_6.data.local

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.enums.ReportStatus
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.local.entity.ActivityEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentObjectiveEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.CourseEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.DocenciaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.LearningObjectiveEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.LearningUnitEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.PedagogicalReportEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.SubjectEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.UserEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant

/** Pruebas instrumentadas de integridad de Room y carga de la semilla. */
@RunWith(AndroidJUnit4::class)
class MpazDatabaseInstrumentedTest {

    private lateinit var context: Context
    private lateinit var database: MpazDatabase

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, MpazDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun unidadConAsignaturaInexistenteViolaLaForeignKey() {
        val sql = """
            INSERT INTO unidad (
                id, asignatura_id, nivel_educativo, titulo, orden, estado,
                autorizada_por_id, fecha_autorizacion
            ) VALUES (1, 99, 'Sexto Basico', 'Unidad sin asignatura', 1, 'BORRADOR', NULL, NULL)
        """.trimIndent()

        assertThrows(SQLiteConstraintException::class.java) { exec(sql) }
    }

    @Test
    fun cursoDuplicadoPorNombreYAnioFalla() = runBlocking {
        database.catalogDao().upsertCourse(CourseEntity(1, "Sexto A", 2026))

        assertThrows(SQLiteConstraintException::class.java) {
            exec("INSERT INTO curso (id, nombre, anio) VALUES (2, 'Sexto A', 2026)")
        }
        Unit
    }

    @Test
    fun asignacionDuplicadaPorCursoYUnidadFalla() = runBlocking {
        crearBaseParaAsignacion()

        assertThrows(SQLiteConstraintException::class.java) {
            exec("INSERT INTO asignacion VALUES (2, 1, 1, 1, 0, ${epoch()})")
        }
        Unit
    }

    @Test
    fun docenciaDuplicadaPorCursoYAsignaturaFalla() = runBlocking {
        crearBaseParaAsignacion()
        database.docenciaDao().upsertDocencia(
            DocenciaEntity(
                id = 1,
                docenteId = 1,
                cursoId = 1,
                asignaturaId = 1
            )
        )

        assertEquals(1, database.userDao().getCoursesForTeacher(1).size)

        assertThrows(SQLiteConstraintException::class.java) {
            exec("INSERT INTO docencia (id, docente_id, curso_id, asignatura_id) VALUES (2, 1, 1, 1)")
        }
        Unit
    }

    @Test
    fun informeDuplicadoPorEstudianteYUnidadFalla() = runBlocking {
        crearBaseParaAsignacion()
        database.reportDao().upsertReport(informe(id = 1))

        assertThrows(SQLiteConstraintException::class.java) {
            exec(
                """
                INSERT INTO informe_pedagogico (
                    id, estudiante_id, unidad_id, docente_id,
                    fecha_generacion, estado, fecha_validacion
                ) VALUES (2, 2, 1, 1, ${epoch()}, 'GENERADO', NULL)
                """.trimIndent()
            )
        }
        Unit
    }

    @Test
    fun actividadConTituloDuplicadoEnLaMismaUnidadFalla() = runBlocking {
        crearUnidad()
        database.curriculumDao().upsertActivity(
            ActivityEntity(1, 1, null, "Prueba", ActivityType.ACTIVITY, 1)
        )

        assertThrows(SQLiteConstraintException::class.java) {
            exec("INSERT INTO actividad VALUES (2, 1, NULL, 'Prueba', 'DESAFIO', 2)")
        }
        Unit
    }

    @Test
    fun indiceParcialPermiteUnaSolaPruebaFinalPorUnidad() {
        runBlocking { crearUnidad() }
        SeedLoader.createIndexes(database.openHelper.writableDatabase)
        exec("INSERT INTO actividad VALUES (1, 1, NULL, 'Prueba final', 'PRUEBA_FINAL', 1)")

        assertThrows(SQLiteConstraintException::class.java) {
            exec("INSERT INTO actividad VALUES (2, 1, NULL, 'Otra prueba final', 'PRUEBA_FINAL', 2)")
        }
        exec("INSERT INTO actividad VALUES (3, 1, NULL, 'Actividad normal', 'ACTIVIDAD', 3)")
    }

    @Test
    fun transaccionFallidaConservaLosDatosAnteriores() = runBlocking {
        val dao = database.catalogDao()
        dao.upsertCourse(CourseEntity(1, "Temporal", 2026))

        runCatching {
            database.withTransaction {
                dao.deleteAllCourses()
                dao.upsertCourse(CourseEntity(2, "Nuevo", 2026))
                error("Fallo simulado")
            }
        }

        assertEquals("Temporal", dao.getCourseById(1)?.nombre)
        assertNull(dao.getCourseById(2))
    }

    @Test
    fun semillaSeCargaSobreUnaBaseNueva() = runBlocking {
        val seeded = Room.inMemoryDatabaseBuilder(context, MpazDatabase::class.java)
            .allowMainThreadQueries()
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        SeedLoader.load(context, db)
                    }
                }
            )
            .build()

        try {
            assertTrue(seeded.catalogDao().getCourses().isNotEmpty())
            assertTrue(seeded.catalogDao().getSubjects().isNotEmpty())
            assertTrue(seeded.userDao().getUsers().isNotEmpty())
            assertTrue(seeded.docenciaDao().getDocencias().isNotEmpty())
            assertTrue(seeded.unitDao().getUnits().isNotEmpty())
            assertTrue(seeded.curriculumDao().getActivities().isNotEmpty())
            assertNotNull(seeded.unitDao().getUnitById(1))
            assertEquals(4, seeded.unitDao().getUnits().size)
            assertEquals(36, seeded.curriculumDao().getObjectives().size)
            assertEquals(9, seeded.curriculumDao().getObjectivesByUnit(12).size)
            assertEquals(3, seeded.assignmentDao().getAssignments().size)
        } finally {
            seeded.close()
        }
    }

    @Test
    fun claveCompuestaDeAsociacionSeValidaEnSQL() = runBlocking {
        crearBaseParaAsignacion()
        database.assignmentDao().upsertAssignment(asignacion(id = 1))
        database.curriculumDao().upsertObjective(
            LearningObjectiveEntity(1, 1, "OA1", "Objetivo", "Eje", null)
        )
        val assoc = AssignmentObjectiveEntity(asignacionId = 1, unidadId = 1, oaId = 1)
        database.assignmentDao().upsertAssignmentObjective(assoc)

        val insert = """
            INSERT INTO asignacion_oa (asignacion_id, unidad_id, oa_id)
            VALUES (1, 1, 1)
        """.trimIndent()
        assertThrows(SQLiteConstraintException::class.java) {
            database.openHelper.writableDatabase.execSQL(insert)
        }
        Unit
    }

    private suspend fun crearUnidad() {
        database.catalogDao().upsertSubject(SubjectEntity(1, "Matemática"))
        database.unitDao().upsertUnit(
            LearningUnitEntity(
                id = 1,
                asignaturaId = 1,
                nivelEducativo = "Sexto Basico",
                titulo = "Fracciones",
                orden = 1,
                estado = ContentStatus.DRAFT,
                autorizadaPorId = null,
                fechaAutorizacion = null
            )
        )
    }

    private suspend fun crearBaseParaAsignacion() {
        crearUnidad()
        database.catalogDao().upsertCourse(CourseEntity(1, "Sexto A", 2026))
        database.userDao().upsertUser(
            UserEntity(1, "Docente", "docente", UserRole.DOCENTE, null)
        )
        database.userDao().upsertUser(
            UserEntity(2, "Estudiante", "estudiante", UserRole.ESTUDIANTE, 1)
        )
        database.assignmentDao().upsertAssignment(
            AssignmentEntity(
                id = 1,
                cursoId = 1,
                unidadId = 1,
                maxIntentos = 1,
                pruebaFinalHabilitada = false,
                fecha = Instant.parse("2026-04-06T08:00:00Z")
            )
        )
    }

    private fun exec(sql: String) {
        database.openHelper.writableDatabase.execSQL(sql)
    }

    private fun epoch(): Long = Instant.parse("2026-04-06T08:00:00Z").toEpochMilli()

    private fun asignacion(id: Int) = AssignmentEntity(
        id = id,
        cursoId = 1,
        unidadId = 1,
        maxIntentos = 1,
        pruebaFinalHabilitada = false,
        fecha = Instant.parse("2026-04-06T08:00:00Z")
    )

    private fun informe(id: Int) = PedagogicalReportEntity(
        id = id,
        estudianteId = 2,
        unidadId = 1,
        docenteId = 1,
        fechaGeneracion = Instant.parse("2026-04-06T08:00:00Z"),
        estado = ReportStatus.GENERATED,
        fechaValidacion = null
    )
}
