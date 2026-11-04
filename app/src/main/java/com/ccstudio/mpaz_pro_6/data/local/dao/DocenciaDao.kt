package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Embedded
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AsignaturaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.ActividadCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.AsignaturaCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.UsuarioCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.CursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.NivelEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.SeccionEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity

data class AsignaturaConDocente(
    @Embedded val asignatura: AsignaturaEntity,
    @Embedded(prefix = "doc_") val docente: UsuarioEntity?
)

data class AsignaturaCursoDetalle(
    @Embedded val asignaturaCurso: AsignaturaCursoEntity,
    @Embedded(prefix = "asig_") val asignatura: AsignaturaEntity,
    @Embedded(prefix = "curso_") val curso: CursoEntity,
    @Embedded(prefix = "niv_") val nivel: NivelEntity,
    @Embedded(prefix = "sec_") val seccion: SeccionEntity,
    @Embedded(prefix = "doc_") val docente: UsuarioEntity?
) {
    val nombreCurso: String
        get() = "${nivel.nombre} ${seccion.letra}"

    val nombreCompletoCurso: String
        get() = "${nivel.nombre} ${seccion.letra} (${curso.annio})"
}

@Dao
interface DocenciaDao {
    // AsignaturaCurso
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsignaturaCurso(entity: AsignaturaCursoEntity): Long

    @Delete
    suspend fun deleteAsignaturaCurso(entity: AsignaturaCursoEntity)

    @Query("SELECT * FROM asignatura_curso WHERE curso_id = :cursoId")
    suspend fun getAsignaturasByCursoId(cursoId: Long): List<AsignaturaCursoEntity>

    @Query("SELECT a.* FROM asignatura a INNER JOIN asignatura_curso ac ON a.asignatura_id = ac.asignatura_id WHERE ac.curso_id = :cursoId")
    suspend fun getAsignaturasEntityByCursoId(cursoId: Long): List<com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AsignaturaEntity>

    @Query("""
        SELECT a.*, 
               u.usuario_id AS doc_usuario_id,
               u.nombre_usuario AS doc_nombre_usuario,
               u.password_hash AS doc_password_hash,
               u.pnombre AS doc_pnombre,
               u.snombre AS doc_snombre,
               u.apaterno AS doc_apaterno,
               u.amaterno AS doc_amaterno,
               u.rol_id AS doc_rol_id,
               u.activo AS doc_activo
        FROM asignatura a
        INNER JOIN asignatura_curso ac ON a.asignatura_id = ac.asignatura_id
        LEFT JOIN usuario u ON ac.docente_id = u.usuario_id
        WHERE ac.curso_id = :cursoId
    """)
    suspend fun getAsignaturasConDocenteByCursoId(cursoId: Long): List<AsignaturaConDocente>

    @Query("UPDATE asignatura_curso SET docente_id = :docenteId WHERE curso_id = :cursoId AND asignatura_id = :asignaturaId")
    suspend fun updateDocenteInAsignaturaCurso(cursoId: Long, asignaturaId: Long, docenteId: Long?)

    @Query("DELETE FROM asignatura_curso WHERE curso_id = :cursoId AND asignatura_id = :asignaturaId")
    suspend fun deleteAsignaturaCursoByCursoYAsignatura(cursoId: Long, asignaturaId: Long)

    @Query("SELECT * FROM asignatura_curso WHERE asignatura_id = :asignaturaId")
    suspend fun getCursosByAsignaturaId(asignaturaId: Long): List<AsignaturaCursoEntity>

