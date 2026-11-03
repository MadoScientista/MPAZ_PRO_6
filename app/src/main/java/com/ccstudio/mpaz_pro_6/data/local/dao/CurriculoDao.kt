package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
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

@Dao
interface CurriculoDao {
    // Asignatura
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsignatura(entity: AsignaturaEntity): Long

    @Update
    suspend fun updateAsignatura(entity: AsignaturaEntity)

    @Delete
    suspend fun deleteAsignatura(entity: AsignaturaEntity)

    @Query("SELECT * FROM asignatura WHERE asignatura_id = :id")
    suspend fun getAsignaturaById(id: Long): AsignaturaEntity?

    @Query("SELECT * FROM asignatura")
    suspend fun getAllAsignaturas(): List<AsignaturaEntity>

    // EstadoAutorizacion
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEstadoAutorizacion(entity: EstadoAutorizacionEntity): Long

    @Update
    suspend fun updateEstadoAutorizacion(entity: EstadoAutorizacionEntity)

    @Delete
    suspend fun deleteEstadoAutorizacion(entity: EstadoAutorizacionEntity)

    @Query("SELECT * FROM estado_autorizacion WHERE estado_id = :id")
    suspend fun getEstadoAutorizacionById(id: Long): EstadoAutorizacionEntity?

    @Query("SELECT * FROM estado_autorizacion")
    suspend fun getAllEstadosAutorizacion(): List<EstadoAutorizacionEntity>

    // TipoActividad
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTipoActividad(entity: TipoActividadEntity): Long

    @Update
    suspend fun updateTipoActividad(entity: TipoActividadEntity)

    @Delete
    suspend fun deleteTipoActividad(entity: TipoActividadEntity)

    @Query("SELECT * FROM tipo_actividad WHERE tipo_actividad_id = :id")
    suspend fun getTipoActividadById(id: Long): TipoActividadEntity?

    @Query("SELECT * FROM tipo_actividad")
    suspend fun getAllTiposActividad(): List<TipoActividadEntity>

    // TipoRecurso
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTipoRecurso(entity: TipoRecursoEntity): Long

    @Update
    suspend fun updateTipoRecurso(entity: TipoRecursoEntity)

    @Delete
    suspend fun deleteTipoRecurso(entity: TipoRecursoEntity)

    @Query("SELECT * FROM tipo_recurso WHERE tipo_recurso_id = :id")
    suspend fun getTipoRecursoById(id: Long): TipoRecursoEntity?

    @Query("SELECT * FROM tipo_recurso")
    suspend fun getAllTiposRecurso(): List<TipoRecursoEntity>

    // Unidad
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUnidad(entity: UnidadEntity): Long

    @Update
    suspend fun updateUnidad(entity: UnidadEntity)

    @Delete
    suspend fun deleteUnidad(entity: UnidadEntity)

    @Query("SELECT * FROM unidad WHERE unidad_id = :id")
    suspend fun getUnidadById(id: Long): UnidadEntity?

    @Query("SELECT * FROM unidad")
    suspend fun getAllUnidades(): List<UnidadEntity>

    @Transaction
    @Query("SELECT * FROM unidad WHERE estado_id = :estadoId")
    suspend fun getUnidadesConDetallesByEstadoId(estadoId: Long): List<UnidadConDetalles>

    @Transaction
    @Query("SELECT * FROM unidad WHERE unidad_id = :id")
    suspend fun getUnidadConDetallesById(id: Long): UnidadConDetalles?

    @Query("UPDATE unidad SET estado_id = :nuevoEstadoId WHERE unidad_id = :unidadId")
    suspend fun updateUnidadEstado(unidadId: Long, nuevoEstadoId: Long)

    // ObjetivoAprendizaje
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObjetivoAprendizaje(entity: ObjetivoAprendizajeEntity): Long

    @Update
    suspend fun updateObjetivoAprendizaje(entity: ObjetivoAprendizajeEntity)

    @Delete
    suspend fun deleteObjetivoAprendizaje(entity: ObjetivoAprendizajeEntity)

    @Query("SELECT * FROM objetivo_aprendizaje WHERE objetivo_aprendizaje_id = :id")
    suspend fun getObjetivoAprendizajeById(id: Long): ObjetivoAprendizajeEntity?

    @Query("SELECT * FROM objetivo_aprendizaje")
    suspend fun getAllObjetivosAprendizaje(): List<ObjetivoAprendizajeEntity>

    // ObjetivoAprendizajeUnidad
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObjetivoAprendizajeUnidad(entity: ObjetivoAprendizajeUnidadEntity): Long

    @Delete
    suspend fun deleteObjetivoAprendizajeUnidad(entity: ObjetivoAprendizajeUnidadEntity)

    @Query("SELECT * FROM objetivo_aprendizaje_unidad WHERE unidad_id = :unidadId")
    suspend fun getObjetivosByUnidadId(unidadId: Long): List<ObjetivoAprendizajeUnidadEntity>

    // Contenido
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContenido(entity: ContenidoEntity): Long

