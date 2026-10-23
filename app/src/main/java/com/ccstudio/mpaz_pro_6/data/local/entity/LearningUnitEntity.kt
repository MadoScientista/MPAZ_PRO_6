package com.ccstudio.mpaz_pro_6.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import java.time.Instant

/** Unidad de aprendizaje del backend (`unidad`). */
@Entity(
    tableName = "unidad",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["asignatura_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["autorizada_por_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["asignatura_id"]),
        Index(value = ["autorizada_por_id"])
    ]
)
data class LearningUnitEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "asignatura_id") val asignaturaId: Int,
    @ColumnInfo(name = "nivel_educativo", defaultValue = "'Sexto Basico'")
    val nivelEducativo: String,
    val titulo: String,
    val orden: Int,
    val estado: ContentStatus,
    @ColumnInfo(name = "autorizada_por_id") val autorizadaPorId: Int?,
    @ColumnInfo(name = "fecha_autorizacion") val fechaAutorizacion: Instant?
)
