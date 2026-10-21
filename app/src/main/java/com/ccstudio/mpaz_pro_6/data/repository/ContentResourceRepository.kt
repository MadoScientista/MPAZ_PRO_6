package com.ccstudio.mpaz_pro_6.data.repository

import com.ccstudio.mpaz_pro_6.data.model.ContentResource
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateContentResourceRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateContentResourceRequest

/**
 * Repositorio de recursos de contenido (`/api/recursos-contenido`).
 *
 * Todas las operaciones son `suspend` y devuelven `Result`; el fallo es siempre
 * un `ApiError`.
 */
interface ContentResourceRepository {

    /**
     * Lista los recursos de contenido.
     *
     * @param contentId filtro opcional del backend (`?contenidoId`).
     */
    suspend fun list(contentId: Int? = null): Result<List<ContentResource>>

    /** Obtiene un recurso de contenido por id. */
    suspend fun getById(id: Int): Result<ContentResource>

    /** Crea un recurso asociado a un contenido existente. */
    suspend fun create(request: CreateContentResourceRequest): Result<ContentResource>

    /** Actualiza tipo, url, descripción u origen. El contenido asociado se mantiene. */
    suspend fun update(id: Int, request: UpdateContentResourceRequest): Result<ContentResource>

    /** Elimina un recurso de contenido. */
    suspend fun delete(id: Int): Result<Unit>
}
