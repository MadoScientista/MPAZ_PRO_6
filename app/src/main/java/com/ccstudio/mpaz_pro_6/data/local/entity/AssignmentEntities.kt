package com.ccstudio.mpaz_pro_6.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/** Asignación de una unidad a un curso (`asignacion`). */
@Entity(
    tableName = "asignacion",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["curso_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = LearningUnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["unidad_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["curso_id"]),
        Index(value = ["unidad_id"]),
        Index(value = ["curso_id", "unidad_id"], unique = true),
        Index(value = ["id", "unidad_id"], unique = true)
    ]
)
data class AssignmentEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "curso_id") val cursoId: Int,
    @ColumnInfo(name = "unidad_id") val unidadId: Int,
    @ColumnInfo(name = "max_intentos") val maxIntentos: Int?,
    @ColumnInfo(name = "prueba_final_habilitada", defaultValue = "0")
    val pruebaFinalHabilitada: Boolean,
    val fecha: Instant
)

/** Asociación asignación-objetivo (`asignacion_oa`). */
@Entity(
    tableName = "asignacion_oa",
    primaryKeys = ["asignacion_id", "oa_id"],
    foreignKeys = [
        ForeignKey(
            entity = AssignmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["asignacion_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = LearningUnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["unidad_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = LearningObjectiveEntity::class,
            parentColumns = ["id"],
            childColumns = ["oa_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AssignmentEntity::class,
            parentColumns = ["id", "unidad_id"],
            childColumns = ["asignacion_id", "unidad_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = LearningObjectiveEntity::class,
            parentColumns = ["id", "unidad_id"],
            childColumns = ["oa_id", "unidad_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["unidad_id"]),
        Index(value = ["asignacion_id", "unidad_id"]),
        Index(value = ["oa_id", "unidad_id"])
    ]
)
data class AssignmentObjectiveEntity(
    @ColumnInfo(name = "asignacion_id") val asignacionId: Int,
    @ColumnInfo(name = "unidad_id") val unidadId: Int,
    @ColumnInfo(name = "oa_id") val oaId: Int
)

/** Asociación asignación-actividad (`asignacion_actividad`). */
@Entity(
    tableName = "asignacion_actividad",
    primaryKeys = ["asignacion_id", "actividad_id"],
    foreignKeys = [
        ForeignKey(
            entity = AssignmentEntity::class,
            parentColumns = ["id"],
            childColumns = ["asignacion_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = LearningUnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["unidad_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["actividad_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AssignmentEntity::class,
            parentColumns = ["id", "unidad_id"],
            childColumns = ["asignacion_id", "unidad_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["id", "unidad_id"],
            childColumns = ["actividad_id", "unidad_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["unidad_id"]),
        Index(value = ["asignacion_id", "unidad_id"]),
        Index(value = ["actividad_id", "unidad_id"])
    ]
)
data class AssignmentActivityEntity(
    @ColumnInfo(name = "asignacion_id") val asignacionId: Int,
    @ColumnInfo(name = "unidad_id") val unidadId: Int,
    @ColumnInfo(name = "actividad_id") val actividadId: Int
)
