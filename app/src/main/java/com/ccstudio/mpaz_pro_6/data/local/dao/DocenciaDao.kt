package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
}
