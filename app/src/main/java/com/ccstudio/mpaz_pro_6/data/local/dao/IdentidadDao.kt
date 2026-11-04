package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.CursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.NivelEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.RolEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.SeccionEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity

@Dao
interface IdentidadDao {
    // Rol
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRol(rol: RolEntity): Long

    @Update
    suspend fun updateRol(rol: RolEntity)

    @Delete
    suspend fun deleteRol(rol: RolEntity)

    @Query("SELECT * FROM rol WHERE rol_id = :id")
    suspend fun getRolById(id: Long): RolEntity?

    @Query("SELECT * FROM rol")
    suspend fun getAllRoles(): List<RolEntity>

    // Nivel
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNivel(nivel: NivelEntity): Long

    @Update
    suspend fun updateNivel(nivel: NivelEntity)

    @Delete
    suspend fun deleteNivel(nivel: NivelEntity)

    @Query("SELECT * FROM nivel WHERE nivel_id = :id")
    suspend fun getNivelById(id: Long): NivelEntity?

    @Query("SELECT * FROM nivel")
    suspend fun getAllNiveles(): List<NivelEntity>

    // Seccion
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeccion(seccion: SeccionEntity): Long

    @Update
    suspend fun updateSeccion(seccion: SeccionEntity)

    @Delete
    suspend fun deleteSeccion(seccion: SeccionEntity)

    @Query("SELECT * FROM seccion WHERE seccion_id = :id")
    suspend fun getSeccionById(id: Long): SeccionEntity?

    @Query("SELECT * FROM seccion")
    suspend fun getAllSecciones(): List<SeccionEntity>

    // Curso
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurso(curso: CursoEntity): Long

    @Update
    suspend fun updateCurso(curso: CursoEntity)

    @Delete
    suspend fun deleteCurso(curso: CursoEntity)

    @Query("SELECT * FROM curso WHERE curso_id = :id")
    suspend fun getCursoById(id: Long): CursoEntity?

    @Query("SELECT * FROM curso")
    suspend fun getAllCursos(): List<CursoEntity>

    @androidx.room.Transaction
    @Query("SELECT * FROM curso")
    suspend fun getCursosConDetalles(): List<CursoConDetalles>

    @androidx.room.Transaction
    @Query("SELECT * FROM curso WHERE curso_id = :id")
    suspend fun getCursoConDetallesById(id: Long): CursoConDetalles?

    // Usuario
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: UsuarioEntity): Long

    @Update
    suspend fun updateUsuario(usuario: UsuarioEntity)

    @Delete
    suspend fun deleteUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuario WHERE usuario_id = :id")
    suspend fun getUsuarioById(id: Long): UsuarioEntity?

    @Query("SELECT * FROM usuario WHERE nombre_usuario = :nombreUsuario")
    suspend fun getUsuarioByNombre(nombreUsuario: String): UsuarioEntity?

    @Query("SELECT * FROM usuario")
    suspend fun getAllUsuarios(): List<UsuarioEntity>

    @androidx.room.Transaction
    @Query("SELECT * FROM usuario")
    suspend fun getAllUsuariosConRol(): List<UsuarioConRol>

    @Query("SELECT * FROM usuario WHERE rol_id = 2 AND activo = 1")
    suspend fun getAllDocentes(): List<UsuarioEntity>

    @Query("SELECT * FROM usuario WHERE rol_id = 3 AND activo = 1")
    suspend fun getAllEstudiantes(): List<UsuarioEntity>
}
