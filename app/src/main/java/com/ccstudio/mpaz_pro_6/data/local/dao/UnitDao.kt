package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.local.entity.LearningUnitEntity

/** Consultas de unidades de aprendizaje. */
@Dao
interface UnitDao {

    @Query("SELECT * FROM unidad ORDER BY asignatura_id, orden")
    suspend fun getUnits(): List<LearningUnitEntity>

    @Query("SELECT * FROM unidad WHERE asignatura_id = :subjectId ORDER BY orden")
    suspend fun getUnitsBySubject(subjectId: Int): List<LearningUnitEntity>

    @Query("SELECT * FROM unidad WHERE id = :id")
    suspend fun getUnitById(id: Int): LearningUnitEntity?

    @Upsert
    suspend fun upsertUnit(entity: LearningUnitEntity)

    @Upsert
    suspend fun upsertUnits(entities: List<LearningUnitEntity>)

    @Query("UPDATE contenido SET estado = :status WHERE unidad_id = :unitId")
    suspend fun updateContentsStatusByUnit(unitId: Int, status: ContentStatus)

    @Query("UPDATE recurso_contenido SET estado = :status WHERE contenido_id IN (SELECT id FROM contenido WHERE unidad_id = :unitId)")
    suspend fun updateResourcesStatusByUnit(unitId: Int, status: ContentStatus)

    @Query("DELETE FROM unidad")
    suspend fun deleteAllUnits()

    @Query("DELETE FROM unidad WHERE id = :id")
    suspend fun deleteUnitById(id: Int)
}
