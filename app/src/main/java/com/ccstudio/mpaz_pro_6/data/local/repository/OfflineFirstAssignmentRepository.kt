package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.error.apiCall
import com.ccstudio.mpaz_pro_6.data.error.requireSuccess
import com.ccstudio.mpaz_pro_6.data.local.dao.AssignmentDao
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentActivityEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentObjectiveEntity
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.local.mapper.toEntity
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.AssignmentActivity
import com.ccstudio.mpaz_pro_6.data.model.AssignmentObjective
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.remote.MpazApi
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.remote.mapper.toDto
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository

/** Repositorio de asignaciones y sus asociaciones con respaldo en Room. */
class OfflineFirstAssignmentRepository(
    private val api: MpazApi,
    private val dao: AssignmentDao
) : AssignmentRepository {

    override suspend fun list(
        courseId: Int?,
        unitId: Int?,
        teacherId: Int?
    ): Result<List<Assignment>> =
        apiCall {
            api.getAssignments(courseId, unitId, teacherId).map { it.toDomain() }
        }.fold(
            onSuccess = { assignments ->
                dao.upsertAssignments(assignments.map { it.toEntity() })
                Result.success(assignments)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    val entities = when {
                        courseId != null -> dao.getAssignmentsByCourse(courseId)
                        unitId != null -> dao.getAssignmentsByUnit(unitId)
                        teacherId != null -> dao.getAssignmentsByTeacher(teacherId)
                        else -> dao.getAssignments()
                    }
                    entities
                        .filter { courseId == null || it.cursoId == courseId }
                        .filter { unitId == null || it.unidadId == unitId }
                        .filter { teacherId == null || it.docenteId == teacherId }
                        .map { it.toDomain() }
                        .takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun getById(id: Int): Result<Assignment> =
        apiCall { api.getAssignment(id).toDomain() }.fold(
            onSuccess = { assignment ->
                dao.upsertAssignment(assignment.toEntity())
                Result.success(assignment)
            },
            onFailure = { error ->
                cachedValueOrError(error) { dao.getAssignmentById(id)?.toDomain() }
            }
        )

    override suspend fun create(
        request: CreateAssignmentRequest
    ): Result<Assignment> =
        apiCall { api.createAssignment(request.toDto()).toDomain() }.fold(
            onSuccess = { assignment ->
                dao.upsertAssignment(assignment.toEntity())
                Result.success(assignment)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun update(
        id: Int,
        request: UpdateAssignmentRequest
    ): Result<Assignment> =
        apiCall { api.updateAssignment(id, request.toDto()).toDomain() }.fold(
            onSuccess = { assignment ->
                dao.upsertAssignment(assignment.toEntity())
                Result.success(assignment)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun delete(id: Int): Result<Unit> =
        apiCall {
            api.deleteAssignment(id).requireSuccess()
            dao.deleteAssignmentObjectivesByAssignment(id)
            dao.deleteAssignmentActivitiesByAssignment(id)
            dao.deleteAssignmentById(id)
        }

    override suspend fun listObjectives(
        assignmentId: Int
    ): Result<List<AssignmentObjective>> =
        apiCall { api.getAssignmentObjectives(assignmentId).map { it.toDomain() } }.fold(
            onSuccess = { objectives ->
                dao.upsertAssignmentObjectives(
                    objectives.map {
                        AssignmentObjectiveEntity(
                            asignacionId = it.asignacionId,
                            unidadId = it.unidadId,
                            oaId = it.oaId
                        )
                    }
                )
                Result.success(objectives)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    dao.getAssignmentObjectivesByAssignment(assignmentId)
                        .map { it.toDomain() }
                        .takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun addObjective(
        assignmentId: Int,
        learningObjectiveId: Int
    ): Result<AssignmentObjective> =
        apiCall {
            api.addAssignmentObjective(assignmentId, learningObjectiveId).toDomain()
        }.fold(
            onSuccess = { objective ->
                dao.upsertAssignmentObjective(
                    AssignmentObjectiveEntity(
                        asignacionId = objective.asignacionId,
                        unidadId = objective.unidadId,
                        oaId = objective.oaId
                    )
                )
                Result.success(objective)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun removeObjective(
        assignmentId: Int,
        learningObjectiveId: Int
    ): Result<Unit> =
        apiCall {
            api.removeAssignmentObjective(assignmentId, learningObjectiveId).requireSuccess()
            dao.deleteAssignmentObjective(assignmentId, learningObjectiveId)
        }

    override suspend fun listActivities(
        assignmentId: Int
    ): Result<List<AssignmentActivity>> =
        apiCall { api.getAssignmentActivities(assignmentId).map { it.toDomain() } }.fold(
            onSuccess = { activities ->
                dao.upsertAssignmentActivities(
                    activities.map {
                        AssignmentActivityEntity(
                            asignacionId = it.asignacionId,
                            unidadId = it.unidadId,
                            actividadId = it.actividadId
                        )
                    }
                )
                Result.success(activities)
            },
            onFailure = { error ->
                cachedValueOrError(error) {
                    dao.getAssignmentActivitiesByAssignment(assignmentId)
                        .map { it.toDomain() }
                        .takeIf { it.isNotEmpty() }
                }
            }
        )

    override suspend fun addActivity(
        assignmentId: Int,
        activityId: Int
    ): Result<AssignmentActivity> =
        apiCall {
            api.addAssignmentActivity(assignmentId, activityId).toDomain()
        }.fold(
            onSuccess = { activity ->
                dao.upsertAssignmentActivity(
                    AssignmentActivityEntity(
                        asignacionId = activity.asignacionId,
                        unidadId = activity.unidadId,
                        actividadId = activity.actividadId
                    )
                )
                Result.success(activity)
            },
            onFailure = { Result.failure(it) }
        )

    override suspend fun removeActivity(
        assignmentId: Int,
        activityId: Int
    ): Result<Unit> =
        apiCall {
            api.removeAssignmentActivity(assignmentId, activityId).requireSuccess()
            dao.deleteAssignmentActivity(assignmentId, activityId)
        }
}
