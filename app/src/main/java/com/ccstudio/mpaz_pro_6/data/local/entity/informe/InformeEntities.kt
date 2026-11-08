package com.ccstudio.mpaz_pro_6.data.local.entity.informe

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ObjetivoAprendizajeEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.UnidadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.UsuarioEntity

@Entity(
    tableName = "informe_pedagogico",
    indices = [
        Index(value = ["usuario_id"]),
        Index(value = ["unidad_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["usuario_id"],
            childColumns = ["usuario_id"],
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
data class InformePedagogicoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "informe_pedagogico_id")
    val id: Long = 0,
    @ColumnInfo(name = "usuario_id")
    val usuarioId: Long,
    @ColumnInfo(name = "unidad_id")
    val unidadId: Long,
    @ColumnInfo(name = "fecha")
    val fecha: String,
    @ColumnInfo(name = "descripcion")
    val descripcion: String? = null,
    @ColumnInfo(name = "fecha_corte")
    val fechaCorte: String = "",
    @ColumnInfo(name = "origen")
    val origen: String = "MANUAL"
)

@Entity(
    tableName = "detalle_informe_pedagogico",
    indices = [
        Index(value = ["informe_pedagogico_id", "objetivo_aprendizaje_id"], unique = true),
        Index(value = ["informe_pedagogico_id"]),
        Index(value = ["objetivo_aprendizaje_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = InformePedagogicoEntity::class,
            parentColumns = ["informe_pedagogico_id"],
            childColumns = ["informe_pedagogico_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = ObjetivoAprendizajeEntity::class,
            parentColumns = ["objetivo_aprendizaje_id"],
            childColumns = ["objetivo_aprendizaje_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class DetalleInformePedagogicoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "informe_pedagogico_id")
    val informePedagogicoId: Long,
    @ColumnInfo(name = "objetivo_aprendizaje_id")
    val objetivoAprendizajeId: Long,
    @ColumnInfo(name = "correctas")
    val correctas: Int = 0,
    @ColumnInfo(name = "incorrectas")
    val incorrectas: Int = 0
)
