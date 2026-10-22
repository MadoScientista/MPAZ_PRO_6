package com.ccstudio.mpaz_pro_6.data.repository

import com.ccstudio.mpaz_pro_6.data.model.Question
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateQuestionRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateQuestionRequest

/**
 * Repositorio de preguntas (`/api/preguntas`).
 *
 * Todas las operaciones son `suspend` y devuelven `Result`; el fallo es siempre
 * un `ApiError`.
 */
interface QuestionRepository {

    /**
     * Lista las preguntas ordenadas por actividad y orden.
     *
     * @param activityId filtro opcional del backend (`?actividadId`).
     * @param learningObjectiveId filtro opcional del backend (`?oaId`).
     * Ambos filtros son combinables.
     */
    suspend fun list(
        activityId: Int? = null,
        learningObjectiveId: Int? = null
    ): Result<List<Question>>

    /** Obtiene una pregunta por id. */
    suspend fun getById(id: Int): Result<Question>

    /** Crea una pregunta asociada a una actividad y a un objetivo de aprendizaje. */
    suspend fun create(request: CreateQuestionRequest): Result<Question>

    /** Actualiza enunciado, criterio, dificultad u orden. Solo se aplican los valores enviados. */
    suspend fun update(id: Int, request: UpdateQuestionRequest): Result<Question>

    /**
     * Elimina una pregunta.
     *
     * Falla con `ApiError.Conflict` si tiene alternativas o respuestas de
     * estudiantes asociadas.
     */
    suspend fun delete(id: Int): Result<Unit>
}
