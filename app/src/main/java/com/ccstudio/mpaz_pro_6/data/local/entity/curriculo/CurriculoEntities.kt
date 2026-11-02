package com.ccstudio.mpaz_pro_6.data.local.entity.curriculo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "asignatura")
data class AsignaturaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "asignatura_id")
    val id: Long = 0,
    @ColumnInfo(name = "nombre")
    val nombre: String
)

@Entity(tableName = "estado_autorizacion")
data class EstadoAutorizacionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "estado_id")
    val id: Long = 0,
    @ColumnInfo(name = "nombre")
    val nombre: String
)

@Entity(tableName = "tipo_actividad")
data class TipoActividadEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tipo_actividad_id")
    val id: Long = 0,
    @ColumnInfo(name = "nombre")
    val nombre: String
)

@Entity(tableName = "tipo_recurso")
data class TipoRecursoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tipo_recurso_id")
    val id: Long = 0,
    @ColumnInfo(name = "nombre")
    val nombre: String
)

@Entity(
    tableName = "unidad",
    indices = [
        Index(value = ["asignatura_id"]),
        Index(value = ["estado_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = AsignaturaEntity::class,
            parentColumns = ["asignatura_id"],
            childColumns = ["asignatura_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = EstadoAutorizacionEntity::class,
            parentColumns = ["estado_id"],
            childColumns = ["estado_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class UnidadEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "unidad_id")
    val id: Long = 0,
    @ColumnInfo(name = "nombre")
    val nombre: String,
    @ColumnInfo(name = "asignatura_id")
    val asignaturaId: Long,
    @ColumnInfo(name = "estado_id")
    val estadoId: Long
)

@Entity(tableName = "objetivo_aprendizaje")
data class ObjetivoAprendizajeEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "objetivo_aprendizaje_id")
    val id: Long = 0,
    @ColumnInfo(name = "codigo")
    val codigo: String,
    @ColumnInfo(name = "descripcion")
    val descripcion: String
)

@Entity(
    tableName = "objetivo_aprendizaje_unidad",
    indices = [
        Index(value = ["objetivo_aprendizaje_id", "unidad_id"], unique = true),
        Index(value = ["objetivo_aprendizaje_id"]),
        Index(value = ["unidad_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = ObjetivoAprendizajeEntity::class,
            parentColumns = ["objetivo_aprendizaje_id"],
            childColumns = ["objetivo_aprendizaje_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = UnidadEntity::class,
            parentColumns = ["unidad_id"],
            childColumns = ["unidad_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class ObjetivoAprendizajeUnidadEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "objetivo_aprendizaje_id")
    val objetivoAprendizajeId: Long,
    @ColumnInfo(name = "unidad_id")
    val unidadId: Long
)

@Entity(
    tableName = "contenido",
    indices = [
        Index(value = ["unidad_id"]),
        Index(value = ["estado_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UnidadEntity::class,
            parentColumns = ["unidad_id"],
            childColumns = ["unidad_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = EstadoAutorizacionEntity::class,
            parentColumns = ["estado_id"],
            childColumns = ["estado_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class ContenidoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "contenido_id")
    val id: Long = 0,
    @ColumnInfo(name = "descripcion")
    val descripcion: String,
    @ColumnInfo(name = "unidad_id")
    val unidadId: Long,
    @ColumnInfo(name = "estado_id")
    val estadoId: Long
)

@Entity(
    tableName = "recurso",
    indices = [
        Index(value = ["contenido_id"]),
        Index(value = ["tipo_recurso_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = ContenidoEntity::class,
            parentColumns = ["contenido_id"],
            childColumns = ["contenido_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = TipoRecursoEntity::class,
            parentColumns = ["tipo_recurso_id"],
            childColumns = ["tipo_recurso_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class RecursoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "recurso_id")
    val id: Long = 0,
    @ColumnInfo(name = "nombre")
    val nombre: String,
    @ColumnInfo(name = "url")
    val url: String? = null,
    @ColumnInfo(name = "contenido_id")
    val contenidoId: Long,
    @ColumnInfo(name = "tipo_recurso_id")
    val tipoRecursoId: Long
)

@Entity(
    tableName = "actividad",
    indices = [
        Index(value = ["contenido_id"]),
        Index(value = ["tipo_actividad_id"]),
        Index(value = ["estado_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = ContenidoEntity::class,
            parentColumns = ["contenido_id"],
            childColumns = ["contenido_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = TipoActividadEntity::class,
            parentColumns = ["tipo_actividad_id"],
            childColumns = ["tipo_actividad_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = EstadoAutorizacionEntity::class,
            parentColumns = ["estado_id"],
            childColumns = ["estado_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class ActividadEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "actividad_id")
    val id: Long = 0,
    @ColumnInfo(name = "instrucciones")
    val instrucciones: String,
    @ColumnInfo(name = "habilitada")
    val habilitada: Boolean = true,
    @ColumnInfo(name = "contenido_id")
    val contenidoId: Long,
    @ColumnInfo(name = "tipo_actividad_id")
    val tipoActividadId: Long,
    @ColumnInfo(name = "estado_id")
    val estadoId: Long
)

@Entity(
    tableName = "pregunta",
    indices = [
        Index(value = ["recurso_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = RecursoEntity::class,
            parentColumns = ["recurso_id"],
            childColumns = ["recurso_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class PreguntaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pregunta_id")
    val id: Long = 0,
    @ColumnInfo(name = "enunciado")
    val enunciado: String,
    @ColumnInfo(name = "recurso_id")
    val recursoId: Long? = null
)

@Entity(
    tableName = "alternativa",
    indices = [
        Index(value = ["recurso_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = RecursoEntity::class,
            parentColumns = ["recurso_id"],
            childColumns = ["recurso_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class AlternativaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "alternativa_id")
    val id: Long = 0,
    @ColumnInfo(name = "enunciado")
    val enunciado: String,
    @ColumnInfo(name = "correcta")
    val correcta: Boolean = false,
    @ColumnInfo(name = "recurso_id")
    val recursoId: Long? = null
)

@Entity(
    tableName = "alternativa_pregunta",
    indices = [
        Index(value = ["pregunta_id", "alternativa_id"], unique = true),
        Index(value = ["pregunta_id"]),
        Index(value = ["alternativa_id"])
    ],
    foreignKeys = [
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
data class AlternativaPreguntaEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "pregunta_id")
    val preguntaId: Long,
    @ColumnInfo(name = "alternativa_id")
    val alternativaId: Long
)

@Entity(
    tableName = "pregunta_actividad",
    indices = [
        Index(value = ["actividad_id", "pregunta_id"], unique = true),
        Index(value = ["actividad_id"]),
        Index(value = ["pregunta_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = ActividadEntity::class,
            parentColumns = ["actividad_id"],
            childColumns = ["actividad_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = PreguntaEntity::class,
            parentColumns = ["pregunta_id"],
            childColumns = ["pregunta_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class PreguntaActividadEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "actividad_id")
    val actividadId: Long,
    @ColumnInfo(name = "pregunta_id")
    val preguntaId: Long
)

@Entity(
    tableName = "objetivo_aprendizaje_contenido",
    indices = [
        Index(value = ["contenido_id", "objetivo_aprendizaje_id"], unique = true),
        Index(value = ["contenido_id"]),
        Index(value = ["objetivo_aprendizaje_id"]),
        Index(value = ["unidad_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = ContenidoEntity::class,
            parentColumns = ["contenido_id"],
            childColumns = ["contenido_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = ObjetivoAprendizajeEntity::class,
            parentColumns = ["objetivo_aprendizaje_id"],
            childColumns = ["objetivo_aprendizaje_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = UnidadEntity::class,
            parentColumns = ["unidad_id"],
            childColumns = ["unidad_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class ObjetivoAprendizajeContenidoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "contenido_id")
    val contenidoId: Long,
    @ColumnInfo(name = "objetivo_aprendizaje_id")
    val objetivoAprendizajeId: Long,
    @ColumnInfo(name = "unidad_id")
    val unidadId: Long
)
