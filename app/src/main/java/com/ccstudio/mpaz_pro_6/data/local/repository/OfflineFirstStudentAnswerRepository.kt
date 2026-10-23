package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.local.dao.AttemptDao
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.StudentAnswer
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateStudentAnswerRequest
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDto
import com.ccstudio.mpaz_pro_6.data.repository.StudentAnswerRepository

/** Repositorio de respuestas de estudiante con respaldo en Room. */
class OfflineFirstStudentAnswerRepository(
    private val api: MpazApi,
    private val dao: AttemptDao
) : StudentAnswerRepository {

    override suspend fun list(attemptId: Int): Result<List<StudentAnswer>> =
        apiCall { api.getStudentAnswers(attemptId).map { it.toDomain() } }.fold(
            onSuccess = { answers ->
                dao.upsertStudentAnswers(answers.map { it.toEntity() })
                Result.success(answers)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    dao.getStudentAnswersByAttempt(attemptId)
                        .map { it.toDomain() }
                        .takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun register(
        attemptId: Int,
        request: CreateStudentAnswerRequest
    ): Result<StudentAnswer> =
        apiCall {
            api.registerStudentAnswer(attemptId, request.toDto()).toDomain()
        }.fold(
            onSuccess = { answer ->
                dao.upsertStudentAnswer(answer.toEntity())
                Result.success(answer)
            },
            onFailure = { Result.failure(it) }
        )
}
