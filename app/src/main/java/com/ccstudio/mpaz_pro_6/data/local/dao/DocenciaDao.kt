package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ccstudio.mpaz_pro_6.data.local.entity.DocenciaEntity

/**
 * Docencia leída de la caché con los nombres de sus entidades relacionadas.
 *
 * La respuesta del backend ya entrega los nombres; esta proyección evita que el
 * repositorio haga tres consultas por cada docencia al caer a la caché.
 */
data class DocenciaWithNames(
    val id: Int,
    @ColumnInfo(name = "docente_id") val docenteId: Int,
    @ColumnInfo(name = "docente_nombre") val docenteNombre: String,
    @ColumnInfo(name = "curso_id") val cursoId: Int,
    @ColumnInfo(name = "curso_nombre") val cursoNombre: String,
    @ColumnInfo(name = "asignatura_id") val asignaturaId: Int,
    @ColumnInfo(name = "asignatura_nombre") val asignaturaNombre: String
)

/** Consultas de docencias y de las asignaciones que impiden borrarlas. */
@Dao
interface DocenciaDao {

    @Query(
        """
        SELECT d.id AS id,
               d.docente_id AS docente_id,
               u.nombre AS docente_nombre,
               d.curso_id AS curso_id,
               c.nombre AS curso_nombre,
               d.asignatura_id AS asignatura_id,
               a.nombre AS asignatura_nombre
        FROM docencia d
        INNER JOIN usuario u ON u.id = d.docente_id
        INNER JOIN curso c ON c.id = d.curso_id
        INNER JOIN asignatura a ON a.id = d.asignatura_id
        ORDER BY c.nombre, a.nombre, u.nombre
        """
    )
    suspend fun getDocencias(): List<DocenciaWithNames>

    @Query(
        """
        SELECT d.id AS id,
               d.docente_id AS docente_id,
               u.nombre AS docente_nombre,
               d.curso_id AS curso_id,
               c.nombre AS curso_nombre,
               d.asignatura_id AS asignatura_id,
               a.nombre AS asignatura_nombre
        FROM docencia d
        INNER JOIN usuario u ON u.id = d.docente_id
        INNER JOIN curso c ON c.id = d.curso_id
        INNER JOIN asignatura a ON a.id = d.asignatura_id
        WHERE d.id = :id
        """
    )
    suspend fun getDocenciaById(id: Int): DocenciaWithNames?

    @Upsert
    suspend fun upsertDocencia(entity: DocenciaEntity)

    @Upsert
    suspend fun upsertDocencias(entities: List<DocenciaEntity>)

    @Query("DELETE FROM docencia")
    suspend fun deleteAllDocencias()

    @Query("DELETE FROM docencia WHERE id = :id")
    suspend fun deleteDocenciaById(id: Int)

    /**
     * Cuenta las asignaciones de un curso cuya unidad pertenece a la asignatura.
     *
     * Se usa para rechazar el borrado de una docencia con `409`, igual que el
     * backend.
     */
    @Query(
        """
        SELECT COUNT(*)
        FROM asignacion
        INNER JOIN unidad ON unidad.id = asignacion.unidad_id
        WHERE asignacion.curso_id = :courseId
          AND unidad.asignatura_id = :subjectId
        """
    )
    suspend fun countAssignments(courseId: Int, subjectId: Int): Int
}
