package com.ccstudio.mpaz_pro_6.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentActivityEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentEntity
import com.ccstudio.mpaz_pro_6.data.local.entity.AssignmentObjectiveEntity

/** Consultas de asignaciones y sus asociaciones. */
@Dao
interface AssignmentDao {

    @Query("SELECT * FROM asignacion ORDER BY id")
    suspend fun getAssignments(): List<AssignmentEntity>

    @Query("SELECT * FROM asignacion WHERE curso_id = :courseId ORDER BY id")
    suspend fun getAssignmentsByCourse(courseId: Int): List<AssignmentEntity>

    @Query("SELECT * FROM asignacion WHERE unidad_id = :unitId ORDER BY id")
    suspend fun getAssignmentsByUnit(unitId: Int): List<AssignmentEntity>

    @Query("SELECT * FROM asignacion WHERE docente_id = :teacherId ORDER BY id")
    suspend fun getAssignmentsByTeacher(teacherId: Int): List<AssignmentEntity>

    @Query("SELECT * FROM asignacion WHERE id = :id")
    suspend fun getAssignmentById(id: Int): AssignmentEntity?

    @Upsert
    suspend fun upsertAssignment(entity: AssignmentEntity)

    @Upsert
    suspend fun upsertAssignments(entities: List<AssignmentEntity>)

    @Query("DELETE FROM asignacion")
    suspend fun deleteAllAssignments()

    @Query("DELETE FROM asignacion WHERE id = :id")
    suspend fun deleteAssignmentById(id: Int)

    @Query("SELECT * FROM asignacion_oa ORDER BY asignacion_id, oa_id")
    suspend fun getAssignmentObjectives(): List<AssignmentObjectiveEntity>

    @Query("SELECT * FROM asignacion_oa WHERE asignacion_id = :assignmentId")
    suspend fun getAssignmentObjectivesByAssignment(
        assignmentId: Int
    ): List<AssignmentObjectiveEntity>

    @Upsert
    suspend fun upsertAssignmentObjective(entity: AssignmentObjectiveEntity)

    @Upsert
    suspend fun upsertAssignmentObjectives(entities: List<AssignmentObjectiveEntity>)

    @Query("DELETE FROM asignacion_oa")
    suspend fun deleteAllAssignmentObjectives()

    @Query("DELETE FROM asignacion_oa WHERE asignacion_id = :assignmentId AND oa_id = :objectiveId")
    suspend fun deleteAssignmentObjective(assignmentId: Int, objectiveId: Int)

    @Query("DELETE FROM asignacion_oa WHERE asignacion_id = :assignmentId")
    suspend fun deleteAssignmentObjectivesByAssignment(assignmentId: Int)

    @Query("SELECT * FROM asignacion_actividad ORDER BY asignacion_id, actividad_id")
    suspend fun getAssignmentActivities(): List<AssignmentActivityEntity>

    @Query("SELECT * FROM asignacion_actividad WHERE asignacion_id = :assignmentId")
    suspend fun getAssignmentActivitiesByAssignment(
        assignmentId: Int
    ): List<AssignmentActivityEntity>

    @Upsert
    suspend fun upsertAssignmentActivity(entity: AssignmentActivityEntity)

    @Upsert
    suspend fun upsertAssignmentActivities(entities: List<AssignmentActivityEntity>)

    @Query("DELETE FROM asignacion_actividad")
    suspend fun deleteAllAssignmentActivities()

    @Query("DELETE FROM asignacion_actividad WHERE asignacion_id = :assignmentId AND actividad_id = :activityId")
    suspend fun deleteAssignmentActivity(assignmentId: Int, activityId: Int)

    @Query("DELETE FROM asignacion_actividad WHERE asignacion_id = :assignmentId")
    suspend fun deleteAssignmentActivitiesByAssignment(assignmentId: Int)
}
