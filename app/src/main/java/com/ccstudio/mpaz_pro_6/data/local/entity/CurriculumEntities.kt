package com.ccstudio.mpaz_pro_6.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.enums.ContentOrigin
import com.ccstudio.mpaz_pro_6.data.enums.ContentResourceType
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.enums.Difficulty

/** Objetivo de aprendizaje del backend (`objetivo_aprendizaje`). */
@Entity(
    tableName = "objetivo_aprendizaje",
    foreignKeys = [
        ForeignKey(
            entity = LearningUnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["unidad_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["unidad_id"]),
        Index(value = ["id", "unidad_id"], unique = true),
        Index(value = ["unidad_id", "codigo"], unique = true)
    ]
)
data class LearningObjectiveEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "unidad_id") val unidadId: Int,
    val codigo: String?,
    val descripcion: String,
    val eje: String,
    @ColumnInfo(name = "texto_referencia") val textoReferencia: String?
)

/** Contenido del backend (`contenido`). */
@Entity(
    tableName = "contenido",
    foreignKeys = [
        ForeignKey(
            entity = LearningObjectiveEntity::class,
            parentColumns = ["id"],
            childColumns = ["oa_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["oa_id"])]
)
data class ContentEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "oa_id") val oaId: Int,
    val titulo: String,
    val explicacion: String,
    val ejemplos: String?,
    val instrucciones: String?,
    val orden: Int,
    val origen: ContentOrigin,
    @ColumnInfo(defaultValue = "'BORRADOR'") val estado: ContentStatus
)

/** Recurso de contenido del backend (`recurso_contenido`). */
@Entity(
    tableName = "recurso_contenido",
    foreignKeys = [
        ForeignKey(
            entity = ContentEntity::class,
            parentColumns = ["id"],
            childColumns = ["contenido_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["contenido_id"])]
)
data class ContentResourceEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "contenido_id") val contenidoId: Int,
    val tipo: ContentResourceType,
    val url: String,
    val descripcion: String?,
    val origen: ContentOrigin,
    @ColumnInfo(defaultValue = "'BORRADOR'") val estado: ContentStatus
)

/** Actividad del backend (`actividad`). */
@Entity(
    tableName = "actividad",
    foreignKeys = [
        ForeignKey(
            entity = LearningUnitEntity::class,
            parentColumns = ["id"],
            childColumns = ["unidad_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["unidad_id"]),
        Index(value = ["id", "unidad_id"], unique = true),
        Index(value = ["unidad_id", "titulo"], unique = true)
    ]
)
data class ActivityEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "unidad_id") val unidadId: Int,
    val titulo: String,
    val tipo: ActivityType,
    val orden: Int
)

/** Pregunta del backend (`pregunta`). */
@Entity(
    tableName = "pregunta",
    foreignKeys = [
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["actividad_id"],
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
            entity = ActivityEntity::class,
            parentColumns = ["id", "unidad_id"],
            childColumns = ["actividad_id", "unidad_id"],
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
        Index(value = ["actividad_id"]),
        Index(value = ["unidad_id"]),
        Index(value = ["oa_id"]),
        Index(value = ["actividad_id", "unidad_id"]),
        Index(value = ["oa_id", "unidad_id"])
    ]
)
data class QuestionEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "actividad_id") val actividadId: Int,
    @ColumnInfo(name = "unidad_id") val unidadId: Int,
    @ColumnInfo(name = "oa_id") val oaId: Int,
    val enunciado: String,
    @ColumnInfo(name = "criterio_respuesta") val criterioRespuesta: String?,
    val dificultad: Difficulty,
    val orden: Int
)

/** Alternativa del backend (`alternativa`). */
@Entity(
    tableName = "alternativa",
    foreignKeys = [
        ForeignKey(
            entity = QuestionEntity::class,
            parentColumns = ["id"],
            childColumns = ["pregunta_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["pregunta_id"]),
        Index(value = ["pregunta_id", "id"], unique = true)
    ]
)
data class AlternativeEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "pregunta_id") val preguntaId: Int,
    val texto: String,
    @ColumnInfo(name = "es_correcta") val esCorrecta: Boolean
)
