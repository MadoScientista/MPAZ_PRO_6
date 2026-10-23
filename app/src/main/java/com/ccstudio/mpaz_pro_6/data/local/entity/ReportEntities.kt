package com.ccstudio.mpaz_pro_6.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ccstudio.mpaz_pro_6.data.enums.ObjectiveLevel
import com.ccstudio.mpaz_pro_6.data.enums.ReportStatus
import java.time.Instant

/** Informe pedagógico (`informe_pedagogico`). */
@Entity(
    tableName = "informe_pedagogico",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["estudiante_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = LearningUnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["unidad_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["docente_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["estudiante_id", "unidad_id"], unique = true),
        Index(value = ["unidad_id"]),
        Index(value = ["docente_id"])
    ]
)
data class PedagogicalReportEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "estudiante_id") val estudianteId: Int,
    @ColumnInfo(name = "unidad_id") val unidadId: Int,
    @ColumnInfo(name = "docente_id") val docenteId: Int,
    @ColumnInfo(name = "fecha_generacion") val fechaGeneracion: Instant,
    val estado: ReportStatus,
    @ColumnInfo(name = "fecha_validacion") val fechaValidacion: Instant?
)

/** Detalle por OA de un informe (`detalle_informe_oa`). */
@Entity(
    tableName = "detalle_informe_oa",
    foreignKeys = [
        ForeignKey(
            entity = PedagogicalReportEntity::class,
            parentColumns = ["id"],
            childColumns = ["informe_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = LearningObjectiveEntity::class,
            parentColumns = ["id"],
            childColumns = ["oa_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["informe_id", "oa_id"], unique = true),
        Index(value = ["oa_id"])
    ]
)
data class ReportObjectiveDetailEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "informe_id") val informeId: Int,
    @ColumnInfo(name = "oa_id") val oaId: Int,
    @ColumnInfo(name = "total_preguntas") val totalPreguntas: Int,
    val correctas: Int,
    val nivel: ObjectiveLevel
)
