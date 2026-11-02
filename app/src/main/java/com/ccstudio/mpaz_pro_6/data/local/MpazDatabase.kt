package com.ccstudio.mpaz_pro_6.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ccstudio.mpaz_pro_6.data.local.dao.CurriculoDao
import com.ccstudio.mpaz_pro_6.data.local.dao.DocenciaDao
import com.ccstudio.mpaz_pro_6.data.local.dao.EvaluacionDao
import com.ccstudio.mpaz_pro_6.data.local.dao.IdentidadDao
import com.ccstudio.mpaz_pro_6.data.local.dao.InformeDao
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AlternativaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AlternativaPreguntaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AsignaturaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ContenidoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.EstadoAutorizacionEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ObjetivoAprendizajeContenidoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ObjetivoAprendizajeEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ObjetivoAprendizajeUnidadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.PreguntaActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.PreguntaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.RecursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.TipoActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.TipoRecursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.UnidadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.AsignaturaCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.UsuarioCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.evaluacion.IntentoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.evaluacion.RespuestaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.CursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.NivelEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.RolEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.SeccionEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.informe.DetalleInformePedagogicoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.informe.InformePedagogicoEntity

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        seedInitialData(db)
    }
}

fun seedInitialData(db: SupportSQLiteDatabase) {
    db.execSQL("INSERT OR IGNORE INTO rol (rol_id, nombre) VALUES (1, 'UTP')")
    db.execSQL("INSERT OR IGNORE INTO rol (rol_id, nombre) VALUES (2, 'DOCENTE')")
    db.execSQL("INSERT OR IGNORE INTO rol (rol_id, nombre) VALUES (3, 'ALUMNO')")

    db.execSQL("""
        INSERT OR IGNORE INTO usuario (nombre_usuario, password_hash, pnombre, snombre, apaterno, amaterno, rol_id, activo)
        VALUES ('utp', 'mpaz123', 'Usuario', NULL, 'UTP', NULL, 1, 1)
    """.trimIndent())
    db.execSQL("""
        INSERT OR IGNORE INTO usuario (nombre_usuario, password_hash, pnombre, snombre, apaterno, amaterno, rol_id, activo)
        VALUES ('docente', 'mpaz123', 'Usuario', NULL, 'Docente', NULL, 2, 1)
    """.trimIndent())
    db.execSQL("""
        INSERT OR IGNORE INTO usuario (nombre_usuario, password_hash, pnombre, snombre, apaterno, amaterno, rol_id, activo)
        VALUES ('alumno', 'mpaz123', 'Usuario', NULL, 'Alumno', NULL, 3, 1)
    """.trimIndent())
}

@Database(
    entities = [
        // Identidad (5)
        RolEntity::class,
        NivelEntity::class,
        SeccionEntity::class,
        CursoEntity::class,
        UsuarioEntity::class,

        // Currículo (15)
        AsignaturaEntity::class,
        EstadoAutorizacionEntity::class,
        TipoActividadEntity::class,
        TipoRecursoEntity::class,
        UnidadEntity::class,
        ObjetivoAprendizajeEntity::class,
        ObjetivoAprendizajeUnidadEntity::class,
        ContenidoEntity::class,
        RecursoEntity::class,
        ActividadEntity::class,
        PreguntaEntity::class,
        AlternativaEntity::class,
        AlternativaPreguntaEntity::class,
        PreguntaActividadEntity::class,
        ObjetivoAprendizajeContenidoEntity::class,

        // Docencia (2)
        AsignaturaCursoEntity::class,
        UsuarioCursoEntity::class,

        // Evaluación (2)
        IntentoEntity::class,
        RespuestaEntity::class,

        // Informe (2)
        InformePedagogicoEntity::class,
        DetalleInformePedagogicoEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class MpazDatabase : RoomDatabase() {

    abstract fun identidadDao(): IdentidadDao
    abstract fun curriculoDao(): CurriculoDao
    abstract fun docenciaDao(): DocenciaDao
    abstract fun evaluacionDao(): EvaluacionDao
    abstract fun informeDao(): InformeDao

    suspend fun resetDatabase() {
        clearAllTables()
        identidadDao().insertRol(RolEntity(id = 1, nombre = "UTP"))
        identidadDao().insertRol(RolEntity(id = 2, nombre = "DOCENTE"))
        identidadDao().insertRol(RolEntity(id = 3, nombre = "ALUMNO"))

        identidadDao().insertUsuario(
            UsuarioEntity(
                id = 1,
                nombreUsuario = "utp",
                passwordHash = "mpaz123",
                pnombre = "Usuario",
                apaterno = "UTP",
                rolId = 1,
                activo = true
            )
        )
        identidadDao().insertUsuario(
            UsuarioEntity(
                id = 2,
                nombreUsuario = "docente",
                passwordHash = "mpaz123",
                pnombre = "Usuario",
                apaterno = "Docente",
                rolId = 2,
                activo = true
            )
        )
        identidadDao().insertUsuario(
            UsuarioEntity(
                id = 3,
                nombreUsuario = "alumno",
                passwordHash = "mpaz123",
                pnombre = "Usuario",
                apaterno = "Alumno",
                rolId = 3,
                activo = true
            )
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: MpazDatabase? = null

        fun getDatabase(context: Context): MpazDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MpazDatabase::class.java,
                    "mpaz_database"
                )
                .addMigrations(MIGRATION_1_2)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        seedInitialData(db)
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
