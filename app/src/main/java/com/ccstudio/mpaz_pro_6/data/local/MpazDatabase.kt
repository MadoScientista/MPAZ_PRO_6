package com.ccstudio.mpaz_pro_6.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ccstudio.mpaz_pro_6.data.local.dao.AssignmentDao
import com.ccstudio.mpaz_pro_6.data.local.dao.AttemptDao
import com.ccstudio.mpaz_pro_6.data.local.dao.CatalogDao
import com.ccstudio.mpaz_pro_6.data.local.dao.CurriculumDao
import com.ccstudio.mpaz_pro_6.data.local.dao.ReportDao
import com.ccstudio.mpaz_pro_6.data.local.dao.UnitDao
import com.ccstudio.mpaz_pro_6.data.local.dao.UserDao
import com.ccstudio.mpaz_pro_6.data.local.entity.ActivityEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AlternativeEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentActivityEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentObjectiveEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AttemptEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.ContentEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.ContentResourceEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.CourseEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.LearningObjectiveEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.LearningUnitEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.PedagogicalReportEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.QuestionEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.ReportObjectiveDetailEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.StudentAnswerEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.SubjectEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.UserEntity

/**
 * Base de datos local de MPAZ.
 *
 * Al crearse carga la semilla de demostración desde `assets/database/seed.sql`
 * y agrega el índice parcial que Room no puede declarar con anotaciones.
 */
