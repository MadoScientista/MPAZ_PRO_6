package com.ccstudio.mpaz_pro_6.data.mock

import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.error.ApiError
import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateContentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateContentRequest
import com.ccstudio.mpaz_pro_6.data.repository.ContentRepository
import kotlinx.coroutines.sync.withLock

/**
 * Implementación en memoria de `ContentRepository`.
 */
class MockContentRepository(
    private val store: MockStore
) : ContentRepository {

    override suspend fun list(learningObjectiveId: Int?): Result<List<Content>> =
        store.mutex.withLock {
            if (learningObjectiveId != null && store.objectives.none { it.id == learningObjectiveId }) {
                return Result.failure(ApiError.NotFound("No existe el objetivo con id $learningObjectiveId."))
            }

            val contents = store.contents
                .filter { learningObjectiveId == null || it.oaId == learningObjectiveId }
                .sortedWith(compareBy({ it.oaId }, { it.orden }))

            Result.success(contents)
        }

    override suspend fun getById(id: Int): Result<Content> = store.mutex.withLock {
        val content = store.contents.find { it.id == id }
        if (content == null) {
            Result.failure(ApiError.NotFound("No existe el contenido con id $id."))
        } else {
            Result.success(content)
        }
    }

    override suspend fun create(request: CreateContentRequest): Result<Content> =
        store.mutex.withLock {
            if (store.objectives.none { it.id == request.oaId }) {
                return Result.failure(ApiError.NotFound("No existe el objetivo con id ${request.oaId}."))
            }

            if (request.titulo.isBlank() || request.explicacion.isBlank()) {
                return Result.failure(ApiError.BadRequest("Título y explicación son obligatorios."))
            }

            val orden = request.orden ?: 1
            if (orden <= 0) {
                return Result.failure(ApiError.BadRequest("El orden debe ser mayor que cero."))
            }

            val content = Content(
                id = store.nextContentId++,
                oaId = request.oaId,
                titulo = request.titulo.trim(),
                explicacion = request.explicacion.trim(),
                ejemplos = request.ejemplos?.trim()?.takeIf { it.isNotEmpty() },
                instrucciones = request.instrucciones?.trim()?.takeIf { it.isNotEmpty() },
                orden = orden,
                origen = request.origen,
                estado = ContentStatus.DRAFT
            )

            store.contents.add(content)
            Result.success(content)
        }

    override suspend fun update(id: Int, request: UpdateContentRequest): Result<Content> =
        store.mutex.withLock {
            val content = store.contents.find { it.id == id }
                ?: return Result.failure(ApiError.NotFound("No existe el contenido con id $id."))

            val sinCampos = request.titulo == null && request.explicacion == null && request.ejemplos == null && request.instrucciones == null && request.orden == null && request.origen == null
            if (sinCampos) {
                return Result.failure(ApiError.BadRequest("Debes enviar al menos un campo para actualizar."))
            }

            val titulo = request.titulo?.trim() ?: content.titulo
            if (titulo.isBlank()) {
                return Result.failure(ApiError.BadRequest("El título es obligatorio."))
            }

            val explicacion = request.explicacion?.trim() ?: content.explicacion
            if (explicacion.isBlank()) {
                return Result.failure(ApiError.BadRequest("La explicación es obligatoria."))
            }

            val orden = request.orden ?: content.orden
            if (orden <= 0) {
                return Result.failure(ApiError.BadRequest("El orden debe ser mayor que cero."))
            }

            val updated = content.copy(
                titulo = titulo,
                explicacion = explicacion,
                ejemplos = request.ejemplos?.trim()?.takeIf { it.isNotEmpty() } ?: content.ejemplos,
                instrucciones = request.instrucciones?.trim()?.takeIf { it.isNotEmpty() } ?: content.instrucciones,
                orden = orden,
                origen = request.origen ?: content.origen
            )

            store.contents[store.contents.indexOf(content)] = updated
            Result.success(updated)
        }

    override suspend fun delete(id: Int): Result<Unit> = store.mutex.withLock {
        val content = store.contents.find { it.id == id }
            ?: return Result.failure(ApiError.NotFound("No existe el contenido con id $id."))

        store.contentResources.removeAll { it.contenidoId == id }
        store.contents.remove(content)
        Result.success(Unit)
    }
}
