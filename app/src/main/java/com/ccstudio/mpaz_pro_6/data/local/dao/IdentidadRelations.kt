package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Embedded
import androidx.room.Relation
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.CursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.NivelEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.SeccionEntity

data class CursoConDetalles(
    @Embedded val curso: CursoEntity,
    @Relation(
        parentColumn = "nivel_id",
        entityColumn = "nivel_id"
    )
    val nivel: NivelEntity,
    @Relation(
        parentColumn = "seccion_id",
        entityColumn = "seccion_id"
    )
    val seccion: SeccionEntity
) {
    val nombreCurso: String
        get() = "${nivel.nombre} ${seccion.letra}"

    val nombreCompletoConAnnio: String
        get() = "${nivel.nombre} ${seccion.letra} (${curso.annio})"
}
