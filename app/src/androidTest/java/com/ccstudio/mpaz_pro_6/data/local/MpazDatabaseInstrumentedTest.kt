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
import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.enums.ReportStatus
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.local.entity.ActivityEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentObjectiveEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.ContentEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.CourseEntity
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
            exec("INSERT INTO asignacion VALUES (2, 1, 1, 1, 1, 0, ${epoch()})")
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
            exec(
                "INSERT INTO actividad VALUES " +
                    "(2, 1, NULL, 'Otra prueba final', 'PRUEBA_FINAL', 2)"
            )
        }
        exec("INSERT INTO actividad VALUES (3, 1, NULL, 'Actividad normal', 'ACTIVIDAD', 3)")
    }

    @Test
    fun actividadTransversalSeGuardaSinContenido() = runBlocking {
        crearBaseConContenido()
        database.curriculumDao().upsertActivity(
            ActivityEntity(1, 1, null, "Transversal", ActivityType.ACTIVITY, 1)
        )

        assertNull(database.curriculumDao().getActivityById(1)?.contenidoId)
    }

    @Test
    fun actividadConContenidoDeLaMismaUnidadSeGuarda() = runBlocking {
        crearBaseConContenido()
        database.curriculumDao().upsertActivity(
            ActivityEntity(1, 1, 1, "Con contenido", ActivityType.ACTIVITY, 1)
        )

        assertEquals(1, database.curriculumDao().getActivityById(1)?.contenidoId)
    }

    @Test
    fun actividadConContenidoDeOtraUnidadViolaLaForeignKeyCompuesta() = runBlocking {
        crearBaseConContenido()
        crearUnidad(id = 2, titulo = "Otra unidad")

        assertThrows(SQLiteConstraintException::class.java) {
            exec(
                "INSERT INTO actividad VALUES " +
                    "(1, 2, 1, 'Contenido ajeno', 'ACTIVIDAD', 1)"
            )
        }
        Unit
    }

    @Test
    fun contenidoConUnidadDistintaAlObjetivoViolaLaForeignKeyCompuesta() = runBlocking {
        crearUnidad()
        database.curriculumDao().upsertObjective(
            LearningObjectiveEntity(1, 1, "OA1", "Objetivo", "Números", null)
        )

        assertThrows(SQLiteConstraintException::class.java) {
            exec(
                """
                INSERT INTO contenido (
                    id, oa_id, unidad_id, titulo, explicacion, ejemplos,
                    instrucciones, orden, origen, estado
                ) VALUES (
                    1, 1, 2, 'Contenido ajeno', 'Explicación', NULL, NULL,
                    1, 'PROPIO', 'BORRADOR'
                )
                """.trimIndent()
            )
        }
        Unit
    }

    @Test
    fun contenidoConActividadesNoSePuedeBorrar() = runBlocking {
        crearBaseConContenido()
        database.curriculumDao().upsertActivity(
            ActivityEntity(1, 1, 1, "Con contenido", ActivityType.ACTIVITY, 1)
        )

        assertThrows(SQLiteConstraintException::class.java) {
            exec("DELETE FROM contenido WHERE id = 1")
        }
        Unit
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
            assertTrue(seeded.unitDao().getUnits().isNotEmpty())
            assertTrue(seeded.curriculumDao().getActivities().isNotEmpty())
            assertNotNull(seeded.unitDao().getUnitById(1))

            // Todo contenido deriva su unidad de un OA existente.
            val contents = seeded.curriculumDao().getContents()
            assertTrue(contents.isNotEmpty())
            contents.forEach { content ->
                val objective = seeded.curriculumDao().getObjectiveById(content.oaId)
                assertNotNull(objective)
                assertEquals(objective?.unidadId, content.unidadId)
            }

            // Las actividades asociadas apuntan a un contenido de su misma unidad.
            val contentsById = contents.associateBy { it.id }
            val activities = seeded.curriculumDao().getActivities()
            activities.filter { it.contenidoId != null }.forEach { activity ->
                val content = contentsById[activity.contenidoId]
                assertNotNull(content)
                assertEquals(activity.unidadId, content?.unidadId)
            }
            assertTrue(
                activities.any {
                    it.tipo == ActivityType.FINAL_TEST && it.contenidoId == null
                }
            )
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

    private suspend fun crearUnidad(id: Int = 1, titulo: String = "Fracciones") {
        if (database.catalogDao().getSubjectById(1) == null) {
            database.catalogDao().upsertSubject(SubjectEntity(1, "Matemática"))
        }
        database.unitDao().upsertUnit(
            LearningUnitEntity(
                id = id,
                asignaturaId = 1,
                nivelEducativo = "Sexto Basico",
                titulo = titulo,
                orden = id,
                estado = ContentStatus.DRAFT,
                autorizadaPorId = null,
                fechaAutorizacion = null
            )
        )
    }

    /** Crea una unidad con un OA y un contenido para probar la relación. */
    private suspend fun crearBaseConContenido() {
        crearUnidad()
        database.curriculumDao().upsertObjective(
            LearningObjectiveEntity(1, 1, "OA1", "Objetivo", "Números", null)
        )
        database.curriculumDao().upsertContent(
            ContentEntity(
                id = 1,
                oaId = 1,
                unidadId = 1,
                titulo = "Contenido",
                explicacion = "Explicación",
                ejemplos = null,
                instrucciones = null,
                orden = 1,
                origen = ContentOrigin.OWN,
                estado = ContentStatus.DRAFT
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
                docenteId = 1,
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
        docenteId = 1,
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
