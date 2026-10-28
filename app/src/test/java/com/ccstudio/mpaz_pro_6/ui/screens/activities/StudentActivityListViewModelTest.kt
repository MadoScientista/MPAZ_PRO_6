package com.ccstudio.mpaz_pro_6.ui.screens.activities

import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.enums.ContentStatus
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.AssignmentActivity
import com.ccstudio.mpaz_pro_6.data.model.AssignmentObjective
import com.ccstudio.mpaz_pro_6.data.model.Attempt
import com.ccstudio.mpaz_pro_6.data.model.LearningUnit
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateActivityRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateLearningUnitRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateActivityRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateLearningUnitRequest
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.AttemptRepository
import com.ccstudio.mpaz_pro_6.data.repository.UnitRepository
import com.ccstudio.mpaz_pro_6.ui.components.activity.StudentActivityStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

/** Pruebas de la repetición de la prueba final en el listado del estudiante. */
class StudentActivityListViewModelTest {

    private val unit = LearningUnit(
        id = 1,
        asignaturaId = 1,
        nivelEducativo = "Sexto basico",
        titulo = "Unidad 1",
        orden = 1,
        estado = ContentStatus.PUBLISHED,
        autorizadaPorId = 1,
        fechaAutorizacion = null
    )

    private val finalTest = Activity(
        id = 10,
        unidadId = 1,
        titulo = "Prueba final",
        tipo = ActivityType.FINAL_TEST,
        orden = 3
    )

    private val assignment = Assignment(
        id = 1,
        cursoId = 1,
        unidadId = 1,
        maxIntentos = 2,
        pruebaFinalHabilitada = true,
        fecha = Instant.parse("2026-04-06T08:00:00Z")
    )

    @Test
    fun `una prueba final completada puede repetirse si quedan intentos`() = runBlocking {
        val viewModel = viewModel(
            attempts = listOf(attempt(id = 1, numero = 1, completed = true))
        )

        viewModel.cargar()

        val item = viewModel.items.single()
        assertEquals(ActivityType.FINAL_TEST, item.activity.tipo)
        assertTrue(item.canRetry)
        assertEquals(StudentActivityStatus.COMPLETED, item.status)
    }

    @Test
    fun `una prueba final deja de ofrecer repeticion al alcanzar el maximo`() = runBlocking {
        val viewModel = viewModel(
            attempts = listOf(
                attempt(id = 1, numero = 1, completed = true),
                attempt(id = 2, numero = 2, completed = true)
            )
        )

        viewModel.cargar()

        assertFalse(viewModel.items.single().canRetry)
    }

    @Test
    fun `una prueba final en progreso no ofrece repeticion`() = runBlocking {
        val viewModel = viewModel(
            attempts = listOf(attempt(id = 1, numero = 1, completed = false))
        )

        viewModel.cargar()

        assertFalse(viewModel.items.single().canRetry)
    }

    private fun viewModel(attempts: List<Attempt>): StudentActivityListViewModel =
        StudentActivityListViewModel(
            assignmentRepository = FakeAssignmentRepository(assignment),
            unitRepository = FakeUnitRepository(unit),
            activityRepository = FakeActivityRepository(finalTest),
            attemptRepository = FakeAttemptRepository(attempts),
            studentId = 3,
            courseId = 1,
            dispatcher = Dispatchers.Unconfined
        )

    private fun attempt(id: Int, numero: Int, completed: Boolean): Attempt = Attempt(
        id = id,
        estudianteId = 3,
        actividadId = finalTest.id,
        numero = numero,
        fechaInicio = Instant.parse("2026-04-06T08:00:00Z"),
        fechaFin = if (completed) Instant.parse("2026-04-06T08:30:00Z") else null
    )
}

private class FakeAssignmentRepository(
    private val assignment: Assignment?
) : AssignmentRepository {

    override suspend fun list(courseId: Int?, unitId: Int?): Result<List<Assignment>> =
        Result.success(listOfNotNull(assignment))

    override suspend fun getById(id: Int): Result<Assignment> =
        error("No se usa en esta prueba")

    override suspend fun create(request: CreateAssignmentRequest): Result<Assignment> =
        error("No se usa en esta prueba")

    override suspend fun update(
        id: Int,
        request: UpdateAssignmentRequest
    ): Result<Assignment> = error("No se usa en esta prueba")

    override suspend fun delete(id: Int): Result<Unit> =
        error("No se usa en esta prueba")

    override suspend fun listObjectives(assignmentId: Int): Result<List<AssignmentObjective>> =
        Result.success(emptyList())

    override suspend fun addObjective(
        assignmentId: Int,
        learningObjectiveId: Int
    ): Result<AssignmentObjective> = error("No se usa en esta prueba")

    override suspend fun removeObjective(
        assignmentId: Int,
        learningObjectiveId: Int
    ): Result<Unit> = error("No se usa en esta prueba")

    override suspend fun listActivities(
        assignmentId: Int
    ): Result<List<AssignmentActivity>> = Result.success(emptyList())

    override suspend fun addActivity(
        assignmentId: Int,
        activityId: Int
    ): Result<AssignmentActivity> = error("No se usa en esta prueba")

    override suspend fun removeActivity(assignmentId: Int, activityId: Int): Result<Unit> =
        error("No se usa en esta prueba")
}

private class FakeUnitRepository(
    private val unit: LearningUnit
) : UnitRepository {

    override suspend fun list(subjectId: Int?): Result<List<LearningUnit>> =
        Result.success(listOf(unit))

    override suspend fun getById(id: Int): Result<LearningUnit> =
        error("No se usa en esta prueba")

    override suspend fun create(request: CreateLearningUnitRequest): Result<LearningUnit> =
        error("No se usa en esta prueba")

    override suspend fun update(
        id: Int,
        request: UpdateLearningUnitRequest
    ): Result<LearningUnit> = error("No se usa en esta prueba")

    override suspend fun sendToReview(id: Int): Result<LearningUnit> =
        error("No se usa en esta prueba")

    override suspend fun delete(id: Int): Result<Unit> =
        error("No se usa en esta prueba")
}

private class FakeActivityRepository(
    private val activity: Activity
) : ActivityRepository {

    override suspend fun list(
        unitId: Int?,
        contentId: Int?,
        onlyTransversal: Boolean
    ): Result<List<Activity>> = Result.success(
        listOf(activity)
            .filter { unitId == null || it.unidadId == unitId }
            .filter { contentId == null || it.contenidoId == contentId }
            .filter { !onlyTransversal || it.contenidoId == null }
    )

    override suspend fun getById(id: Int): Result<Activity> =
        error("No se usa en esta prueba")

    override suspend fun create(request: CreateActivityRequest): Result<Activity> =
        error("No se usa en esta prueba")

    override suspend fun update(
        id: Int,
        request: UpdateActivityRequest
    ): Result<Activity> = error("No se usa en esta prueba")

    override suspend fun delete(id: Int): Result<Unit> =
        error("No se usa en esta prueba")
}

private class FakeAttemptRepository(
    private val attempts: List<Attempt>
) : AttemptRepository {

    override suspend fun list(
        studentId: Int?,
        activityId: Int?
    ): Result<List<Attempt>> = Result.success(attempts)

    override suspend fun getById(id: Int): Result<Attempt> =
        error("No se usa en esta prueba")

    override suspend fun start(studentId: Int, activityId: Int): Result<Attempt> =
        error("No se usa en esta prueba")

    override suspend fun complete(id: Int): Result<Attempt> =
        error("No se usa en esta prueba")
}
