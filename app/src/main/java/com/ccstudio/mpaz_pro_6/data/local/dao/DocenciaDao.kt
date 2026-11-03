package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.ActividadCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.AsignaturaCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.UsuarioCursoEntity

@Dao
interface DocenciaDao {
    // AsignaturaCurso
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsignaturaCurso(entity: AsignaturaCursoEntity): Long

    @Delete
    suspend fun deleteAsignaturaCurso(entity: AsignaturaCursoEntity)

    @Query("SELECT * FROM asignatura_curso WHERE curso_id = :cursoId")
    suspend fun getAsignaturasByCursoId(cursoId: Long): List<AsignaturaCursoEntity>

    @Query("SELECT * FROM asignatura_curso WHERE asignatura_id = :asignaturaId")
    suspend fun getCursosByAsignaturaId(asignaturaId: Long): List<AsignaturaCursoEntity>

    // UsuarioCurso
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuarioCurso(entity: UsuarioCursoEntity): Long

    @Delete
    suspend fun deleteUsuarioCurso(entity: UsuarioCursoEntity)

    @Query("SELECT * FROM usuario_curso WHERE curso_id = :cursoId")
    suspend fun getUsuariosByCursoId(cursoId: Long): List<UsuarioCursoEntity>

    @Query("SELECT * FROM usuario_curso WHERE usuario_id = :usuarioId")
    suspend fun getCursosByUsuarioId(usuarioId: Long): List<UsuarioCursoEntity>

    // ActividadCurso
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActividadCurso(entity: ActividadCursoEntity): Long

    @Update
    suspend fun updateActividadCurso(entity: ActividadCursoEntity)

    @Delete
    suspend fun deleteActividadCurso(entity: ActividadCursoEntity)

    @Query("SELECT * FROM actividad_curso WHERE actividad_curso_id = :id")
    suspend fun getActividadCursoById(id: Long): ActividadCursoEntity?

    @Query("SELECT * FROM actividad_curso WHERE actividad_id = :actividadId AND curso_id = :cursoId")
    suspend fun getActividadCursoByActividadYCurso(actividadId: Long, cursoId: Long): ActividadCursoEntity?

    @Query("SELECT * FROM actividad_curso WHERE curso_id = :cursoId")
    suspend fun getActividadesByCursoId(cursoId: Long): List<ActividadCursoEntity>

    @Query("SELECT * FROM actividad_curso WHERE curso_id = :cursoId AND visible = 1 AND habilitada = 1")
    suspend fun getActividadesVisiblesByCursoId(cursoId: Long): List<ActividadCursoEntity>
}