    @Update
    suspend fun updateContenido(entity: ContenidoEntity)

    @Delete
    suspend fun deleteContenido(entity: ContenidoEntity)

    @Query("SELECT * FROM contenido WHERE contenido_id = :id")
    suspend fun getContenidoById(id: Long): ContenidoEntity?

    @Query("SELECT * FROM contenido")
    suspend fun getAllContenidos(): List<ContenidoEntity>

    @Query("SELECT * FROM contenido WHERE unidad_id = :unidadId")
    suspend fun getContenidosByUnidadId(unidadId: Long): List<ContenidoEntity>

    // Recurso
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecurso(entity: RecursoEntity): Long

    @Update
    suspend fun updateRecurso(entity: RecursoEntity)

    @Delete
    suspend fun deleteRecurso(entity: RecursoEntity)

    @Query("SELECT * FROM recurso WHERE recurso_id = :id")
    suspend fun getRecursoById(id: Long): RecursoEntity?

    @Query("SELECT * FROM recurso")
    suspend fun getAllRecursos(): List<RecursoEntity>

    // Actividad
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActividad(entity: ActividadEntity): Long

    @Update
    suspend fun updateActividad(entity: ActividadEntity)

    @Delete
    suspend fun deleteActividad(entity: ActividadEntity)

    @Query("SELECT * FROM actividad WHERE actividad_id = :id")
    suspend fun getActividadById(id: Long): ActividadEntity?

    @Query("SELECT * FROM actividad")
    suspend fun getAllActividades(): List<ActividadEntity>

    @Transaction
    @Query("SELECT * FROM actividad WHERE contenido_id = :contenidoId")
    suspend fun getActividadesConTipoByContenidoId(contenidoId: Long): List<ActividadConTipo>

    @Transaction
    @Query("SELECT * FROM actividad WHERE actividad_id = :id")
    suspend fun getActividadConTipoById(id: Long): ActividadConTipo?

    @Transaction
    @Query("SELECT a.* FROM actividad a INNER JOIN contenido c ON a.contenido_id = c.contenido_id WHERE c.unidad_id = :unidadId AND a.tipo_actividad_id = 3 LIMIT 1")
    suspend fun getPruebaFinalConTipoByUnidadId(unidadId: Long): ActividadConTipo?

    // Pregunta
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPregunta(entity: PreguntaEntity): Long

    @Update
    suspend fun updatePregunta(entity: PreguntaEntity)

    @Delete
    suspend fun deletePregunta(entity: PreguntaEntity)

    @Query("SELECT * FROM pregunta WHERE pregunta_id = :id")
    suspend fun getPreguntaById(id: Long): PreguntaEntity?

    @Query("SELECT * FROM pregunta")
    suspend fun getAllPreguntas(): List<PreguntaEntity>

    @Transaction
    @Query("SELECT p.* FROM pregunta p INNER JOIN pregunta_actividad pa ON p.pregunta_id = pa.pregunta_id WHERE pa.actividad_id = :actividadId")
    suspend fun getPreguntasConAlternativasByActividadId(actividadId: Long): List<PreguntaConAlternativas>

    // Alternativa
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlternativa(entity: AlternativaEntity): Long

    @Update
    suspend fun updateAlternativa(entity: AlternativaEntity)

    @Delete
    suspend fun deleteAlternativa(entity: AlternativaEntity)

    @Query("SELECT * FROM alternativa WHERE alternativa_id = :id")
    suspend fun getAlternativaById(id: Long): AlternativaEntity?

    @Query("SELECT * FROM alternativa")
    suspend fun getAllAlternativas(): List<AlternativaEntity>

    // AlternativaPregunta
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlternativaPregunta(entity: AlternativaPreguntaEntity): Long

    @Delete
    suspend fun deleteAlternativaPregunta(entity: AlternativaPreguntaEntity)

    @Query("SELECT * FROM alternativa_pregunta WHERE pregunta_id = :preguntaId")
    suspend fun getAlternativasByPreguntaId(preguntaId: Long): List<AlternativaPreguntaEntity>

    // PreguntaActividad
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreguntaActividad(entity: PreguntaActividadEntity): Long

    @Delete
    suspend fun deletePreguntaActividad(entity: PreguntaActividadEntity)

    @Query("SELECT * FROM pregunta_actividad WHERE actividad_id = :actividadId")
    suspend fun getPreguntasByActividadId(actividadId: Long): List<PreguntaActividadEntity>

    // ObjetivoAprendizajeContenido
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObjetivoAprendizajeContenido(entity: ObjetivoAprendizajeContenidoEntity): Long

    @Delete
    suspend fun deleteObjetivoAprendizajeContenido(entity: ObjetivoAprendizajeContenidoEntity)

    @Query("SELECT * FROM objetivo_aprendizaje_contenido WHERE contenido_id = :contenidoId")
    suspend fun getObjetivosByContenidoId(contenidoId: Long): List<ObjetivoAprendizajeContenidoEntity>
}
