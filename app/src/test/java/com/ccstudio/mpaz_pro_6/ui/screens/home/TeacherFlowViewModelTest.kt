package com.ccstudio.mpaz_pro_6.ui.screens.home

import com.ccstudio.mpaz_pro_6.data.enums.ActivityType
import com.ccstudio.mpaz_pro_6.data.model.Activity
import com.ccstudio.mpaz_pro_6.data.model.Assignment
import com.ccstudio.mpaz_pro_6.data.model.AssignmentActivity
import com.ccstudio.mpaz_pro_6.data.model.AssignmentObjective
import com.ccstudio.mpaz_pro_6.data.model.Docencia
import com.ccstudio.mpaz_pro_6.data.model.PedagogicalReport
import com.ccstudio.mpaz_pro_6.data.model.ReportObjectiveDetail
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreateDocenciaRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.CreatePedagogicalReportRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateAssignmentRequest
import com.ccstudio.mpaz_pro_6.data.model.requests.UpdateDocenciaRequest
import com.ccstudio.mpaz_pro_6.data.repository.ActivityRepository
import com.ccstudio.mpaz_pro_6.data.repository.PedagogicalReportRepository
import com.ccstudio.mpaz_pro_6.ui.screens.assignments.AssignmentFormViewModel
import com.ccstudio.mpaz_pro_6.data.repository.AssignmentRepository
import com.ccstudio.mpaz_pro_6.data.repository.DocenciaRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockCourseRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockData
import com.ccstudio.mpaz_pro_6.data.mock.MockSubjectRepository
import com.ccstudio.mpaz_pro_6.data.mock.MockUnitRepository
import com.ccstudio.mpaz_pro_6.ui.screens.subjects.TeacherSubjectDetailViewModel
import com.ccstudio.mpaz_pro_6.ui.screens.subjects.TeacherSubjectListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

/** Pruebas del flujo docente por asignatura y sus docencias. */
class TeacherFlowViewModelTest {

    private val docencias = listOf(
        docencia(id = 1, subjectId = 1, subjectName = "Matemática", courseId = 1, courseName = "6A"),
        docencia(id = 2, subjectId = 2, subjectName = "Ciencias Naturales", courseId = 2, courseName = "6B"),
        docencia(id = 3, subjectId = 1, subjectName = "Matemática", courseId = 2, courseName = "6B")
    )

    private val assignments = listOf(
        assignment(id = 1, courseId = 1, unitId = 1),
        assignment(id = 2, courseId = 2, unitId = 3),
        assignment(id = 3, courseId = 1, unitId = 2)
    )

    @Test
    fun `el Home del docente deriva asignaturas, cursos y actividades de sus docencias`() =
        runBlocking {
            val store = MockData.crearStore()
            val viewModel = HomeTeacherViewModel(
                docenciaRepository = FakeDocenciaRepository(docencias),
                courseRepository = MockCourseRepository(store),
                assignmentRepository = FakeAssignmentRepository(assignments),
                unitRepository = MockUnitRepository(store),
                activityRepository = FakeActivityRepository(
                    listOf(
                        activity(id = 1, unitId = 1),
                        activity(id = 2, unitId = 3),
                        activity(id = 3, unitId = 2)
                    )
                ),
                pedagogicalReportRepository = EmptyPedagogicalReportRepository,
                teacherId = 2,
                dispatcher = Dispatchers.Unconfined
            )

            viewModel.cargar()

            assertNull(viewModel.error)
            assertEquals(2, viewModel.cursos.size)
            assertEquals(listOf("Ciencias Naturales", "Matemática"), viewModel.asignaturas.map { it.nombre })
            assertEquals(listOf("6A", "6B"), viewModel.cursosPorAsignatura[1])
            assertEquals(listOf("6B"), viewModel.cursosPorAsignatura[2])
            assertEquals(2, viewModel.unidadesPorAsignatura[1])
            assertEquals(2, viewModel.unidadesPorAsignatura[2])
            assertEquals(2, viewModel.actividades)
        }

