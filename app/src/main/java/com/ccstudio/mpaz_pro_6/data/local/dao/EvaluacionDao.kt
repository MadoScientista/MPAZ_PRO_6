package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ccstudio.mpaz_pro_6.data.local.entity.evaluacion.IntentoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.evaluacion.RespuestaEntity

@Dao
interface EvaluacionDao {
    // Intento
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntento(entity: IntentoEntity): Long

    @Update
    suspend fun updateIntento(entity: IntentoEntity)

    @Delete
    suspend fun deleteIntento(entity: IntentoEntity)

    @Query("SELECT * FROM intento WHERE intento_id = :id")
    suspend fun getIntentoById(id: Long): IntentoEntity?

    @Query("SELECT * FROM intento WHERE usuario_id = :usuarioId")
    suspend fun getIntentosByUsuarioId(usuarioId: Long): List<IntentoEntity>

    @Query("SELECT * FROM intento WHERE actividad_id = :actividadId")
    suspend fun getIntentosByActividadId(actividadId: Long): List<IntentoEntity>

    // Respuesta
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRespuesta(entity: RespuestaEntity): Long

    @Delete
    suspend fun deleteRespuesta(entity: RespuestaEntity)

    @Query("SELECT * FROM respuesta WHERE intento_id = :intentoId")
    suspend fun getRespuestasByIntentoId(intentoId: Long): List<RespuestaEntity>
}
