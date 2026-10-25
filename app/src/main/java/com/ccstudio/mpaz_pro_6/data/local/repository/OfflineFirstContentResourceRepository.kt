package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.error.requireSuccess
import com.ccstudio.mpaz_pro_6.data.local.dao.CurriculumDao
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.ContentResource
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateContentResourceRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateContentResourceRequest
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDto
import com.ccstudio.mpaz_pro_6.data.repository.ContentResourceRepository

/** Repositorio de recursos de contenido con respaldo en Room. */
class OfflineFirstContentResourceRepository(
    private val api: MpazApi,
    private val dao: CurriculumDao
) : ContentResourceRepository {

    override suspend fun list(contentId: Int?): Result<List<ContentResource>> =
        apiCall { api.getContentResources(contentId).map { it.toDomain() } }.fold(
            onSuccess = { resources ->
                dao.upsertContentResources(resources.map { it.toEntity() })
                Result.success(resources)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    val entities = if (contentId == null) {
                        dao.getContentResources()
                    } else {
                        dao.getContentResourcesByContent(contentId)
                    }
                    entities.map { it.toDomain() }.takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun getById(id: Int): Result<ContentResource> =
        apiCall { api.getContentResource(id).toDomain() }.fold(
            onSuccess = { resource ->
                dao.upsertContentResource(resource.toEntity())
                Result.success(resource)
            },
            onFailure = { error ->
                cachedValueOrError(error) { dao.getContentResourceById(id)?.toDomain() }
            }
        )

    override suspend fun create(
        request: CreateContentResourceRequest
    ): Result<ContentResource> =
        apiCall { api.createContentResource(request.toDto()).toDomain() }.fold(
            onSuccess = { resource ->
                dao.upsertContentResource(resource.toEntity())
                Result.success(resource)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun update(
        id: Int,
        request: UpdateContentResourceRequest
    ): Result<ContentResource> =
        apiCall { api.updateContentResource(id, request.toDto()).toDomain() }.fold(
            onSuccess = { resource ->
                dao.upsertContentResource(resource.toEntity())
                Result.success(resource)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun delete(id: Int): Result<Unit> =
        apiCall {
            api.deleteContentResource(id).requireSuccess()
            dao.deleteContentResourceById(id)
        }
}