    @Test
    fun `el listado docente solo expone sus asignaturas y los cursos de cada una`() = runBlocking {
        val store = MockData.crearStore()
        val viewModel = TeacherSubjectListViewModel(
            docenciaRepository = FakeDocenciaRepository(docencias),
            unitRepository = MockUnitRepository(store),
            teacherId = 2,
            dispatcher = Dispatchers.Unconfined
        )

        viewModel.cargar()

        assertNull(viewModel.error)
        assertEquals(2, viewModel.asignaturas.size)
        assertEquals(listOf("6A", "6B"), viewModel.cursosPorAsignatura[1])
        assertEquals(2, viewModel.estadisticasPorAsignatura[1]?.total)
    }

    @Test
    fun `crear una asignación limita cursos y unidades a las docencias`() = runBlocking {
        val store = MockData.crearStore()
        val assignmentRepository = FakeAssignmentRepository(emptyList())
        val viewModel = AssignmentFormViewModel(
            assignmentRepository = assignmentRepository,
            courseRepository = MockCourseRepository(store),
            unitRepository = MockUnitRepository(store),
            docenciaRepository = FakeDocenciaRepository(docencias),
            assignmentId = null,
            teacherId = 2,
            dispatcher = Dispatchers.Unconfined
        )

        viewModel.cargar()
        assertEquals(2, viewModel.courses.size)

        viewModel.onCursoIdChange(1)
        assertEquals(listOf(1, 3), viewModel.units.map { it.id })

        viewModel.onUnidadIdChange(1)
        var saved = false
        viewModel.guardar { saved = true }

        assertTrue(saved)
        assertEquals(1, assignmentRepository.lastCreated?.cursoId)
        assertEquals(1, assignmentRepository.lastCreated?.unidadId)
    }

    @Test
    fun `el detalle docente filtra las unidades por curso asignado`() = runBlocking {
        val store = MockData.crearStore()
        val viewModel = TeacherSubjectDetailViewModel(
            docenciaRepository = FakeDocenciaRepository(docencias),
            subjectRepository = MockSubjectRepository(store),
            courseRepository = MockCourseRepository(store),
            unitRepository = MockUnitRepository(store),
            assignmentRepository = FakeAssignmentRepository(assignments),
            teacherId = 2,
            subjectId = 1,
            dispatcher = Dispatchers.Unconfined
        )

        viewModel.cargar()

        assertEquals(2, viewModel.cursos.size)
        assertEquals(listOf(1, 3), viewModel.unidades.map { it.id })

        viewModel.seleccionarCurso(1)
        assertEquals(listOf(1), viewModel.unidades.map { it.id })

        viewModel.seleccionarCurso(2)
        assertEquals(listOf(3), viewModel.unidades.map { it.id })

        viewModel.seleccionarCurso(null)
        assertEquals(2, viewModel.unidades.size)
    }

    private fun docencia(
        id: Int,
        subjectId: Int,
        subjectName: String,
        courseId: Int,
        courseName: String
    ) = Docencia(
        id = id,
        docenteId = 2,
        docenteNombre = "Docente",
        cursoId = courseId,
        cursoNombre = courseName,
        asignaturaId = subjectId,
        asignaturaNombre = subjectName
    )

    private fun assignment(id: Int, courseId: Int, unitId: Int) = Assignment(
        id = id,
        cursoId = courseId,
        unidadId = unitId,
        maxIntentos = 1,
        pruebaFinalHabilitada = false,
        fecha = Instant.parse("2026-04-06T08:00:00Z")
    )

    private fun activity(id: Int, unitId: Int) = Activity(
        id = id,
        unidadId = unitId,
        titulo = "Actividad $id",
        tipo = ActivityType.ACTIVITY,
        orden = 1
    )
}

/** Docencias fijas para las pruebas; las escrituras no se usan. */
private class FakeDocenciaRepository(
    private val docencias: List<Docencia>
) : DocenciaRepository {

    override suspend fun list(docenteId: Int?): Result<List<Docencia>> =
        Result.success(docencias.filter { docenteId == null || it.docenteId == docenteId })

    override suspend fun getById(id: Int): Result<Docencia> =
        docencias.firstOrNull { it.id == id }
            ?.let { Result.success(it) }
            ?: Result.failure(IllegalArgumentException("Docencia inexistente"))

    override suspend fun create(request: CreateDocenciaRequest): Result<Docencia> =
        error("No se usa en esta prueba")

    override suspend fun update(id: Int, request: UpdateDocenciaRequest): Result<Docencia> =
        error("No se usa en esta prueba")

    override suspend fun delete(id: Int): Result<Unit> =
        error("No se usa en esta prueba")
}

