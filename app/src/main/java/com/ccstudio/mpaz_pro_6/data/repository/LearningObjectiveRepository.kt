package com.ccstudio.mpaz_pro_6.data.repository

import com.ccstudio.mpaz_pro_6.data.model.LearningObjective
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateLearningObjectiveRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateLearningObjectiveRequest

/**
 * Repositorio de objetivos de aprendizaje (`/api/objetivos-aprendizaje`).
 *
 * Todas las operaciones son `suspend` y devuelven `Result`; el fallo es siempre
 * un `ApiError`.
 */
interface LearningObjectiveRepository {

    /**
     * Lista los objetivos ordenados por unidad y código.
     *
     * @param unitId filtro opcional del backend (`?unidadId`).
     */
    suspend fun list(unitId: Int? = null): Result<List<LearningObjective>>

    /** Obtiene un objetivo de aprendizaje por id. */
    suspend fun getById(id: Int): Result<LearningObjective>

    /** Crea un objetivo asociado a una unidad existente. */
    suspend fun create(request: CreateLearningObjectiveRequest): Result<LearningObjective>

    /** Actualiza código, descripción, eje o texto de referencia. Solo se aplican los valores enviados. */
    suspend fun update(
        id: Int,
        request: UpdateLearningObjectiveRequest
    ): Result<LearningObjective>

    /**
     * Elimina un objetivo de aprendizaje.
     *
     * Falla con `ApiError.Conflict` si tiene contenidos, preguntas, asignaciones
     * o informes asociados.
     */
    suspend fun delete(id: Int): Result<Unit>
}
