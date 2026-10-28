package com.ccstudio.mpaz_pro_6.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Docencia de un docente en un curso y una asignatura (`docencia`).
 *
 * El nombre de cada entidad relacionada no se duplica: se resuelve al leer la
 * caché mediante una consulta con `JOIN`. El índice único reproduce la regla del
 * backend: en un mismo curso y asignatura solo puede existir una docencia.
 */
@Entity(
    tableName = "docencia",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["docente_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["curso_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["asignatura_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["docente_id"]),
        Index(value = ["curso_id"]),
        Index(value = ["asignatura_id"]),
        Index(value = ["curso_id", "asignatura_id"], unique = true)
    ]
)
data class DocenciaEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "docente_id") val docenteId: Int,
    @ColumnInfo(name = "curso_id") val cursoId: Int,
    @ColumnInfo(name = "asignatura_id") val asignaturaId: Int
)
