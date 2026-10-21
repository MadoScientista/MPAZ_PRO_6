package com.ccstudio.mpaz_pro_6.data.repository

import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.AssignmentActivity
import com.ccstudio.mpaz_pro_6.data.model.AssignmentObjective
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateAssignmentRequest

/**
 * Repositorio de asignaciones (`/api/asignaciones`).
 *
 * Todas las operaciones son `suspend` y devuelven `Result`; el fallo es siempre
 * un `ApiError`.
 *
 * También expone las dos asociaciones de la asignación: con objetivos de
 * aprendizaje y con actividades.
 */
interface AssignmentRepository {

    /**
     * Lista las asignaciones ordenadas por id.
     *
     * @param courseId filtro opcional del backend (`?cursoId`).
     * @param unitId filtro opcional del backend (`?unidadId`).
     * @param teacherId filtro opcional del backend (`?docenteId`).
     * Los tres filtros son combinables.
     */
    suspend fun list(
        courseId: Int? = null,
        unitId: Int? = null,
        teacherId: Int? = null
    ): Result<List<Assignment>>

    /** Obtiene una asignación por id. */
    suspend fun getById(id: Int): Result<Assignment>

    /** Crea la asignación de una unidad a un curso, con su docente. */
    suspend fun create(request: CreateAssignmentRequest): Result<Assignment>

    /** Actualiza `maxIntentos`, `pruebaFinalHabilitada`, `fecha` o una combinación. */
    suspend fun update(id: Int, request: UpdateAssignmentRequest): Result<Assignment>

    /**
     * Elimina una asignación.
     *
     * Falla con `ApiError.Conflict` si tiene objetivos, actividades o intentos
     * asociados.
     */
    suspend fun delete(id: Int): Result<Unit>

    /** Lista los objetivos de aprendizaje asociados a la asignación. */
    suspend fun listObjectives(assignmentId: Int): Result<List<AssignmentObjective>>

    /**
     * Asocia a la asignación un objetivo de aprendizaje de su unidad.
     *
     * Falla con `ApiError.BadRequest` si el objetivo es de otra unidad y con
     * `ApiError.Conflict` si ya estaba asociado.
     */
    suspend fun addObjective(assignmentId: Int, learningObjectiveId: Int): Result<AssignmentObjective>

    /** Quita la asociación entre la asignación y el objetivo de aprendizaje. */
    suspend fun removeObjective(assignmentId: Int, learningObjectiveId: Int): Result<Unit>

    /** Lista las actividades asociadas a la asignación. */
    suspend fun listActivities(assignmentId: Int): Result<List<AssignmentActivity>>

    /**
     * Asocia a la asignación una actividad de su unidad.
     *
     * Falla con `ApiError.BadRequest` si la actividad es de otra unidad o si es
     * una `PRUEBA_FINAL`, y con `ApiError.Conflict` si ya estaba asociada.
     */
    suspend fun addActivity(assignmentId: Int, activityId: Int): Result<AssignmentActivity>

    /** Quita la asociación entre la asignación y la actividad. */
    suspend fun removeActivity(assignmentId: Int, activityId: Int): Result<Unit>
}
