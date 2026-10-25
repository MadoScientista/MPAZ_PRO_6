package com.ccstudio.mpaz_pro_6.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Curso del backend (`curso`). */
@Entity(
    tableName = "curso",
    indices = [Index(value = ["nombre", "anio"], unique = true)]
)
data class CourseEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val anio: Int
)
