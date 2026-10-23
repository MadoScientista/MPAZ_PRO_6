package com.ccstudio.mpaz_pro_6.data.local.repository

import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.enums.UserRole
import com.ccstudio.mpaz_pro_6.data.local.dao.AssignmentDao
import com.ccstudio.mpaz_pro_6.data.local.dao.CatalogDao
import com.ccstudio.mpaz_pro_6.data.local.dao.CurriculumDao
import com.ccstudio.mpaz_pro_6.data.local.dao.UnitDao
import com.ccstudio.mpaz_pro_6.data.local.dao.UserDao
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentActivityEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentObjectiveEntity
import com.ccstudio.mpaz_pro_6.data.local.mapper.toDomain
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.AssignmentActivity
import com.ccstudio.mpaz_pro_6.data.model.AssignmentObjective
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository

/** CRUD local de asignaciones y sus asociaciones sobre Room. */
class LocalAssignmentRepository(
    private val dao: AssignmentDao,
    private val catalogDao: CatalogDao,
    private val unitDao: UnitDao,
    private val userDao: UserDao,
    private val curriculumDao: CurriculumDao
) : AssignmentRepository {

    override suspend fun list(
        courseId: Int?,
        unitId: Int?,
        teacherId: Int?
    ): Result<List<Assignment>> = localCall {
        dao.getAssignments()
            .filter { courseId == null || it.cursoId == courseId }
            .filter { unitId == null || it.unidadId == unitId }
            .filter { teacherId == null || it.docenteId == teacherId }
            .map { it.toDomain() }
    }

    override suspend fun getById(id: Int): Result<Assignment> = localCall {
        dao.getAssignmentById(id).orNotFound("Asignación").getOrThrow().toDomain()
    }

    override suspend fun create(request: CreateAssignmentRequest): Result<Assignment> =
        localCall {
            catalogDao.getCourseById(request.cursoId).orNotFound("Curso").getOrThrow()
            unitDao.getUnitById(request.unidadId).orNotFound("Unidad").getOrThrow()
            val teacher = userDao.getUserById(request.docenteId)
                .orNotFound("Docente")
                .getOrThrow()
            require(teacher.rol == UserRole.DOCENTE) { "El usuario debe tener rol DOCENTE." }

            val entities = dao.getAssignments()
            val entity = AssignmentEntity(
                id = entities.nextId { it.id },
                cursoId = request.cursoId,
                unidadId = request.unidadId,
                docenteId = request.docenteId,
                maxIntentos = request.maxIntentos ?: 1,
                pruebaFinalHabilitada = request.pruebaFinalHabilitada ?: false,
                fecha = request.fecha
            )
            dao.upsertAssignment(entity)
            entity.toDomain()
        }

    override suspend fun update(
        id: Int,
        request: UpdateAssignmentRequest
    ): Result<Assignment> = localCall {
        val current = dao.getAssignmentById(id).orNotFound("Asignación").getOrThrow()
        val updated = current.copy(
            maxIntentos = request.maxIntentos ?: current.maxIntentos,
            pruebaFinalHabilitada = request.pruebaFinalHabilitada
                ?: current.pruebaFinalHabilitada,
            fecha = request.fecha ?: current.fecha
        )
        dao.upsertAssignment(updated)
        updated.toDomain()
    }

    override suspend fun delete(id: Int): Result<Unit> = localCall {
        dao.getAssignmentById(id).orNotFound("Asignación").getOrThrow()
        dao.deleteAssignmentById(id)
    }

    override suspend fun listObjectives(
        assignmentId: Int
    ): Result<List<AssignmentObjective>> = localCall {
        dao.getAssignmentById(assignmentId).orNotFound("Asignación").getOrThrow()
        dao.getAssignmentObjectivesByAssignment(assignmentId).map { it.toDomain() }
    }

    override suspend fun addObjective(
        assignmentId: Int,
        learningObjectiveId: Int
    ): Result<AssignmentObjective> = localCall {
        val assignment = dao.getAssignmentById(assignmentId)
            .orNotFound("Asignación")
            .getOrThrow()
        val objective = curriculumDao.getObjectiveById(learningObjectiveId)
            .orNotFound("Objetivo de aprendizaje")
            .getOrThrow()
        require(objective.unidadId == assignment.unidadId) {
            "El objetivo debe pertenecer a la unidad de la asignación."
        }
        val existing = dao.getAssignmentObjectivesByAssignment(assignmentId)
        require(existing.none { it.oaId == learningObjectiveId }) {
            "El objetivo ya está asociado a la asignación."
        }
        val entity = AssignmentObjectiveEntity(
            asignacionId = assignmentId,
            unidadId = assignment.unidadId,
            oaId = learningObjectiveId
        )
        dao.upsertAssignmentObjective(entity)
        entity.toDomain()
    }

    override suspend fun removeObjective(
        assignmentId: Int,
        learningObjectiveId: Int
    ): Result<Unit> = localCall {
        dao.getAssignmentById(assignmentId).orNotFound("Asignación").getOrThrow()
        dao.deleteAssignmentObjective(assignmentId, learningObjectiveId)
    }

    override suspend fun listActivities(
        assignmentId: Int
    ): Result<List<AssignmentActivity>> = localCall {
        dao.getAssignmentById(assignmentId).orNotFound("Asignación").getOrThrow()
        dao.getAssignmentActivitiesByAssignment(assignmentId).map { it.toDomain() }
    }

    override suspend fun addActivity(
        assignmentId: Int,
        activityId: Int
    ): Result<AssignmentActivity> = localCall {
        val assignment = dao.getAssignmentById(assignmentId)
            .orNotFound("Asignación")
            .getOrThrow()
        val activity = curriculumDao.getActivityById(activityId)
            .orNotFound("Actividad")
            .getOrThrow()
        require(activity.unidadId == assignment.unidadId) {
            "La actividad debe pertenecer a la unidad de la asignación."
        }
        require(activity.tipo != ActivityType.FINAL_TEST) {
            "La prueba final no se puede asociar como actividad."
        }
        val existing = dao.getAssignmentActivitiesByAssignment(assignmentId)
        require(existing.none { it.actividadId == activityId }) {
            "La actividad ya está asociada a la asignación."
        }
        val entity = AssignmentActivityEntity(
            asignacionId = assignmentId,
            unidadId = assignment.unidadId,
            actividadId = activityId
        )
        dao.upsertAssignmentActivity(entity)
        entity.toDomain()
    }

    override suspend fun removeActivity(
        assignmentId: Int,
        activityId: Int
    ): Result<Unit> = localCall {
        dao.getAssignmentById(assignmentId).orNotFound("Asignación").getOrThrow()
        dao.deleteAssignmentActivity(assignmentId, activityId)
    }
}
