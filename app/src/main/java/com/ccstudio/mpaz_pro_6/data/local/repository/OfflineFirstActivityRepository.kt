package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.error.requireSuccess
import com.ccstudio.mpaz_pro_6.data.local.dao.CurriculumDao
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateActivityRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateActivityRequest
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDto
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository

/** Repositorio de actividades con respaldo en Room. */
class OfflineFirstActivityRepository(
    private val api: MpazApi,
    private val dao: CurriculumDao
) : ActivityRepository {

    override suspend fun list(unitId: Int?): Result<List<Activity>> =
        apiCall { api.getActivities(unitId).map { it.toDomain() } }.fold(
            onSuccess = { activities ->
                dao.upsertActivities(activities.map { it.toEntity() })
                Result.success(activities)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    val entities = if (unitId == null) {
                        dao.getActivities()
                    } else {
                        dao.getActivitiesByUnit(unitId)
                    }
                    entities.map { it.toDomain() }.takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun getById(id: Int): Result<Activity> =
        apiCall { api.getActivity(id).toDomain() }.fold(
            onSuccess = { activity ->
                dao.upsertActivity(activity.toEntity())
                Result.success(activity)
            },
            onFailure = { error ->
                cachedValueOrError(error) { dao.getActivityById(id)?.toDomain() }
            }
        )

    override suspend fun create(request: CreateActivityRequest): Result<Activity> =
        apiCall { api.createActivity(request.toDto()).toDomain() }.fold(
            onSuccess = { activity ->
                dao.upsertActivity(activity.toEntity())
                Result.success(activity)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun update(
        id: Int,
        request: UpdateActivityRequest
    ): Result<Activity> =
        apiCall { api.updateActivity(id, request.toDto()).toDomain() }.fold(
            onSuccess = { activity ->
                dao.upsertActivity(activity.toEntity())
                Result.success(activity)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun delete(id: Int): Result<Unit> =
        apiCall {
            api.deleteActivity(id).requireSuccess()
            dao.deleteActivityById(id)
        }
}
