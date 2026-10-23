package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.error.requireSuccess
import com.ccstudio.mpaz_pro_6.data.local.dao.CurriculumDao
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateContentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateContentRequest
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDto
import com.ccstudio.mpaz_pro_6.data.repository.ContentRepository

/** Repositorio de contenidos con respaldo en Room. */
class OfflineFirstContentRepository(
    private val api: MpazApi,
    private val dao: CurriculumDao
) : ContentRepository {

    override suspend fun list(learningObjectiveId: Int?): Result<List<Content>> =
        apiCall { api.getContents(learningObjectiveId).map { it.toDomain() } }.fold(
            onSuccess = { contents ->
                dao.upsertContents(contents.map { it.toEntity() })
                Result.success(contents)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    val entities = if (learningObjectiveId == null) {
                        dao.getContents()
                    } else {
                        dao.getContentsByObjective(learningObjectiveId)
                    }
                    entities.map { it.toDomain() }.takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun getById(id: Int): Result<Content> =
        apiCall { api.getContent(id).toDomain() }.fold(
            onSuccess = { content ->
                dao.upsertContent(content.toEntity())
                Result.success(content)
            },
            onFailure = { error ->
                cachedValueOrError(error) { dao.getContentById(id)?.toDomain() }
            }
        )

    override suspend fun create(request: CreateContentRequest): Result<Content> =
        apiCall { api.createContent(request.toDto()).toDomain() }.fold(
            onSuccess = { content ->
                dao.upsertContent(content.toEntity())
                Result.success(content)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun update(id: Int, request: UpdateContentRequest): Result<Content> =
        apiCall { api.updateContent(id, request.toDto()).toDomain() }.fold(
            onSuccess = { content ->
                dao.upsertContent(content.toEntity())
                Result.success(content)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun delete(id: Int): Result<Unit> =
        apiCall {
            api.deleteContent(id).requireSuccess()
            dao.deleteContentCascade(id)
        }
}
