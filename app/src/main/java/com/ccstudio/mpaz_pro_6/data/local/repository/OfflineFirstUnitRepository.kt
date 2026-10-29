package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.error.requireSuccess
import com.ccstudio.mpaz_pro_6.data.local.dao.UnitDao
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateLearningUnitRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateLearningUnitRequest
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDto
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository

/** Repositorio de unidades de aprendizaje con respaldo en la caché Room. */
class OfflineFirstUnitRepository(
    private val api: MpazApi,
    private val dao: UnitDao
) : UnitRepository {

    override suspend fun list(subjectId: Int?): Result<List<LearningUnit>> =
        apiCall { api.getUnits(subjectId).map { it.toDomain() } }.fold(
            onSuccess = { units ->
                dao.upsertUnits(units.map { it.toEntity() })
                Result.success(units)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    val entities = if (subjectId == null) {
                        dao.getUnits()
                    } else {
                        dao.getUnitsBySubject(subjectId)
                    }
                    entities.map { it.toDomain() }.takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun getById(id: Int): Result<LearningUnit> =
        apiCall { api.getUnit(id).toDomain() }.fold(
            onSuccess = { unit ->
                dao.upsertUnit(unit.toEntity())
                Result.success(unit)
            },
            onFailure = { error ->
                cachedValueOrError(error) { dao.getUnitById(id)?.toDomain() }
            }
        )

    override suspend fun create(
        request: CreateLearningUnitRequest
    ): Result<LearningUnit> =
        apiCall { api.createUnit(request.toDto()).toDomain() }.fold(
            onSuccess = { unit ->
                dao.upsertUnit(unit.toEntity())
                Result.success(unit)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun update(
        id: Int,
        request: UpdateLearningUnitRequest
    ): Result<LearningUnit> =
        apiCall { api.updateUnit(id, request.toDto()).toDomain() }.fold(
            onSuccess = { unit ->
                dao.upsertUnit(unit.toEntity())
                if (request.estado != null) {
                    dao.updateContentsStatusByUnit(unit.id, unit.estado)
                    dao.updateResourcesStatusByUnit(unit.id, unit.estado)
                }
                Result.success(unit)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun delete(id: Int): Result<Unit> =
        apiCall {
            api.deleteUnit(id).requireSuccess()
            dao.deleteUnitById(id)
        }
}
