package com.ccstudio.mpaz_pro_6.data.local.entity.docencia

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AsignaturaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.CursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity

@Entity(
    tableName = "asignatura_curso",
    indices = [
        Index(value = ["curso_id", "asignatura_id"], unique = true),
        Index(value = ["curso_id"]),
        Index(value = ["asignatura_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = CursoEntity::class,
            parentColumns = ["curso_id"],
            childColumns = ["curso_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = AsignaturaEntity::class,
            parentColumns = ["asignatura_id"],
            childColumns = ["asignatura_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class AsignaturaCursoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "curso_id")
    val cursoId: Long,
    @ColumnInfo(name = "asignatura_id")
    val asignaturaId: Long
)

@Entity(
    tableName = "usuario_curso",
    indices = [
        Index(value = ["usuario_id", "curso_id"], unique = true),
        Index(value = ["usuario_id"]),
        Index(value = ["curso_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["usuario_id"],
            childColumns = ["usuario_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = CursoEntity::class,
            parentColumns = ["curso_id"],
            childColumns = ["curso_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class UsuarioCursoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "usuario_id")
    val usuarioId: Long,
    @ColumnInfo(name = "curso_id")
    val cursoId: Long
)

@Entity(
    tableName = "actividad_curso",
    indices = [
        Index(value = ["actividad_id", "curso_id"], unique = true),
        Index(value = ["actividad_id"]),
        Index(value = ["curso_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ActividadEntity::class,
            parentColumns = ["actividad_id"],
            childColumns = ["actividad_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = CursoEntity::class,
            parentColumns = ["curso_id"],
            childColumns = ["curso_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class ActividadCursoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "actividad_curso_id")
    val id: Long = 0,
    @ColumnInfo(name = "actividad_id")
    val actividadId: Long,
    @ColumnInfo(name = "curso_id")
    val cursoId: Long,
    @ColumnInfo(name = "fecha_inicio")
    val fechaInicio: String? = null,
    @ColumnInfo(name = "fecha_fin")
    val fechaFin: String? = null,
    @ColumnInfo(name = "habilitada")
    val habilitada: Boolean = false,
    @ColumnInfo(name = "visible")
    val visible: Boolean = false,
    @ColumnInfo(name = "max_intentos")
    val maxIntentos: Int? = null
)
