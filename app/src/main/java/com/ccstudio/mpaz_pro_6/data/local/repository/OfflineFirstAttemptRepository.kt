package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.local.dao.AttemptDao
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.Attempt
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.repository.AttemptRepository

/** Repositorio de intentos con respaldo en Room. */
class OfflineFirstAttemptRepository(
    private val api: MpazApi,
    private val dao: AttemptDao
) : AttemptRepository {

    override suspend fun list(
        studentId: Int?,
        activityId: Int?
    ): Result<List<Attempt>> =
        apiCall { api.getAttempts(studentId, activityId).map { it.toDomain() } }.fold(
            onSuccess = { attempts ->
                dao.upsertAttempts(attempts.map { it.toEntity() })
                Result.success(attempts)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    val entities = when {
                        studentId != null -> dao.getAttemptsByStudent(studentId)
                        activityId != null -> dao.getAttemptsByActivity(activityId)
                        else -> dao.getAttempts()
                    }
                    entities
                        .filter { studentId == null || it.estudianteId == studentId }
                        .filter { activityId == null || it.actividadId == activityId }
                        .map { it.toDomain() }
                        .takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun getById(id: Int): Result<Attempt> =
        apiCall { api.getAttempt(id).toDomain() }.fold(
            onSuccess = { attempt ->
                dao.upsertAttempt(attempt.toEntity())
                Result.success(attempt)
            },
            onFailure = { error ->
                cachedValueOrError(error) { dao.getAttemptById(id)?.toDomain() }
            }
        )

    override suspend fun start(studentId: Int, activityId: Int): Result<Attempt> =
        apiCall { api.startAttempt(studentId, activityId).toDomain() }.fold(
            onSuccess = { attempt ->
                dao.upsertAttempt(attempt.toEntity())
                Result.success(attempt)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun complete(id: Int): Result<Attempt> =
        apiCall { api.completeAttempt(id).toDomain() }.fold(
            onSuccess = { attempt ->
                dao.upsertAttempt(attempt.toEntity())
                Result.success(attempt)
            },
            onFailure = { Result.failure(it) }
        )
}
