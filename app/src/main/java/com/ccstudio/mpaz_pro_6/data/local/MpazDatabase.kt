package com.ccstudio.mpaz_pro_6.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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
    version = 1,
    exportSchema = true
)
abstract class MpazDatabase : RoomDatabase() {

    abstract fun identidadDao(): IdentidadDao
    abstract fun curriculoDao(): CurriculoDao
    abstract fun docenciaDao(): DocenciaDao
    abstract fun evaluacionDao(): EvaluacionDao
    abstract fun informeDao(): InformeDao

    companion object {
        @Volatile
        private var INSTANCE: MpazDatabase? = null

        fun getDatabase(context: Context): MpazDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MpazDatabase::class.java,
                    "mpaz_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
