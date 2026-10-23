package com.ccstudio.mpaz_pro_6.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant

/** Intento de un estudiante sobre una actividad (`intento`). */
@Entity(
    tableName = "intento",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["estudiante_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["actividad_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["estudiante_id"]),
        Index(value = ["actividad_id"]),
        Index(
            value = ["estudiante_id", "actividad_id", "numero"],
            unique = true
        )
    ]
)
data class AttemptEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "estudiante_id") val estudianteId: Int,
    @ColumnInfo(name = "actividad_id") val actividadId: Int,
    val numero: Int,
    @ColumnInfo(name = "fecha_inicio") val fechaInicio: Instant,
    @ColumnInfo(name = "fecha_fin") val fechaFin: Instant?
)

/** Respuesta de un estudiante (`respuesta_estudiante`). */
@Entity(
    tableName = "respuesta_estudiante",
    foreignKeys = [
        ForeignKey(
            entity = AttemptEntity::class,
            parentColumns = ["id"],
            childColumns = ["intento_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["pregunta_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AlternativeEntity::class,
            parentColumns = ["id"],
            childColumns = ["alternativa_id"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = AlternativeEntity::class,
            parentColumns = ["pregunta_id", "id"],
            childColumns = ["pregunta_id", "alternativa_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["intento_id", "pregunta_id"], unique = true),
        Index(value = ["pregunta_id"]),
        Index(value = ["alternativa_id"]),
        Index(value = ["pregunta_id", "alternativa_id"])
    ]
)
data class StudentAnswerEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "intento_id") val intentoId: Int,
    @ColumnInfo(name = "pregunta_id") val preguntaId: Int,
    @ColumnInfo(name = "alternativa_id") val alternativaId: Int,
    @ColumnInfo(name = "es_correcta") val esCorrecta: Boolean
)
