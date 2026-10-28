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
import com.ccstudio.mpaz_pro_6.data.local.dao.DocenciaDao
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
import com.ccstudio.mpaz_pro_6.data.local.entity.DocenciaEntity
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
        DocenciaEntity::class,
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
    version = 4,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MpazDatabase : RoomDatabase() {

    abstract fun catalogDao(): CatalogDao

    abstract fun userDao(): UserDao

    abstract fun docenciaDao(): DocenciaDao

    abstract fun unitDao(): UnitDao

    abstract fun curriculumDao(): CurriculumDao

    abstract fun assignmentDao(): AssignmentDao

    abstract fun attemptDao(): AttemptDao

    abstract fun reportDao(): ReportDao

    companion object {

        /** Nombre del archivo de base de datos. */
        const val NAME = "mpaz.db"

        /**
         * Construye la instancia única de la base local.
         *
         * @param seedOnEmpty vuelve a cargar la semilla en modo local si una
         * migración destructiva dejó la base vacía. En modo remoto la caché se
         * llena con `InitialSync`.
         */
        fun build(context: Context, seedOnEmpty: Boolean = false): MpazDatabase =
            Room.databaseBuilder(context, MpazDatabase::class.java, NAME)
                // La app no tiene datos de producción: si cambia el esquema se
                // reconstruye la caché y se vuelve a sincronizar.
                .addMigrations(MIGRATION_3_4)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .addCallback(
                    SeedCallback(
                        context = context.applicationContext,
                        seedOnEmpty = seedOnEmpty
                    )
                )
                .build()
    }
}

/** Callback que prepara la base al crearse o al quedar vacía tras una migración. */
private class SeedCallback(
    private val context: Context,
    private val seedOnEmpty: Boolean
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        SeedLoader.load(context, db)
    }

    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        if (!seedOnEmpty || hasCourses(db)) return
        SeedLoader.load(context, db)
    }

    /** Indica si la base ya tiene cursos cargados. */
    private fun hasCourses(db: SupportSQLiteDatabase): Boolean =
        db.query("SELECT COUNT(*) FROM curso").use { cursor ->
            cursor.moveToFirst() && cursor.getInt(0) > 0
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


/**
 * Migración 3 → 4.
 *
 * Agrega `actividad.contenido_id`, que relaciona una actividad con un contenido
 * de la misma unidad. Las actividades existentes quedan transversales.
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE actividad ADD COLUMN contenido_id INTEGER")
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_actividad_contenido_id " +
                "ON actividad (contenido_id)"
        )
    }
}
