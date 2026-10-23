package com.ccstudio.mpaz_pro_6.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ccstudio.mpaz_pro_6.data.enums.UserRole

/**
 * Usuario del backend (`usuario`).
 *
 * No incluye `password_hash`: el login es remoto y la app nunca necesita hashes
 * ni contraseñas en la caché local.
 */
@Entity(
    tableName = "usuario",
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["curso_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["curso_id"])
    ]
)
data class UserEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val username: String,
    val rol: UserRole,
    @ColumnInfo(name = "curso_id") val cursoId: Int?
)
