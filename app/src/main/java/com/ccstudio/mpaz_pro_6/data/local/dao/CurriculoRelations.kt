package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.ActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AlternativaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AlternativaPreguntaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.AsignaturaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.EstadoAutorizacionEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.PreguntaEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.TipoActividadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.curriculo.UnidadEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.docencia.ActividadCursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.CursoEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.identidad.NivelEntity

data class AsignaturaConNivel(
    @Embedded val asignatura: AsignaturaEntity,
    @Relation(
        parentColumn = "nivel_id",
        entityColumn = "nivel_id"
    )
    val nivel: NivelEntity
)

data class UnidadConDetalles(
    @Embedded val unidad: UnidadEntity,
    @Relation(
        parentColumn = "asignatura_id",
        entityColumn = "asignatura_id"
    )
    val asignatura: AsignaturaEntity,
    @Relation(
        parentColumn = "estado_id",
        entityColumn = "estado_id"
    )
    val estado: EstadoAutorizacionEntity
)

data class ActividadConTipo(
    @Embedded val actividad: ActividadEntity,
    @Relation(
        parentColumn = "tipo_actividad_id",
        entityColumn = "tipo_actividad_id"
    )
    val tipoActividad: TipoActividadEntity,
    @Relation(
        parentColumn = "estado_id",
        entityColumn = "estado_id"
    )
    val estado: EstadoAutorizacionEntity
)

data class ActividadCursoConDetalles(
    @Embedded val actividadCurso: ActividadCursoEntity,
    @Relation(
        parentColumn = "actividad_id",
        entityColumn = "actividad_id"
    )
    val actividad: ActividadEntity,
    @Relation(
        parentColumn = "curso_id",
        entityColumn = "curso_id"
    )
    val curso: CursoEntity
)

data class PreguntaConAlternativas(
    @Embedded val pregunta: PreguntaEntity,
    @Relation(
        parentColumn = "pregunta_id",
        entityColumn = "alternativa_id",
        associateBy = Junction(
            value = AlternativaPreguntaEntity::class,
            parentColumn = "pregunta_id",
            entityColumn = "alternativa_id"
        )
    )
    val alternativas: List<AlternativaEntity>
)
