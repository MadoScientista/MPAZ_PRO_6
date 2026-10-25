package com.ccstudio.mpaz_pro_6.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** Asignatura del backend (`asignatura`). */
@Entity(
    tableName = "asignatura",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class SubjectEntity(
    @PrimaryKey val id: Int,
    val nombre: String
)
