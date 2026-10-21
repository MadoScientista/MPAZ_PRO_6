package com.ccstudio.mpaz_pro_6.data.repository

import com.ccstudio.mpaz_pro_6.data.model.Content
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateContentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateContentRequest

/**
 * Repositorio de contenidos (`/api/contenidos`).
 *
 * Todas las operaciones son `suspend` y devuelven `Result`; el fallo es siempre
 * un `ApiError`.
 */
interface ContentRepository {

    /**
     * Lista los contenidos ordenados por objetivo de aprendizaje y orden.
     *
     * @param learningObjectiveId filtro opcional del backend (`?oaId`).
     */
    suspend fun list(learningObjectiveId: Int? = null): Result<List<Content>>

    /** Obtiene un contenido por id. */
    suspend fun getById(id: Int): Result<Content>

    /** Crea un contenido asociado a un objetivo de aprendizaje existente. */
    suspend fun create(request: CreateContentRequest): Result<Content>

    /** Actualiza título, explicación, ejemplos, instrucciones, orden u origen. */
    suspend fun update(id: Int, request: UpdateContentRequest): Result<Content>

    /** Elimina un contenido. El backend arrastra en cascada sus recursos asociados. */
    suspend fun delete(id: Int): Result<Unit>
}