/** Asignaciones fijas; solo se usa la lectura del listado. */
private class FakeAssignmentRepository(
    private val assignments: List<Assignment>
) : AssignmentRepository {

    var lastCreated: CreateAssignmentRequest? = null
        private set

    override suspend fun list(courseId: Int?, unitId: Int?): Result<List<Assignment>> =
        Result.success(
            assignments
                .filter { courseId == null || it.cursoId == courseId }
                .filter { unitId == null || it.unidadId == unitId }
        )

    override suspend fun getById(id: Int): Result<Assignment> =
        error("No se usa en esta prueba")

    override suspend fun create(request: CreateAssignmentRequest): Result<Assignment> {
        lastCreated = request
        return Result.success(
            Assignment(
                id = (assignments.maxOfOrNull { it.id } ?: 0) + 1,
                cursoId = request.cursoId,
                unidadId = request.unidadId,
                maxIntentos = request.maxIntentos ?: 1,
                pruebaFinalHabilitada = request.pruebaFinalHabilitada ?: false,
                fecha = request.fecha
            )
        )
    }

    override suspend fun update(id: Int, request: UpdateAssignmentRequest): Result<Assignment> =
        error("No se usa en esta prueba")

    override suspend fun delete(id: Int): Result<Unit> =
        error("No se usa en esta prueba")

    override suspend fun listObjectives(assignmentId: Int): Result<List<AssignmentObjective>> =
        error("No se usa en esta prueba")

    override suspend fun addObjective(
        assignmentId: Int,
        learningObjectiveId: Int
    ): Result<AssignmentObjective> = error("No se usa en esta prueba")

    override suspend fun removeObjective(
        assignmentId: Int,
        learningObjectiveId: Int
    ): Result<Unit> = error("No se usa en esta prueba")

    override suspend fun listActivities(assignmentId: Int): Result<List<AssignmentActivity>> =
        error("No se usa en esta prueba")

    override suspend fun addActivity(
        assignmentId: Int,
        activityId: Int
    ): Result<AssignmentActivity> = error("No se usa en esta prueba")

    override suspend fun removeActivity(assignmentId: Int, activityId: Int): Result<Unit> =
        error("No se usa en esta prueba")
}

/** Repositorio de informes vacío para las pruebas del Home. */
private object EmptyPedagogicalReportRepository : PedagogicalReportRepository {

    override suspend fun list(
        studentId: Int?,
        unitId: Int?
    ): Result<List<PedagogicalReport>> = Result.success(emptyList())

    override suspend fun getById(id: Int): Result<PedagogicalReport> =
        error("No se usa en esta prueba")

    override suspend fun generate(
        request: CreatePedagogicalReportRequest
    ): Result<PedagogicalReport> = error("No se usa en esta prueba")

    override suspend fun validate(id: Int): Result<PedagogicalReport> =
        error("No se usa en esta prueba")

    override suspend fun listObjectiveDetails(
        reportId: Int
    ): Result<List<ReportObjectiveDetail>> = error("No se usa en esta prueba")
}

/** Actividades fijas; solo se usa el listado. */
private class FakeActivityRepository(
    private val activities: List<Activity>
) : ActivityRepository {

    override suspend fun list(
        unitId: Int?,
        contentId: Int?,
        onlyTransversal: Boolean
    ): Result<List<Activity>> =
        Result.success(
            activities
                .filter { unitId == null || it.unidadId == unitId }
                .filter { contentId == null || it.contenidoId == contentId }
                .filter { !onlyTransversal || it.contenidoId == null }
        )

    override suspend fun getById(id: Int): Result<Activity> =
        error("No se usa en esta prueba")

    override suspend fun create(request: com.ccstudio.mpaz_pro_6.data.model.requests.CreateActivityRequest): Result<Activity> =
        error("No se usa en esta prueba")

    override suspend fun update(
        id: Int,
        request: com.ccstudio.mpaz_pro_6.data.model.requests.UpdateActivityRequest
    ): Result<Activity> = error("No se usa en esta prueba")

    override suspend fun delete(id: Int): Result<Unit> =
        error("No se usa en esta prueba")
}
