package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.error.requireSuccess
import com.ccstudio.mpaz_pro_6.data.local.dao.CatalogDao
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.Subject
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateSubjectRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateSubjectRequest
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDto
import com.ccstudio.mpaz_pro_6.data.repository.SubjectRepository

/** Repositorio de asignaturas con respaldo en la caché Room. */
class OfflineFirstSubjectRepository(
    private val api: MpazApi,
    private val dao: CatalogDao
) : SubjectRepository {

    override suspend fun list(): Result<List<Subject>> =
        apiCall { api.getSubjects().map { it.toDomain() } }.fold(
            onSuccess = { subjects ->
                dao.upsertSubjects(subjects.map { it.toEntity() })
                Result.success(subjects)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    dao.getSubjects().map { it.toDomain() }.takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun getById(id: Int): Result<Subject> =
        apiCall { api.getSubject(id).toDomain() }.fold(
            onSuccess = { subject ->
                dao.upsertSubject(subject.toEntity())
                Result.success(subject)
            },
            onFailure = { error ->
                cachedValueOrError(error) { dao.getSubjectById(id)?.toDomain() }
            }
        )

    override suspend fun create(request: CreateSubjectRequest): Result<Subject> =
        apiCall { api.createSubject(request.toDto()).toDomain() }.fold(
            onSuccess = { subject ->
                dao.upsertSubject(subject.toEntity())
                Result.success(subject)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun update(
        id: Int,
        request: UpdateSubjectRequest
    ): Result<Subject> =
        apiCall { api.updateSubject(id, request.toDto()).toDomain() }.fold(
            onSuccess = { subject ->
                dao.upsertSubject(subject.toEntity())
                Result.success(subject)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun delete(id: Int): Result<Unit> =
        apiCall {
            api.deleteSubject(id).requireSuccess()
            dao.deleteSubjectById(id)
        }
}