    @Query("""
        SELECT 
            ac.id, ac.curso_id, ac.asignatura_id, ac.docente_id,
            a.asignatura_id AS asig_asignatura_id,
            a.nombre AS asig_nombre,
            a.nivel_id AS asig_nivel_id,
            c.curso_id AS curso_curso_id,
            c.annio AS curso_annio,
            c.nivel_id AS curso_nivel_id,
            c.seccion_id AS curso_seccion_id,
            n.nivel_id AS niv_nivel_id,
            n.nombre AS niv_nombre,
            s.seccion_id AS sec_seccion_id,
            s.letra AS sec_letra,
            u.usuario_id AS doc_usuario_id,
            u.nombre_usuario AS doc_nombre_usuario,
            u.password_hash AS doc_password_hash,
            u.pnombre AS doc_pnombre,
            u.snombre AS doc_snombre,
            u.apaterno AS doc_apaterno,
            u.amaterno AS doc_amaterno,
            u.rol_id AS doc_rol_id,
            u.activo AS doc_activo
        FROM asignatura_curso ac
        INNER JOIN asignatura a ON ac.asignatura_id = a.asignatura_id
        INNER JOIN curso c ON ac.curso_id = c.curso_id
        INNER JOIN nivel n ON c.nivel_id = n.nivel_id
        INNER JOIN seccion s ON c.seccion_id = s.seccion_id
        LEFT JOIN usuario u ON ac.docente_id = u.usuario_id
        WHERE ac.docente_id = :docenteId
    """)
    suspend fun getAsignaturasCursoByDocenteId(docenteId: Long): List<AsignaturaCursoDetalle>

    @Query("""
        SELECT 
            ac.id, ac.curso_id, ac.asignatura_id, ac.docente_id,
            a.asignatura_id AS asig_asignatura_id,
            a.nombre AS asig_nombre,
            a.nivel_id AS asig_nivel_id,
            c.curso_id AS curso_curso_id,
            c.annio AS curso_annio,
            c.nivel_id AS curso_nivel_id,
            c.seccion_id AS curso_seccion_id,
            n.nivel_id AS niv_nivel_id,
            n.nombre AS niv_nombre,
            s.seccion_id AS sec_seccion_id,
            s.letra AS sec_letra,
            u.usuario_id AS doc_usuario_id,
            u.nombre_usuario AS doc_nombre_usuario,
            u.password_hash AS doc_password_hash,
            u.pnombre AS doc_pnombre,
            u.snombre AS doc_snombre,
            u.apaterno AS doc_apaterno,
            u.amaterno AS doc_amaterno,
            u.rol_id AS doc_rol_id,
            u.activo AS doc_activo
        FROM asignatura_curso ac
        INNER JOIN asignatura a ON ac.asignatura_id = a.asignatura_id
        INNER JOIN curso c ON ac.curso_id = c.curso_id
        INNER JOIN nivel n ON c.nivel_id = n.nivel_id
        INNER JOIN seccion s ON c.seccion_id = s.seccion_id
        LEFT JOIN usuario u ON ac.docente_id = u.usuario_id
    """)
    suspend fun getAllAsignaturasCursoDetalles(): List<AsignaturaCursoDetalle>

    @Query("SELECT COUNT(*) FROM asignatura_curso WHERE docente_id = :docenteId AND curso_id = :cursoId")
    suspend fun countAsignaturasByDocenteYCurso(docenteId: Long, cursoId: Long): Int

    // UsuarioCurso
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuarioCurso(entity: UsuarioCursoEntity): Long

    @Delete
    suspend fun deleteUsuarioCurso(entity: UsuarioCursoEntity)

    @Query("DELETE FROM usuario_curso WHERE curso_id = :cursoId AND usuario_id = :usuarioId")
    suspend fun deleteUsuarioCursoByCursoYUsuario(cursoId: Long, usuarioId: Long)

    @Query("SELECT * FROM usuario_curso WHERE curso_id = :cursoId")
    suspend fun getUsuariosByCursoId(cursoId: Long): List<UsuarioCursoEntity>

    @Query("SELECT u.* FROM usuario u INNER JOIN usuario_curso uc ON u.usuario_id = uc.usuario_id WHERE uc.curso_id = :cursoId AND u.rol_id = 2")
    suspend fun getDocentesByCursoId(cursoId: Long): List<com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity>

    @Query("SELECT u.* FROM usuario u INNER JOIN usuario_curso uc ON u.usuario_id = uc.usuario_id WHERE uc.curso_id = :cursoId AND u.rol_id = 3")
    suspend fun getEstudiantesByCursoId(cursoId: Long): List<com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity>

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
