package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.error.requireSuccess
import com.ccstudio.mpaz_pro_6.data.local.dao.CurriculumDao
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateLearningObjectiveRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateLearningObjectiveRequest
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDto
import com.ccstudio.mpaz_pro_6.data.repository.LearningObjectiveRepository

/** Repositorio de objetivos de aprendizaje con respaldo en Room. */
class OfflineFirstLearningObjectiveRepository(
    private val api: MpazApi,
    private val dao: CurriculumDao
) : LearningObjectiveRepository {

    override suspend fun list(unitId: Int?): Result<List<LearningObjective>> =
        apiCall { api.getObjectives(unitId).map { it.toDomain() } }.fold(
            onSuccess = { objectives ->
                dao.upsertObjectives(objectives.map { it.toEntity() })
                Result.success(objectives)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    val entities = if (unitId == null) {
                        dao.getObjectives()
                    } else {
                        dao.getObjectivesByUnit(unitId)
                    }
                    entities.map { it.toDomain() }.takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun getById(id: Int): Result<LearningObjective> =
        apiCall { api.getObjective(id).toDomain() }.fold(
            onSuccess = { objective ->
                dao.upsertObjective(objective.toEntity())
                Result.success(objective)
            },
            onFailure = { error ->
                cachedValueOrError(error) { dao.getObjectiveById(id)?.toDomain() }
            }
        )

    override suspend fun create(
        request: CreateLearningObjectiveRequest
    ): Result<LearningObjective> =
        apiCall { api.createObjective(request.toDto()).toDomain() }.fold(
            onSuccess = { objective ->
                dao.upsertObjective(objective.toEntity())
                Result.success(objective)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun update(
        id: Int,
        request: UpdateLearningObjectiveRequest
    ): Result<LearningObjective> =
        apiCall { api.updateObjective(id, request.toDto()).toDomain() }.fold(
            onSuccess = { objective ->
                dao.upsertObjective(objective.toEntity())
                Result.success(objective)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun delete(id: Int): Result<Unit> =
        apiCall {
            api.deleteObjective(id).requireSuccess()
            dao.deleteObjectiveById(id)
        }
}
