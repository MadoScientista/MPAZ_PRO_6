package com.ccstudio.mpaz_pro_6.data.mock

import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.ContentResource
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateContentResourceRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateContentResourceRequest
import com.ccstudio.mpaz_pro_6.data.repository.ContentResourceRepository
import kotlinx.coroutines.sync.withLock

/**
 * Implementación en memoria de `ContentResourceRepository`.
 */
class MockContentResourceRepository(
    private val store: MockStore
) : ContentResourceRepository {

    override suspend fun list(contentId: Int?): Result<List<ContentResource>> =
        store.mutex.withLock {
            if (contentId != null && store.contents.none { it.id == contentId }) {
                return Result.failure(ApiError.NotFound("No existe el contenido con id $contentId."))
            }

            val resources = store.contentResources
                .filter { contentId == null || it.contenidoId == contentId }

            Result.success(resources)
        }

    override suspend fun getById(id: Int): Result<ContentResource> = store.mutex.withLock {
        val resource = store.contentResources.find { it.id == id }
        if (resource == null) {
            Result.failure(ApiError.NotFound("No existe el recurso con id $id."))
        } else {
            Result.success(resource)
        }
    }

    override suspend fun create(request: CreateContentResourceRequest): Result<ContentResource> =
        store.mutex.withLock {
            if (store.contents.none { it.id == request.contenidoId }) {
                return Result.failure(ApiError.NotFound("No existe el contenido con id ${request.contenidoId}."))
            }

            if (request.url.isBlank()) {
                return Result.failure(ApiError.BadRequest("La URL es obligatoria."))
            }

            val resource = ContentResource(
                id = store.nextContentResourceId++,
                contenidoId = request.contenidoId,
                tipo = request.tipo,
                url = request.url.trim(),
                descripcion = request.descripcion?.trim()?.takeIf { it.isNotEmpty() },
                origen = request.origen,
                estado = ContentStatus.DRAFT
            )

            store.contentResources.add(resource)
            Result.success(resource)
        }

    override suspend fun update(id: Int, request: UpdateContentResourceRequest): Result<ContentResource> =
        store.mutex.withLock {
            val resource = store.contentResources.find { it.id == id }
                ?: return Result.failure(ApiError.NotFound("No existe el recurso con id $id."))

            val updated = resource.copy(
                tipo = request.tipo ?: resource.tipo,
                url = request.url?.trim() ?: resource.url,
                descripcion = request.descripcion?.trim()?.takeIf { it.isNotEmpty() } ?: resource.descripcion,
                origen = request.origen ?: resource.origen
            )

            store.contentResources[store.contentResources.indexOf(resource)] = updated
            Result.success(updated)
        }

    override suspend fun delete(id: Int): Result<Unit> = store.mutex.withLock {
        val resource = store.contentResources.find { it.id == id }
            ?: return Result.failure(ApiError.NotFound("No existe el recurso con id $id."))

        store.contentResources.remove(resource)
        Result.success(Unit)
    }
}
