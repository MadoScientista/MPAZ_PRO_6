package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.error.requireSuccess
import com.ccstudio.mpaz_pro_6.data.local.dao.CurriculumDao
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.Question
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateQuestionRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateQuestionRequest
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDto
import com.ccstudio.mpaz_pro_6.data.repository.QuestionRepository

/** Repositorio de preguntas con respaldo en Room. */
class OfflineFirstQuestionRepository(
    private val api: MpazApi,
    private val dao: CurriculumDao
) : QuestionRepository {

    override suspend fun list(
        activityId: Int?,
        learningObjectiveId: Int?
    ): Result<List<Question>> =
        apiCall {
            api.getQuestions(activityId, learningObjectiveId).map { it.toDomain() }
        }.fold(
            onSuccess = { questions ->
                dao.upsertQuestions(questions.map { it.toEntity() })
                Result.success(questions)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    val entities = when {
                        activityId != null -> dao.getQuestionsByActivity(activityId)
                        learningObjectiveId != null -> dao.getQuestionsByObjective(learningObjectiveId)
                        else -> dao.getQuestions()
                    }
                    entities.map { it.toDomain() }.takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun getById(id: Int): Result<Question> =
        apiCall { api.getQuestion(id).toDomain() }.fold(
            onSuccess = { question ->
                dao.upsertQuestion(question.toEntity())
                Result.success(question)
            },
            onFailure = { error ->
                cachedValueOrError(error) { dao.getQuestionById(id)?.toDomain() }
            }
        )

    override suspend fun create(request: CreateQuestionRequest): Result<Question> =
        apiCall { api.createQuestion(request.toDto()).toDomain() }.fold(
            onSuccess = { question ->
                dao.upsertQuestion(question.toEntity())
                Result.success(question)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun update(
        id: Int,
        request: UpdateQuestionRequest
    ): Result<Question> =
        apiCall { api.updateQuestion(id, request.toDto()).toDomain() }.fold(
            onSuccess = { question ->
                dao.upsertQuestion(question.toEntity())
                Result.success(question)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun delete(id: Int): Result<Unit> =
        apiCall {
            api.deleteQuestion(id).requireSuccess()
            dao.deleteQuestionById(id)
        }
}
