package com.ccstudio.mpaz_pro_6.data.repository

import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateLearningUnitRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateLearningUnitRequest

/**
 * Repositorio de unidades de aprendizaje (`/api/unidades`).
 *
 * Todas las operaciones son `suspend` y devuelven `Result`; el fallo es siempre
 * un `ApiError`.
 */
interface UnitRepository {

    /**
     * Lista las unidades ordenadas por asignatura y orden.
     *
     * @param subjectId filtro opcional del backend (`?asignaturaId`).
     */
    suspend fun list(subjectId: Int? = null): Result<List<LearningUnit>>

    /** Obtiene una unidad por id. */
    suspend fun getById(id: Int): Result<LearningUnit>

    /** Crea una unidad asociada a una asignatura existente. */
    suspend fun create(request: CreateLearningUnitRequest): Result<LearningUnit>

    /**
     * Actualiza nivel, título, orden, estado o datos de autorización.
     *
     * La actualización es parcial: solo se aplican los campos enviados.
     */
    suspend fun update(id: Int, request: UpdateLearningUnitRequest): Result<LearningUnit>

    /**
     * Envía una unidad en borrador a revisión de la UTP.
     *
     * Solo el docente dueño de la asignatura puede hacerlo. La unidad debe estar
     * en `DRAFT`; después queda en `IN_REVIEW`.
     */
    suspend fun sendToReview(id: Int): Result<LearningUnit>

    /**
     * Elimina una unidad.
     *
     * Falla con `ApiError.Conflict` si tiene objetivos, actividades, asignaciones
     * o informes asociados.
     */
    suspend fun delete(id: Int): Result<Unit>
}
