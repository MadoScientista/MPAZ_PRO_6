package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.error.requireSuccess
import com.ccstudio.mpaz_pro_6.data.local.dao.CurriculumDao
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.Alternative
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateAlternativeRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateAlternativeRequest
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDto
import com.ccstudio.mpaz_pro_6.data.repository.AlternativeRepository

/** Repositorio de alternativas con respaldo en Room. */
class OfflineFirstAlternativeRepository(
    private val api: MpazApi,
    private val dao: CurriculumDao
) : AlternativeRepository {

    override suspend fun list(questionId: Int?): Result<List<Alternative>> =
        apiCall { api.getAlternatives(questionId).map { it.toDomain() } }.fold(
            onSuccess = { alternatives ->
                dao.upsertAlternatives(alternatives.map { it.toEntity() })
                Result.success(alternatives)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    val entities = if (questionId == null) {
                        dao.getAlternatives()
                    } else {
                        dao.getAlternativesByQuestion(questionId)
                    }
                    entities.map { it.toDomain() }.takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun getById(id: Int): Result<Alternative> =
        apiCall { api.getAlternative(id).toDomain() }.fold(
            onSuccess = { alternative ->
                dao.upsertAlternative(alternative.toEntity())
                Result.success(alternative)
            },
            onFailure = { error ->
                cachedValueOrError(error) { dao.getAlternativeById(id)?.toDomain() }
            }
        )

    override suspend fun create(
        request: CreateAlternativeRequest
    ): Result<Alternative> =
        apiCall { api.createAlternative(request.toDto()).toDomain() }.fold(
            onSuccess = { alternative ->
                dao.upsertAlternative(alternative.toEntity())
                Result.success(alternative)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun update(
        id: Int,
        request: UpdateAlternativeRequest
    ): Result<Alternative> =
        apiCall { api.updateAlternative(id, request.toDto()).toDomain() }.fold(
            onSuccess = { alternative ->
                dao.upsertAlternative(alternative.toEntity())
                Result.success(alternative)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun delete(id: Int): Result<Unit> =
        apiCall {
            api.deleteAlternative(id).requireSuccess()
            dao.deleteAlternativeById(id)
        }
}
