package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ccstudio.mpaz_pro_6.data.local.entity.PedagogicalReportEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.ReportObjectiveDetailEntity

/** Consultas de informes pedagógicos y su detalle por objetivo. */
@Dao
interface ReportDao {

    @Query("SELECT * FROM informe_pedagogico ORDER BY id")
    suspend fun getReports(): List<PedagogicalReportEntity>

    @Query("SELECT * FROM informe_pedagogico WHERE estudiante_id = :studentId ORDER BY id")
    suspend fun getReportsByStudent(studentId: Int): List<PedagogicalReportEntity>

    @Query("SELECT * FROM informe_pedagogico WHERE unidad_id = :unitId ORDER BY id")
    suspend fun getReportsByUnit(unitId: Int): List<PedagogicalReportEntity>

    @Query("SELECT * FROM informe_pedagogico WHERE id = :id")
    suspend fun getReportById(id: Int): PedagogicalReportEntity?

    @Upsert
    suspend fun upsertReport(entity: PedagogicalReportEntity)

    @Upsert
    suspend fun upsertReports(entities: List<PedagogicalReportEntity>)

    @Query("DELETE FROM informe_pedagogico")
    suspend fun deleteAllReports()

    @Query("SELECT * FROM detalle_informe_oa ORDER BY informe_id, oa_id")
    suspend fun getReportDetails(): List<ReportObjectiveDetailEntity>

    @Query("SELECT * FROM detalle_informe_oa WHERE informe_id = :reportId ORDER BY oa_id")
    suspend fun getReportDetailsByReport(reportId: Int): List<ReportObjectiveDetailEntity>

    @Upsert
    suspend fun upsertReportDetail(entity: ReportObjectiveDetailEntity)

    @Upsert
    suspend fun upsertReportDetails(entities: List<ReportObjectiveDetailEntity>)

    @Query("DELETE FROM detalle_informe_oa")
    suspend fun deleteAllReportDetails()
}
