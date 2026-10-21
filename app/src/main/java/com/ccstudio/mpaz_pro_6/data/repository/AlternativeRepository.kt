package com.ccstudio.mpaz_pro_6.data.repository

import com.ccstudio.mpaz_pro_6.data.model.Alternative
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateAlternativeRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateAlternativeRequest

/**
 * Repositorio de alternativas (`/api/alternativas`).
 *
 * Todas las operaciones son `suspend` y devuelven `Result`; el fallo es siempre
 * un `ApiError`.
 */
interface AlternativeRepository {

    /**
     * Lista las alternativas de una pregunta.
     *
     * @param questionId filtro opcional del backend (`?preguntaId`).
     */
    suspend fun list(questionId: Int? = null): Result<List<Alternative>>

    /** Obtiene una alternativa por id. */
    suspend fun getById(id: Int): Result<Alternative>

    /** Crea una alternativa asociada a una pregunta existente. */
    suspend fun create(request: CreateAlternativeRequest): Result<Alternative>

    /** Actualiza el texto o si la alternativa es correcta. Solo se aplican los valores enviados. */
    suspend fun update(id: Int, request: UpdateAlternativeRequest): Result<Alternative>

    /**
     * Elimina una alternativa.
     *
     * Falla con `ApiError.Conflict` si ya fue seleccionada en la respuesta de un
     * estudiante.
     */
    suspend fun delete(id: Int): Result<Unit>
}
