package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.error.requireSuccess
import com.ccstudio.mpaz_pro_6.data.local.dao.DocenciaDao
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.Docencia
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateDocenciaRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateDocenciaRequest
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDto
import com.ccstudio.mpaz_pro_6.data.repository.DocenciaRepository

/** Repositorio de docencias con respaldo en la caché Room. */
class OfflineFirstDocenciaRepository(
    private val api: MpazApi,
    private val dao: DocenciaDao
) : DocenciaRepository {

    override suspend fun list(docenteId: Int?): Result<List<Docencia>> =
        apiCall {
            api.getDocencias(docenteId).map { it.toDomain() }
        }.fold(
            onSuccess = { docencias ->
                dao.upsertDocencias(docencias.map { it.toEntity() })
                Result.success(docencias)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    dao.getDocencias()
                        .filter { docenteId == null || it.docenteId == docenteId }
                        .map { it.toDomain() }
                        .takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun getById(id: Int): Result<Docencia> =
        apiCall { api.getDocencia(id).toDomain() }.fold(
            onSuccess = { docencia ->
                dao.upsertDocencia(docencia.toEntity())
                Result.success(docencia)
            },
            onFailure = { error ->
                cachedValueOrError(error) { dao.getDocenciaById(id)?.toDomain() }
            }
        )

    override suspend fun create(request: CreateDocenciaRequest): Result<Docencia> =
        apiCall { api.createDocencia(request.toDto()).toDomain() }.fold(
            onSuccess = { docencia ->
                dao.upsertDocencia(docencia.toEntity())
                Result.success(docencia)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun update(
        id: Int,
        request: UpdateDocenciaRequest
    ): Result<Docencia> =
        apiCall { api.updateDocencia(id, request.toDto()).toDomain() }.fold(
            onSuccess = { docencia ->
                dao.upsertDocencia(docencia.toEntity())
                Result.success(docencia)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun delete(id: Int): Result<Unit> =
        apiCall {
            api.deleteDocencia(id).requireSuccess()
            dao.deleteDocenciaById(id)
        }
}
