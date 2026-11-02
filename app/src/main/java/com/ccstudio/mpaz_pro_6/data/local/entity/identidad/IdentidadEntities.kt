package com.ccstudio.mpaz_pro_6.data.local.entity.identidad

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "rol")
data class RolEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rol_id")
    val id: Long = 0,
    @ColumnInfo(name = "nombre")
    val nombre: String
)

@Entity(tableName = "nivel")
data class NivelEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "nivel_id")
    val id: Long = 0,
    @ColumnInfo(name = "nombre")
    val nombre: String
)

@Entity(
    tableName = "seccion",
    indices = [
        Index(value = ["letra"], unique = true)
    ]
)
data class SeccionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "seccion_id")
    val id: Long = 0,
    @ColumnInfo(name = "letra")
    val letra: String
)

@Entity(
    tableName = "curso",
    indices = [
        Index(value = ["annio", "nivel_id", "seccion_id"], unique = true),
        Index(value = ["nivel_id"]),
        Index(value = ["seccion_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = NivelEntity::class,
            parentColumns = ["nivel_id"],
            childColumns = ["nivel_id"],
            onDelete = ForeignKey.NO_ACTION
        ),
        ForeignKey(
            entity = SeccionEntity::class,
            parentColumns = ["seccion_id"],
            childColumns = ["seccion_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class CursoEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "curso_id")
    val id: Long = 0,
    @ColumnInfo(name = "annio")
    val annio: Int,
    @ColumnInfo(name = "nivel_id")
    val nivelId: Long,
    @ColumnInfo(name = "seccion_id")
    val seccionId: Long
)

@Entity(
    tableName = "usuario",
    indices = [
        Index(value = ["nombre_usuario"], unique = true),
        Index(value = ["rol_id"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = RolEntity::class,
            parentColumns = ["rol_id"],
            childColumns = ["rol_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "usuario_id")
    val id: Long = 0,
    @ColumnInfo(name = "nombre_usuario")
    val nombreUsuario: String,
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,
    @ColumnInfo(name = "pnombre")
    val pnombre: String,
    @ColumnInfo(name = "snombre")
    val snombre: String? = null,
    @ColumnInfo(name = "apaterno")
    val apaterno: String,
    @ColumnInfo(name = "amaterno")
    val amaterno: String? = null,
    @ColumnInfo(name = "rol_id")
    val rolId: Long,
    @ColumnInfo(name = "activo")
    val activo: Boolean = true
) {
    val nombreCompleto: String
        get() = listOfNotNull(pnombre, snombre, apaterno, amaterno)
            .filter { it.isNotBlank() }
            .joinToString(" ")
}
