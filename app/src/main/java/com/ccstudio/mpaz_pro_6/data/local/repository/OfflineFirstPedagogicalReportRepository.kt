package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.local.dao.ReportDao
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import com.ccstudio.mpaz_pro_6.data.model.ReportObjectiveDetail
import com.ccstudio.mpaz_pro_6.data.model.requests.CreatePedagogicalReportRequest
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDto
import com.ccstudio.mpaz_pro_6.data.repository.PedagogicalReportRepository

/** Repositorio de informes pedagógicos con respaldo en Room. */
class OfflineFirstPedagogicalReportRepository(
    private val api: MpazApi,
    private val dao: ReportDao
) : PedagogicalReportRepository {

    override suspend fun list(
        studentId: Int?,
        unitId: Int?
    ): Result<List<PedagogicalReport>> =
        apiCall { api.getReports(studentId, unitId).map { it.toDomain() } }.fold(
            onSuccess = { reports ->
                dao.upsertReports(reports.map { it.toEntity() })
                Result.success(reports)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    val entities = when {
                        studentId != null -> dao.getReportsByStudent(studentId)
                        unitId != null -> dao.getReportsByUnit(unitId)
                        else -> dao.getReports()
                    }
                    entities
                        .filter { studentId == null || it.estudianteId == studentId }
                        .filter { unitId == null || it.unidadId == unitId }
                        .map { it.toDomain() }
                        .takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun getById(id: Int): Result<PedagogicalReport> =
        apiCall { api.getReport(id).toDomain() }.fold(
            onSuccess = { report ->
                dao.upsertReport(report.toEntity())
                Result.success(report)
            },
            onFailure = { error ->
                cachedValueOrError(error) { dao.getReportById(id)?.toDomain() }
            }
        )

    override suspend fun generate(
        request: CreatePedagogicalReportRequest
    ): Result<PedagogicalReport> =
        apiCall { api.generateReport(request.toDto()).toDomain() }.fold(
            onSuccess = { report ->
                dao.upsertReport(report.toEntity())
                Result.success(report)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun validate(id: Int): Result<PedagogicalReport> =
        apiCall { api.validateReport(id).toDomain() }.fold(
            onSuccess = { report ->
                dao.upsertReport(report.toEntity())
                Result.success(report)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun listObjectiveDetails(
        reportId: Int
    ): Result<List<ReportObjectiveDetail>> =
        apiCall { api.getReportDetails(reportId).map { it.toDomain() } }.fold(
            onSuccess = { details ->
                dao.upsertReportDetails(details.map { it.toEntity(reportId) })
                Result.success(details)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    dao.getReportDetailsByReport(reportId)
                        .map { it.toDomain() }
                        .takeIf { it.isNotEmpty() }
                }
            }
        )
}