@Database(
    entities = [
        CourseEntity::class,
        UserEntity::class,
        SubjectEntity::class,
        LearningUnitEntity::class,
        LearningObjectiveEntity::class,
        ContentEntity::class,
        ContentResourceEntity::class,
        ActivityEntity::class,
        QuestionEntity::class,
        AlternativeEntity::class,
        AssignmentEntity::class,
        AssignmentObjectiveEntity::class,
        AssignmentActivityEntity::class,
        AttemptEntity::class,
        StudentAnswerEntity::class,
        PedagogicalReportEntity::class,
        ReportObjectiveDetailEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MpazDatabase : RoomDatabase() {

    abstract fun catalogDao(): CatalogDao

    abstract fun userDao(): UserDao

    abstract fun unitDao(): UnitDao

    abstract fun curriculumDao(): CurriculumDao

    abstract fun assignmentDao(): AssignmentDao

    abstract fun attemptDao(): AttemptDao

    abstract fun reportDao(): ReportDao

    companion object {

        /** Nombre del archivo de base de datos. */
        const val NAME = "mpaz.db"

        /** Construye la instancia única de la base local. */
        fun build(context: Context): MpazDatabase =
            Room.databaseBuilder(context, MpazDatabase::class.java, NAME)
                .addMigrations(MIGRATION_1_2)
                .addCallback(SeedCallback(context.applicationContext))
                .build()
    }
}

/**
 * Migra la base local de la versión 1 a la 2.
 *
 * Agrega `contenido.unidad_id` y `actividad.contenido_id`. SQLite no permite
 * agregar una columna `NOT NULL` con FK ni una FK compuesta con `ALTER TABLE`,
 * así que se reconstruyen las dos tablas. Las tablas hijas se respaldan y se
 * vuelven a crear al final, porque `ON DELETE RESTRICT` no se puede diferir
 * dentro de la transacción exclusiva de Room.
 *
 * Las actividades existentes quedan transversales: la versión 1 no guardaba
 * ninguna asociación y la migración no inventa datos.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        backupAndDropChildren(db)
        rebuildContent(db)
        rebuildActivity(db)
        restoreChildren(db)
        dropBackups(db)
        ensureForeignKeysValid(db)
    }
}

/** Respaldar y borrar las tablas hijas de hoja a raíz. */
private fun backupAndDropChildren(db: SupportSQLiteDatabase) {
    listOf(
        "respuesta_estudiante",
        "intento",
        "asignacion_actividad",
        "alternativa",
        "pregunta",
        "recurso_contenido"
    ).forEach { table ->
        db.execSQL("CREATE TABLE __migracion_$table AS SELECT * FROM $table")
        db.execSQL("DROP TABLE $table")
    }
}

/** Reconstruir `contenido` con la unidad derivada de su objetivo de aprendizaje. */
private fun rebuildContent(db: SupportSQLiteDatabase) {
    val previousCount = db.countRows("contenido")

    db.execSQL(
        """
        CREATE TABLE contenido_nuevo (
            id INTEGER NOT NULL,
            oa_id INTEGER NOT NULL,
            unidad_id INTEGER NOT NULL,
            titulo TEXT NOT NULL,
            explicacion TEXT NOT NULL,
            ejemplos TEXT,
            instrucciones TEXT,
            orden INTEGER NOT NULL,
            origen TEXT NOT NULL,
            estado TEXT NOT NULL DEFAULT 'BORRADOR',
            PRIMARY KEY(id),
            FOREIGN KEY(oa_id) REFERENCES objetivo_aprendizaje(id)
                ON UPDATE NO ACTION ON DELETE RESTRICT,
            FOREIGN KEY(oa_id, unidad_id) REFERENCES objetivo_aprendizaje(id, unidad_id)
                ON UPDATE NO ACTION ON DELETE RESTRICT
        )
        """.trimIndent()
    )

    db.execSQL(
        """
        INSERT INTO contenido_nuevo (
            id, oa_id, unidad_id, titulo, explicacion, ejemplos,
            instrucciones, orden, origen, estado
        )
        SELECT c.id, c.oa_id, oa.unidad_id, c.titulo, c.explicacion, c.ejemplos,
               c.instrucciones, c.orden, c.origen, c.estado
        FROM contenido AS c
        JOIN objetivo_aprendizaje AS oa ON oa.id = c.oa_id
        """.trimIndent()
    )

    check(db.countRows("contenido_nuevo") == previousCount) {
        "La migración perdió contenidos: se esperaban $previousCount filas."
    }

    db.execSQL("DROP TABLE contenido")
    db.execSQL("ALTER TABLE contenido_nuevo RENAME TO contenido")
    db.execSQL("CREATE INDEX IF NOT EXISTS index_contenido_oa_id ON contenido (oa_id)")
    db.execSQL("CREATE INDEX IF NOT EXISTS index_contenido_unidad_id ON contenido (unidad_id)")
    db.execSQL(
        "CREATE UNIQUE INDEX IF NOT EXISTS index_contenido_id_unidad_id " +
            "ON contenido (id, unidad_id)"
    )
    db.execSQL(
        "CREATE INDEX IF NOT EXISTS index_contenido_oa_id_unidad_id " +
            "ON contenido (oa_id, unidad_id)"
    )
}

/** Reconstruir `actividad` con `contenido_id` nulo en todas las filas existentes. */
private fun rebuildActivity(db: SupportSQLiteDatabase) {
    db.execSQL(
        """
        CREATE TABLE actividad_nuevo (
            id INTEGER NOT NULL,
            unidad_id INTEGER NOT NULL,
            contenido_id INTEGER,
            titulo TEXT NOT NULL,
            tipo TEXT NOT NULL,
            orden INTEGER NOT NULL,
            PRIMARY KEY(id),
            FOREIGN KEY(unidad_id) REFERENCES unidad(id)
                ON UPDATE NO ACTION ON DELETE RESTRICT,
            FOREIGN KEY(contenido_id) REFERENCES contenido(id)
                ON UPDATE NO ACTION ON DELETE RESTRICT,
            FOREIGN KEY(contenido_id, unidad_id) REFERENCES contenido(id, unidad_id)
                ON UPDATE NO ACTION ON DELETE RESTRICT
        )
        """.trimIndent()
    )

    db.execSQL(
        """
        INSERT INTO actividad_nuevo (id, unidad_id, contenido_id, titulo, tipo, orden)
        SELECT id, unidad_id, NULL, titulo, tipo, orden FROM actividad
        """.trimIndent()
    )

    db.execSQL("DROP TABLE actividad")
    db.execSQL("ALTER TABLE actividad_nuevo RENAME TO actividad")
    db.execSQL("CREATE INDEX IF NOT EXISTS index_actividad_unidad_id ON actividad (unidad_id)")
    db.execSQL(
        "CREATE UNIQUE INDEX IF NOT EXISTS index_actividad_id_unidad_id " +
            "ON actividad (id, unidad_id)"
    )
    db.execSQL(
        "CREATE UNIQUE INDEX IF NOT EXISTS index_actividad_unidad_id_titulo " +
            "ON actividad (unidad_id, titulo)"
    )
    db.execSQL(
        "CREATE INDEX IF NOT EXISTS index_actividad_contenido_id ON actividad (contenido_id)"
    )
    db.execSQL(
        "CREATE INDEX IF NOT EXISTS index_actividad_contenido_id_unidad_id " +
            "ON actividad (contenido_id, unidad_id)"
    )
}

/** Volver a crear las tablas hijas con el esquema de la versión 2. */
private fun restoreChildren(db: SupportSQLiteDatabase) {
    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS recurso_contenido (
            id INTEGER NOT NULL,
            contenido_id INTEGER NOT NULL,
            tipo TEXT NOT NULL,
            url TEXT NOT NULL,
            descripcion TEXT,
            origen TEXT NOT NULL,
            estado TEXT NOT NULL DEFAULT 'BORRADOR',
            PRIMARY KEY(id),
            FOREIGN KEY(contenido_id) REFERENCES contenido(id)
                ON UPDATE NO ACTION ON DELETE RESTRICT
        )
        """.trimIndent()
    )
    db.execSQL(
        """
        INSERT INTO recurso_contenido (id, contenido_id, tipo, url, descripcion, origen, estado)
        SELECT id, contenido_id, tipo, url, descripcion, origen, estado
        FROM __migracion_recurso_contenido
        """.trimIndent()
    )
    db.execSQL(
        "CREATE INDEX IF NOT EXISTS index_recurso_contenido_contenido_id " +
            "ON recurso_contenido (contenido_id)"
    )

    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS pregunta (
            id INTEGER NOT NULL,
            actividad_id INTEGER NOT NULL,
            unidad_id INTEGER NOT NULL,
            oa_id INTEGER NOT NULL,
            enunciado TEXT NOT NULL,
            criterio_respuesta TEXT,
            dificultad TEXT NOT NULL,
            orden INTEGER NOT NULL,
            PRIMARY KEY(id),
            FOREIGN KEY(actividad_id) REFERENCES actividad(id)
                ON UPDATE NO ACTION ON DELETE RESTRICT,
            FOREIGN KEY(unidad_id) REFERENCES unidad(id)
                ON UPDATE NO ACTION ON DELETE RESTRICT,
            FOREIGN KEY(oa_id) REFERENCES objetivo_aprendizaje(id)
                ON UPDATE NO ACTION ON DELETE RESTRICT,
            FOREIGN KEY(actividad_id, unidad_id) REFERENCES actividad(id, unidad_id)
                ON UPDATE NO ACTION ON DELETE RESTRICT,
            FOREIGN KEY(oa_id, unidad_id) REFERENCES objetivo_aprendizaje(id, unidad_id)
                ON UPDATE NO ACTION ON DELETE RESTRICT
        )
        """.trimIndent()
    )
    db.execSQL(
        """
        INSERT INTO pregunta (
            id, actividad_id, unidad_id, oa_id, enunciado,
            criterio_respuesta, dificultad, orden
        )
        SELECT id, actividad_id, unidad_id, oa_id, enunciado,
               criterio_respuesta, dificultad, orden
        FROM __migracion_pregunta
        """.trimIndent()
    )
    db.execSQL("CREATE INDEX IF NOT EXISTS index_pregunta_actividad_id ON pregunta (actividad_id)")
    db.execSQL("CREATE INDEX IF NOT EXISTS index_pregunta_unidad_id ON pregunta (unidad_id)")
    db.execSQL("CREATE INDEX IF NOT EXISTS index_pregunta_oa_id ON pregunta (oa_id)")
    db.execSQL(
        "CREATE INDEX IF NOT EXISTS index_pregunta_actividad_id_unidad_id " +
            "ON pregunta (actividad_id, unidad_id)"
    )
    db.execSQL(
        "CREATE INDEX IF NOT EXISTS index_pregunta_oa_id_unidad_id " +
            "ON pregunta (oa_id, unidad_id)"
    )

    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS alternativa (
            id INTEGER NOT NULL,
            pregunta_id INTEGER NOT NULL,
            texto TEXT NOT NULL,
            es_correcta INTEGER NOT NULL,
            PRIMARY KEY(id),
            FOREIGN KEY(pregunta_id) REFERENCES pregunta(id)
                ON UPDATE NO ACTION ON DELETE RESTRICT
        )
        """.trimIndent()
    )
    db.execSQL(
        """
        INSERT INTO alternativa (id, pregunta_id, texto, es_correcta)
        SELECT id, pregunta_id, texto, es_correcta
        FROM __migracion_alternativa
        """.trimIndent()
    )
    db.execSQL(
        "CREATE INDEX IF NOT EXISTS index_alternativa_pregunta_id " +
            "ON alternativa (pregunta_id)"
    )
    db.execSQL(
        "CREATE UNIQUE INDEX IF NOT EXISTS index_alternativa_pregunta_id_id " +
            "ON alternativa (pregunta_id, id)"
    )

    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS asignacion_actividad (
            asignacion_id INTEGER NOT NULL,
            unidad_id INTEGER NOT NULL,
            actividad_id INTEGER NOT NULL,
            PRIMARY KEY(asignacion_id, actividad_id),
            FOREIGN KEY(asignacion_id) REFERENCES asignacion(id)
                ON UPDATE NO ACTION ON DELETE RESTRICT,
            FOREIGN KEY(unidad_id) REFERENCES unidad(id)
                ON UPDATE NO ACTION ON DELETE RESTRICT,
            FOREIGN KEY(actividad_id) REFERENCES actividad(id)
                ON UPDATE NO ACTION ON DELETE RESTRICT,
            FOREIGN KEY(asignacion_id, unidad_id) REFERENCES asignacion(id, unidad_id)
                ON UPDATE NO ACTION ON DELETE RESTRICT,
            FOREIGN KEY(actividad_id, unidad_id) REFERENCES actividad(id, unidad_id)
                ON UPDATE NO ACTION ON DELETE RESTRICT
        )
        """.trimIndent()
    )
    db.execSQL(
        """
        INSERT INTO asignacion_actividad (asignacion_id, unidad_id, actividad_id)
        SELECT asignacion_id, unidad_id, actividad_id
        FROM __migracion_asignacion_actividad
        """.trimIndent()
    )
    db.execSQL(
        "CREATE INDEX IF NOT EXISTS index_asignacion_actividad_unidad_id " +
            "ON asignacion_actividad (unidad_id)"
    )
    db.execSQL(
        "CREATE INDEX IF NOT EXISTS index_asignacion_actividad_asignacion_id_unidad_id " +
            "ON asignacion_actividad (asignacion_id, unidad_id)"
    )
    db.execSQL(
        "CREATE INDEX IF NOT EXISTS index_asignacion_actividad_actividad_id_unidad_id " +
            "ON asignacion_actividad (actividad_id, unidad_id)"
    )

    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS intento (
            id INTEGER NOT NULL,
            estudiante_id INTEGER NOT NULL,
            actividad_id INTEGER NOT NULL,
            numero INTEGER NOT NULL,
            fecha_inicio INTEGER NOT NULL,
            fecha_fin INTEGER,
            PRIMARY KEY(id),
            FOREIGN KEY(estudiante_id) REFERENCES usuario(id)
                ON UPDATE NO ACTION ON DELETE RESTRICT,
            FOREIGN KEY(actividad_id) REFERENCES actividad(id)
                ON UPDATE NO ACTION ON DELETE RESTRICT
        )
        """.trimIndent()
    )
    db.execSQL(
        """
        INSERT INTO intento (id, estudiante_id, actividad_id, numero, fecha_inicio, fecha_fin)
        SELECT id, estudiante_id, actividad_id, numero, fecha_inicio, fecha_fin
        FROM __migracion_intento
        """.trimIndent()
    )
    db.execSQL("CREATE INDEX IF NOT EXISTS index_intento_estudiante_id ON intento (estudiante_id)")
    db.execSQL("CREATE INDEX IF NOT EXISTS index_intento_actividad_id ON intento (actividad_id)")
    db.execSQL(
        "CREATE UNIQUE INDEX IF NOT EXISTS index_intento_estudiante_id_actividad_id_numero " +
            "ON intento (estudiante_id, actividad_id, numero)"
    )

    db.execSQL(
        """
        CREATE TABLE IF NOT EXISTS respuesta_estudiante (
            id INTEGER NOT NULL,
            intento_id INTEGER NOT NULL,
            pregunta_id INTEGER NOT NULL,
            alternativa_id INTEGER NOT NULL,
            es_correcta INTEGER NOT NULL,
            PRIMARY KEY(id),
            FOREIGN KEY(intento_id) REFERENCES intento(id)
                ON UPDATE NO ACTION ON DELETE RESTRICT,
            FOREIGN KEY(pregunta_id) REFERENCES pregunta(id)
                ON UPDATE NO ACTION ON DELETE RESTRICT,
            FOREIGN KEY(alternativa_id) REFERENCES alternativa(id)
                ON UPDATE NO ACTION ON DELETE RESTRICT,
            FOREIGN KEY(pregunta_id, alternativa_id) REFERENCES alternativa(pregunta_id, id)
                ON UPDATE NO ACTION ON DELETE RESTRICT
        )
        """.trimIndent()
    )
    db.execSQL(
        """
        INSERT INTO respuesta_estudiante (
            id, intento_id, pregunta_id, alternativa_id, es_correcta
        )
        SELECT id, intento_id, pregunta_id, alternativa_id, es_correcta
        FROM __migracion_respuesta_estudiante
        """.trimIndent()
    )
    db.execSQL(
        "CREATE UNIQUE INDEX IF NOT EXISTS index_respuesta_estudiante_intento_id_pregunta_id " +
            "ON respuesta_estudiante (intento_id, pregunta_id)"
    )
    db.execSQL(
        "CREATE INDEX IF NOT EXISTS index_respuesta_estudiante_pregunta_id " +
            "ON respuesta_estudiante (pregunta_id)"
    )
    db.execSQL(
        "CREATE INDEX IF NOT EXISTS index_respuesta_estudiante_alternativa_id " +
            "ON respuesta_estudiante (alternativa_id)"
    )
    db.execSQL(
        "CREATE INDEX IF NOT EXISTS index_respuesta_estudiante_pregunta_id_alternativa_id " +
            "ON respuesta_estudiante (pregunta_id, alternativa_id)"
    )
}

/** Borrar las tablas temporales de respaldo. */
private fun dropBackups(db: SupportSQLiteDatabase) {
    listOf(
        "respuesta_estudiante",
        "intento",
        "asignacion_actividad",
        "alternativa",
        "pregunta",
        "recurso_contenido"
    ).forEach { table ->
        db.execSQL("DROP TABLE __migracion_$table")
    }
}

/** Contar filas de una tabla. */
private fun SupportSQLiteDatabase.countRows(table: String): Int =
    query("SELECT COUNT(*) FROM $table").use { cursor ->
        if (cursor.moveToFirst()) cursor.getInt(0) else 0
    }

/** Verificar que no queden referencias rotas después de la migración. */
private fun ensureForeignKeysValid(db: SupportSQLiteDatabase) {
    db.query("PRAGMA foreign_key_check").use { cursor ->
        check(!cursor.moveToFirst()) {
            "La migración dejó referencias rotas entre tablas."
        }
    }
}

/** Callback que prepara la base al crearse o abrirse. */
private class SeedCallback(
    private val context: Context
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        SeedLoader.load(context, db)
    }

    /**
     * Recrea el índice parcial de `PRUEBA_FINAL` que Room no expresa.
     *
     * Corre después de la validación de esquema, así que un índice parcial extra
     * no hace fallar `MIGRATION_1_2`. Es idempotente.
     */
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        SeedLoader.createIndexes(db)
    }
}

/** Carga la semilla de demostración y crea los índices que Room no expresa. */
object SeedLoader {

    /** Crea los índices que Room no puede expresar con anotaciones. */
    fun createIndexes(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS ux_actividad_prueba_final_por_unidad
            ON actividad (unidad_id)
            WHERE tipo = 'PRUEBA_FINAL'
            """.trimIndent()
        )
    }

    /** Ejecuta la semilla sobre una base recién creada o vaciada. */
    fun load(context: Context, db: SupportSQLiteDatabase) {
        createIndexes(db)

        val script = context.assets
            .open("database/seed.sql")
            .bufferedReader()
            .use { it.readText() }

        splitStatements(script).forEach(db::execSQL)
    }

    /**
     * Divide el script en sentencias SQL.
     *
     * No se puede usar `split(';')`: la semilla tiene puntos y comas dentro de
     * literales de texto. Este recorrido respeta las cadenas entre comillas
     * simples (incluido el escape `''`) y descarta los comentarios de línea y
     * de bloque solo cuando aparecen fuera de una cadena.
     */
    internal fun splitStatements(script: String): List<String> {
        val statements = mutableListOf<String>()
        val current = StringBuilder()
        var index = 0
        var inString = false

        while (index < script.length) {
            val char = script[index]

            if (inString) {
                current.append(char)
                if (char == '\'') {
                    if (index + 1 < script.length && script[index + 1] == '\'') {
                        current.append(script[index + 1])
                        index += 2
                        continue
                    }
                    inString = false
                }
                index++
                continue
            }

            when {
                char == '\'' -> {
                    inString = true
                    current.append(char)
                    index++
                }

                char == '-' && script.getOrNull(index + 1) == '-' -> {
                    while (index < script.length && script[index] != '\n') index++
                }

                char == '/' && script.getOrNull(index + 1) == '*' -> {
                    index += 2
                    while (
                        index + 1 < script.length &&
                        !(script[index] == '*' && script[index + 1] == '/')
                    ) {
                        index++
                    }
                    index = (index + 2).coerceAtMost(script.length)
                }

                char == ';' -> {
                    current.toString().trim()
                        .takeIf(String::isNotEmpty)
                        ?.let(statements::add)
                    current.clear()
                    index++
                }

                else -> {
                    current.append(char)
                    index++
                }
            }
        }

        current.toString().trim()
            .takeIf(String::isNotEmpty)
            ?.let(statements::add)

        return statements
    }
}
