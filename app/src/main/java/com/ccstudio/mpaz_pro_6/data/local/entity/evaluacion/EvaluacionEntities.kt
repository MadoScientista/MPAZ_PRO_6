package com.ccstudio.mpaz_pro_6.data.local.entity.evaluacion

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AlternativaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.PreguntaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.ActividadCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity

@Entity(
    tableName = "intento",
    indices = [
        Index(value = ["usuario_id"]),
        Index(value = ["actividad_curso_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["usuario_id"],
            childColumns = ["usuario_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = ActividadCursoEntity::class,
            parentColumns = ["actividad_curso_id"],
            childColumns = ["actividad_curso_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class IntentoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "intento_id")
    val id: Long = 0,
    @ColumnInfo(name = "usuario_id")
    val usuarioId: Long,
    @ColumnInfo(name = "actividad_curso_id")
    val actividadCursoId: Long,
    @ColumnInfo(name = "fecha_inicio")
    val fechaInicio: String,
    @ColumnInfo(name = "fecha_fin")
    val fechaFin: String? = null
)

@Entity(
    tableName = "respuesta",
    indices = [
        Index(value = ["intento_id", "pregunta_id", "alternativa_id"], unique = true),
        Index(value = ["intento_id"]),
        Index(value = ["pregunta_id"]),
        Index(value = ["alternativa_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = IntentoEntity::class,
            parentColumns = ["intento_id"],
            childColumns = ["intento_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = PreguntaEntity::class,
            parentColumns = ["pregunta_id"],
            childColumns = ["pregunta_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = AlternativaEntity::class,
            parentColumns = ["alternativa_id"],
            childColumns = ["alternativa_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class RespuestaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "respuesta_id")
    val id: Long = 0,
    @ColumnInfo(name = "intento_id")
    val intentoId: Long,
    @ColumnInfo(name = "pregunta_id")
    val preguntaId: Long,
    @ColumnInfo(name = "alternativa_id")
    val alternativaId: Long
)
